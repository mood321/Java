1、回顾 

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/69211300_1647778621.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

2、本节内容

在把所有的gc roots扫描，复制到对应的分区，并完成对feild的处理之后，此时就需要去处理RSet。

对于RSet的处理，有两步操作：

（1）更新RSet

（2）扫描RSet，找到并处理RSet引用的对象

首先我们来看看Rset更新的操作，RSet更新的时机前面也已经介绍过了。实际上就是发生了GC的时候，要更新RSet。不发生的时候，其实也要更新RSet，只不过不一定会触发refine线程来更新，只有在DCQ的数量达到了green - yellow之间的时候，才会开始激活refine线程

 

我们回顾前面的代码，在gc roots处理之后，会处理RSet，代码入口是scan_remebered_sets我们就一起来看看这个扫描到底处理了哪些逻辑。

 

还是在这个类中：

src\share\vm\gc_implementation\g1\g1RootProcessor.cpp

 

void G1RootProcessor::scan_remembered_sets(G1ParPushHeapRSClosure* scan_rs,

​                      OopClosure* scan_non_heap_weak_roots,

​                      uint worker_i) {

 G1GCPhaseTimes* phase_times = _g1h->g1_policy()->phase_times();

 G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::CodeCacheRoots, worker_i);

 

 // Now scan the complement of the collection set.

 G1CodeBlobClosure scavenge_cs_nmethods(scan_non_heap_weak_roots);

 

// 最终的扫描入口是在这个方法

 _g1h->g1_rem_set()->oops_into_collection_set_do(scan_rs, &scavenge_cs_nmethods, worker_i);

}

在下面这个类中：

src\share\vm\gc_implementation\g1\g1RemSet.cpp

void G1RemSet::oops_into_collection_set_do(G1ParPushHeapRSClosure* oc,

​                      CodeBlobClosure* code_root_cl,

​                      uint worker_i) {

\#if CARD_REPEAT_HISTO

 ct_freq_update_histo_and_reset();

\#endif

 

 // We cache the value of 'oc' closure into the appropriate slot in the

 // _cset_rs_update_cl for this worker

 assert(worker_i < n_workers(), "sanity");

 _cset_rs_update_cl[worker_i] = oc;

 

 // A DirtyCardQueue that is used to hold cards containing references

 // that point into the collection set. This DCQ is associated with a

 // special DirtyCardQueueSet (see g1CollectedHeap.hpp).  Under normal

 // circumstances (i.e. the pause successfully completes), these cards

 // are just discarded (there's no need to update the RSets of regions

 // that were in the collection set - after the pause these regions

 // are wholly 'free' of live objects. In the event of an evacuation

 // failure the cards/buffers in this queue set are passed to the

 // DirtyCardQueueSet that is used to manage RSet updates

// 这个地方又重新搞了一个DCQ，这个地方容易混淆，这个DCQ不是程序运行过程中产生的DCQ，而是在GC运行过程中，如果GC失败了，记录下来的需要保留的引用关系

 DirtyCardQueue into_cset_dcq(&_g1->into_cset_dirty_card_queue_set());

 

 assert((ParallelGCThreads > 0) || worker_i == 0, "invariant");

// 这边就是核心的两步，一步是更新RSet，一步是扫描RSet

 updateRS(&into_cset_dcq, worker_i);

 scanRS(oc, code_root_cl, worker_i);

 

 // We now clear the cached values of _cset_rs_update_cl for this worker

 _cset_rs_update_cl[worker_i] = NULL;

}

更新操作的方法在：

src\share\vm\gc_implementation\g1\g1RemSet.cpp

void G1RemSet::updateRS(DirtyCardQueue* into_cset_dcq, uint worker_i) {

 G1GCParPhaseTimesTracker x(_g1p->phase_times(), G1GCPhaseTimes::UpdateRS, worker_i);

 // Apply the given closure to all remaining log entries.

// 使用Closure去把剩下的所有的DCQ日志信息都处理掉。

 RefineRecordRefsIntoCSCardTableEntryClosure into_cset_update_rs_cl(_g1, into_cset_dcq);

// 迭代遍历所有的clousure，处理 DCQ

 _g1->iterate_dirty_card_closure(&into_cset_update_rs_cl, into_cset_dcq, false, worker_i);

}

 

方法位于：

src\share\vm\gc_implementation\g1\g1CollectedHeap.cpp

iterate_dirty_card_closure

void G1CollectedHeap::iterate_dirty_card_closure(CardTableEntryClosure* cl,

​                         DirtyCardQueue* into_cset_dcq,

​                         bool concurrent,

​                         uint worker_i) {

 // Clean cards in the hot card cache

 G1HotCardCache* hot_card_cache = _cg1r->hot_card_cache();

 hot_card_cache->drain(worker_i, g1_rem_set(), into_cset_dcq);

 

// 循环处理DCQS中的所有DCQ

 DirtyCardQueueSet& dcqs = JavaThread::dirty_card_queue_set();

 size_t n_completed_buffers = 0;

 while (dcqs.apply_closure_to_completed_buffer(cl, worker_i, 0, true)) {

  n_completed_buffers++;

 }

 g1_policy()->phase_times()->record_thread_work_item(G1GCPhaseTimes::UpdateRS, worker_i, n_completed_buffers);

 dcqs.clear_n_completed_buffers();

 assert(!dcqs.completed_buffers_exist_dirty(), "Completed buffers exist!");

}

（2）扫描RSet，找到并处理RSet引用的对象

 从这里入口，我们看看它的实现，scanRS(oc, code_root_cl, worker_i);

 

void G1RemSet::scanRS(G1ParPushHeapRSClosure* oc,

​           CodeBlobClosure* code_root_cl,

​           uint worker_i) {

 double rs_time_start = os::elapsedTime();

// 这里很关键，这个方法，其实就是从cset中选择一些分区给当前的这个工作线程worker_i

// 也就是说，每个GC线程都会分配自己的一个region的处理范围。以此来并行处理这些任务，提升处理的效率。 *startRegion就是起始地址，然后使用scanRScl来设置扫描范围，并使用ScanRSClosure来执行扫描任务

 HeapRegion *startRegion = _g1->start_cset_region_for_worker(worker_i);

 ScanRSClosure scanRScl(oc, code_root_cl, worker_i);

 

// 这里执行了两次扫描，我们可以理解为，针对两种不同类型的对象进行扫描。

// 扫描的具体逻辑可以理解为：RSet中记录的所有引用到当前region的对象所在的卡表的地址，然后去扫描这个引用者对象卡表所在的分区，并对卡表所在的那块儿内存进行扫描，卡表默认情况下是512字节（这块儿内存有可能是细粒度，或者粗粒度，如果达到了粗粒度，则有可能会对整个region进行扫描）

因为RSet在运行过程中，可能会有很多中粒度，并且会根据一定的条件触发粗粒度细粒度两种不同的存储结构。所以在扫描的时候，会有不同。

 _g1->collection_set_iterate_from(startRegion, &scanRScl);

 scanRScl.set_try_claimed();

 _g1->collection_set_iterate_from(startRegion, &scanRScl);

 

 double scan_rs_time_sec = (os::elapsedTime() - rs_time_start)

​              \- scanRScl.strong_code_root_scan_time_sec();

 

 assert(_cards_scanned != NULL, "invariant");

 _cards_scanned[worker_i] = scanRScl.cards_done();

 

 _g1p->phase_times()->record_time_secs(G1GCPhaseTimes::ScanRS, worker_i, scan_rs_time_sec);

 _g1p->phase_times()->record_time_secs(G1GCPhaseTimes::CodeRoots, worker_i, scanRScl.strong_code_root_scan_time_sec());

}

 

另外需要说明的一点是，在扫描RSet的过程中，

会扫描所有CSet region 的 RSet，然后找到引用者对象。对于YGC来说就是，找到所有新生代region的RSet，然后扫描这些RSet，根据RSet中存储的引用者地址（老年代中的地址），找到对应的引用者（老年代中的对象），然后把老年代中的这些引用者对象的每一个feild加入到待扫描队列中。