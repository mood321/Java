 void work(uint worker_id) {

  if (worker_id >= _n_workers) return; // no work needed this round

 

  _g1h->g1_policy()->phase_times()->record_time_secs(G1GCPhaseTimes::GCWorkerStart, worker_id, os::elapsedTime());

 

  {

   ResourceMark rm;

   HandleMark  hm;

 

   ReferenceProcessor*       rp = _g1h->ref_processor_stw();

 

   G1ParScanThreadState       pss(_g1h, worker_id, rp);

   G1ParScanHeapEvacFailureClosure evac_failure_cl(_g1h, &pss, rp);

 

   pss.set_evac_failure_closure(&evac_failure_cl);

 

   bool only_young = _g1h->g1_policy()->gcs_are_young();

 

   // Non-IM young GC.

   G1ParCopyClosure<G1BarrierNone, G1MarkNone>       scan_only_root_cl(_g1h, &pss, rp);

   G1CLDClosure<G1MarkNone>                 scan_only_cld_cl(&scan_only_root_cl,

​                                        only_young, // Only process dirty klasses.

​                                        false);   // No need to claim CLDs.

   // IM young GC.

   //   Strong roots closures.

   G1ParCopyClosure<G1BarrierNone, G1MarkFromRoot>     scan_mark_root_cl(_g1h, &pss, rp);

   G1CLDClosure<G1MarkFromRoot>               scan_mark_cld_cl(&scan_mark_root_cl,

​                                        false, // Process all klasses.

​                                        true); // Need to claim CLDs.

   //   Weak roots closures.

   G1ParCopyClosure<G1BarrierNone, G1MarkPromotedFromRoot> scan_mark_weak_root_cl(_g1h, &pss, rp);

   G1CLDClosure<G1MarkPromotedFromRoot>           scan_mark_weak_cld_cl(&scan_mark_weak_root_cl,

​                                          false, // Process all klasses.

​                                          true); // Need to claim CLDs.

 

   OopClosure* strong_root_cl;

   OopClosure* weak_root_cl;

   CLDClosure* strong_cld_cl;

   CLDClosure* weak_cld_cl;

 

   bool trace_metadata = false;

 

   if (_g1h->g1_policy()->during_initial_mark_pause()) {

​    // We also need to mark copied objects.

​    strong_root_cl = &scan_mark_root_cl;

​    strong_cld_cl = &scan_mark_cld_cl;

​    if (ClassUnloadingWithConcurrentMark) {

​     weak_root_cl = &scan_mark_weak_root_cl;

​     weak_cld_cl = &scan_mark_weak_cld_cl;

​     trace_metadata = true;

​    } else {

​     weak_root_cl = &scan_mark_root_cl;

​     weak_cld_cl = &scan_mark_cld_cl;

​    }

   } else {

​    strong_root_cl = &scan_only_root_cl;

​    weak_root_cl  = &scan_only_root_cl;

​    strong_cld_cl = &scan_only_cld_cl;

​    weak_cld_cl   = &scan_only_cld_cl;

   }

 

   pss.start_strong_roots();

 

// 处理gc roots内容，本节课我们要关注的重点方法，接下来我们来看看他的具体实现

   _root_processor->evacuate_roots(strong_root_cl,

​                   weak_root_cl,

​                   strong_cld_cl,

​                   weak_cld_cl,

​                   trace_metadata,

​                   worker_id);

 

   G1ParPushHeapRSClosure push_heap_rs_cl(_g1h, &pss);

// 处理Rset的内容

   _root_processor->scan_remembered_sets(&push_heap_rs_cl,

​                      weak_root_cl,

​                      worker_id);

   pss.end_strong_roots();

 

   {

​    double start = os::elapsedTime();

// 执行对象复制操作

​    G1ParEvacuateFollowersClosure evac(_g1h, &pss, _queues, &_terminator);

​    evac.do_void();

​    double elapsed_sec = os::elapsedTime() - start;

​    double term_sec = pss.term_time();

​    _g1h->g1_policy()->phase_times()->add_time_secs(G1GCPhaseTimes::ObjCopy, worker_id, elapsed_sec - term_sec);

​    _g1h->g1_policy()->phase_times()->record_time_secs(G1GCPhaseTimes::Termination, worker_id, term_sec);

​    _g1h->g1_policy()->phase_times()->record_thread_work_item(G1GCPhaseTimes::Termination, worker_id, pss.term_attempts());

   }

   _g1h->g1_policy()->record_thread_age_table(pss.age_table());

   _g1h->update_surviving_young_words(pss.surviving_young_words()+1);

 

   if (ParallelGCVerbose) {

​    MutexLocker x(stats_lock());

​    pss.print_termination_stats(worker_id);

   }

 

   assert(pss.queue_is_empty(), "should be empty");

 

   // Close the inner scope so that the ResourceMark and HandleMark

   // destructors are executed here and are included as part of the

   // "GC Worker Time".

}

// 记录时间

  _g1h->g1_policy()->phase_times()->record_time_secs(G1GCPhaseTimes::GCWorkerEnd, worker_id, os::elapsedTime());

 }

};

 

2、 _root_processor中的evacuate_roots()方法

这个方法在g1RootProcessor.cpp中，从这个_root_processor名字也可以看的出来。

在这个方法里面，我们需要关注的是，到底有哪些根对象需要处理。

void G1RootProcessor::evacuate_roots(OopClosure* scan_non_heap_roots,

​                   OopClosure* scan_non_heap_weak_roots,

​                   CLDClosure* scan_strong_clds,

​                   CLDClosure* scan_weak_clds,

​                   bool trace_metadata,

​                   uint worker_i) {

 // First scan the shared roots.首先扫描共享roots

// 这边儿在扫描开始前，先记录一下时间

 double ext_roots_start = os::elapsedTime();

 G1GCPhaseTimes* phase_times = _g1h->g1_policy()->phase_times();

 BufferingOopClosure buf_scan_non_heap_roots(scan_non_heap_roots);

 BufferingOopClosure buf_scan_non_heap_weak_roots(scan_non_heap_weak_roots);

 

 OopClosure* const weak_roots = &buf_scan_non_heap_weak_roots;

 OopClosure* const strong_roots = &buf_scan_non_heap_roots;

 

 // CodeBlobClosures are not interoperable with BufferingOopClosures

 G1CodeBlobClosure root_code_blobs(scan_non_heap_roots);

// 先处理java 的roots

 process_java_roots(strong_roots,

​           trace_metadata ? scan_strong_clds : NULL,

​           scan_strong_clds,

​           trace_metadata ? NULL : scan_weak_clds,

​           &root_code_blobs,

​           phase_times,

​           worker_i);

 

 // This is the point where this worker thread will not find more strong CLDs/nmethods.

 // Report this so G1 can synchronize the strong and weak CLDs/nmethods processing.

// 这一步主要是为了做一个记录通知。

 if (trace_metadata) {

  worker_has_discovered_all_strong_classes();

 }

// 处理JVM里面的根

 process_vm_roots(strong_roots, weak_roots, phase_times, worker_i);

// 处理string table相关的根

 process_string_table_roots(weak_roots, phase_times, worker_i);

 {

  // Now the CM ref_processor roots.

  G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::CMRefRoots, worker_i);

  if (!_process_strong_tasks.is_task_claimed(G1RP_PS_refProcessor_oops_do)) {

   // We need to treat the discovered reference lists of the

   // concurrent mark ref processor as roots and keep entries

   // (which are added by the marking threads) on them live

   // until they can be processed at the end of marking.

   _g1h->ref_processor_cm()->weak_oops_do(&buf_scan_non_heap_roots);

  }

 }

// 这里还是处理一些跟踪信息

 if (trace_metadata) {

  {

   G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::WaitForStrongCLD, worker_i);

   // Barrier to make sure all workers passed

   // the strong CLD and strong nmethods phases.

   wait_until_all_strong_classes_discovered();

  }

 

  // Now take the complement of the strong CLDs.

  G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::WeakCLDRoots, worker_i);

  ClassLoaderDataGraph::roots_cld_do(NULL, scan_weak_clds);

 } else {

  phase_times->record_time_secs(G1GCPhaseTimes::WaitForStrongCLD, worker_i, 0.0);

  phase_times->record_time_secs(G1GCPhaseTimes::WeakCLDRoots, worker_i, 0.0);

 }

 

 // Finish up any enqueued closure apps (attributed as object copy time).

 buf_scan_non_heap_roots.done();

 buf_scan_non_heap_weak_roots.done();

 

 double obj_copy_time_sec = buf_scan_non_heap_roots.closure_app_seconds()

   \+ buf_scan_non_heap_weak_roots.closure_app_seconds();

 

 phase_times->record_time_secs(G1GCPhaseTimes::ObjCopy, worker_i, obj_copy_time_sec);

 

 double ext_root_time_sec = os::elapsedTime() - ext_roots_start - obj_copy_time_sec;

 

 phase_times->record_time_secs(G1GCPhaseTimes::ExtRootScan, worker_i, ext_root_time_sec);

 

 // During conc marking we have to filter the per-thread SATB buffers

 // to make sure we remove any oops into the CSet (which will show up

 // as implicitly live).

 {

  G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::SATBFiltering, worker_i);

  if (!_process_strong_tasks.is_task_claimed(G1RP_PS_filter_satb_buffers) && _g1h->mark_in_progress()) {

   JavaThread::satb_mark_queue_set().filter_thread_buffers();

  }

 }

 

// 最后等待所有任务结束

 _process_strong_tasks.all_tasks_completed();

}

 

3、接下来就是遍历这些Java根处理线程和VM根处理线程，从而获取最终的执行结果

我们以Java根处理为例：

void G1RootProcessor::process_java_roots(OopClosure* strong_roots,

​                     CLDClosure* thread_stack_clds,

​                     CLDClosure* strong_clds,

​                     CLDClosure* weak_clds,

​                     CodeBlobClosure* strong_code,

​                     G1GCPhaseTimes* phase_times,

​                     uint worker_i) {

 assert(thread_stack_clds == NULL || weak_clds == NULL, "There is overlap between those, only one may be set");

 // Iterating over the CLDG and the Threads are done early to allow us to

 // first process the strong CLDs and nmethods and then, after a barrier,

 // let the thread process the weak CLDs and nmethods.

 {

  G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::CLDGRoots, worker_i);

  if (!_process_strong_tasks.is_task_claimed(G1RP_PS_ClassLoaderDataGraph_oops_do)) {

   ClassLoaderDataGraph::roots_cld_do(strong_clds, weak_clds);

  }

 }

 

 {

G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::ThreadRoots, worker_i);

// 这个地方就是Threads类里面的线程遍历方法，会追踪这个线程的执行情况

  Threads::possibly_parallel_oops_do(strong_roots, thread_stack_clds, strong_code);

 }

}

 

src\share\vm\runtime\thread.cpp的遍历方法：

其中包含了Java线程的遍历和VM线程的遍历。

void Threads::possibly_parallel_oops_do(OopClosure* f, CLDClosure* cld_f, CodeBlobClosure* cf) {

 // Introduce a mechanism allowing parallel threads to claim threads as

 // root groups.  Overhead should be small enough to use all the time,

 // even in sequential code.

 SharedHeap* sh = SharedHeap::heap();

 // Cannot yet substitute active_workers for n_par_threads

 // because of G1CollectedHeap::verify() use of

 // SharedHeap::process_roots().  n_par_threads == 0 will

 // turn off parallelism in process_roots while active_workers

 // is being used for parallelism elsewhere.

 bool is_par = sh->n_par_threads() > 0;

 assert(!is_par ||

​     (SharedHeap::heap()->n_par_threads() ==

​     SharedHeap::heap()->workers()->active_workers()), "Mismatch");

 int cp = SharedHeap::heap()->strong_roots_parity();

// java线程的遍历 

ALL_JAVA_THREADS(p) {

if (p->claim_oops_do(is_par, cp)) {

// 最终执行的方法

   p->oops_do(f, cld_f, cf);

  }

 }

 VMThread* vmt = VMThread::vm_thread();

// JVM线程的遍历

 if (vmt->claim_oops_do(is_par, cp)) {

// 最终执行的方法

  vmt->oops_do(f, cld_f, cf);

 }

}

 

 

// src\share\vm\runtime\thread.cpp

// JavaThread的遍历代码

void JavaThread::oops_do(OopClosure* f, CLDClosure* cld_f, CodeBlobClosure* cf) {

 // Verify that the deferred card marks have been flushed.

 assert(deferred_card_mark().is_empty(), "Should be empty during GC");

 

 // The ThreadProfiler oops_do is done from FlatProfiler::oops_do

 // since there may be more than one thread using each ThreadProfiler.

 

 // Traverse the GCHandles

 Thread::oops_do(f, cld_f, cf);

 

 assert( (!has_last_Java_frame() && java_call_counter() == 0) ||

​     (has_last_Java_frame() && java_call_counter() > 0), "wrong java_sp info!");

// 注意这个if块儿内的代码，这个里面包含了很多Java相关的扫描内容

 if (has_last_Java_frame()) {

  // Record JavaThread to GC thread

  RememberProcessedThread rpt(this);

 

  // Traverse the privileged stack 遍历特权栈，主要是指Java安全功能相关的类，暂时不需要关注

  if (_privileged_stack_top != NULL) {

   _privileged_stack_top->oops_do(f);

  }

 

  // traverse the registered growable array 遍历注册的全局数组

  if (_array_for_gc != NULL) {

   for (int index = 0; index < _array_for_gc->length(); index++) {

​    f->do_oop(_array_for_gc->adr_at(index));

   }

  }

 

  // Traverse the monitor chunks 遍历Monitor块儿

  for (MonitorChunk* chunk = monitor_chunks(); chunk != NULL; chunk = chunk->next()) {

   chunk->oops_do(f);

  }

 

  // Traverse the execution stack 遍历栈这个栈就是我们所理解的虚拟机栈

  for(StackFrameStream fst(this); !fst.is_done(); fst.next()) {

   fst.current()->oops_do(f, cld_f, cf, fst.register_map());

  }

 }

 

 // callee_target is never live across a gc point so NULL it here should

 // it still contain a methdOop.

 

 set_callee_target(NULL);

 

 assert(vframe_array_head() == NULL, "deopt in progress at a safepoint!");

 // If we have deferred set_locals there might be oops waiting to be

 // written

 GrowableArray<jvmtiDeferredLocalVariableSet*>* list = deferred_locals();

 if (list != NULL) {

  for (int i = 0; i < list->length(); i++) {

   list->at(i)->oops_do(f);

  }

 }

 

 // Traverse instance variables at the end since the GC may be moving things

 // around using this function

 f->do_oop((oop*) &_threadObj);

 f->do_oop((oop*) &_vm_result);

 f->do_oop((oop*) &_exception_oop);

 f->do_oop((oop*) &_pending_async_exception);

 

 if (jvmti_thread_state() != NULL) {

  jvmti_thread_state()->oops_do(f);

 }

}

 

所有的任务都执行完毕之后，就代表所有的gc roots已经被找到，此时就需要对这些gc roots对象进行一些复制操作。把他们复制到新的分区，复制到survivor分区，或者是直接复制到老年代分区。

 

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/99123400_1647078013.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)