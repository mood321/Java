1、参数设置

对于YGC来说，我们只是要模拟YGC的整个过程，并且要打印出YGC整个过程的一些GC细节，所以我们设置这么一套参数：

-Xmx256M -XX:+UseG1GC -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -XX:MaxGCPauseMillis=20 -Xloggc:gc.log

 

2、沿用上一节写的简单demo代码

public class TLABDemo {

  public static void main(String[] args) throws Exception{

​    for (int n = 0 ;;n++){

​      // 外层循环100次，每次内层循环创建10个字符串

​      for (int i = 0; i<100; i++){

​        // 内层循环10次

​        for (int j = 0; j < 10; j++){

​           strs.add(new String("NO." + j + "Str" ));

​        }

​      }

​      // 无限循环，每次循环间隔0.1s

​      System.out.println("第" + n + "次循环");

​      Thread.sleep(100);

​    }

  }

}

 

这段代码的意思其实也很简单，无限循环，去创建字符串儿，放到一个list去，每次循环会执行1000次创建字符串，并把字符串加入到list的操作。通过这段代码，我们先看看TLAB相关的GC日志信息。

 

3、代码及YGC时机分析

首先，这个代码我们没有设置一个占用多少空间新生代占用多少空间发生GC没设置。因为所以具体什么时候会发生YGC我们暂时不确定。

不过我们可以简单的思考YGC的发生时机，尤其是第一次，第一次发生YGC的时候，是不是应该是256 * 5%的年轻代使用量的时候。大概是这个值，当然，会比这个值略小一点，因为新生代第一次GC的时候survivor区是没有存活对象的，并且，因为一些描述信息的存在，堆内存会有一部分被其他的一些描述信息占用，不会256MB全部都给对象分配来使用。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/4711500_1641819595.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

程序运行一段时间之后，大量字符串对象会填满eden区，此时就会触发一次YGC。

4、YGC相关的运行日志

可以看到，G1的GC日志看起来非常复杂，中间有非常复杂的处理步骤。比如，roots扫描，RS扫描等等操作。每一步的操作都会有各自的耗时时长。接下来我们一步一步分析这个日志。

 

// 这一部分是我们设置的一些参数信息此处就不再赘述。

Java HotSpot(TM) 64-Bit Server VM (25.162-b12) for windows-amd64 JRE (1.8.0_162-b12), built on Dec 19 2017 20:00:03 by "java_re" with MS VC++ 10.0 (VS2010)

Memory: 4k page, physical 16629016k(8490740k free), swap 20823320k(6937244k free)

CommandLine flags: -XX:InitialHeapSize=266064256 -XX:MaxGCPauseMillis=20 -XX:MaxHeapSize=268435456 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseG1GC -XX:-UseLargePagesIndividualAllocation

// 从这里开始，出现了第一次GC，大概是在程序启动6.193s之后，停顿时间是0.0048641秒，也就是4.8ms左右

6.193: [GC pause (G1 Evacuation Pause) (young), 0.0048641 secs]

// 这里很明显看到GC线程并行工作的时间是2.9ms，并且使用了8个GCWorkers线程。因为我的电脑是8核的，这个大家不同的电脑，会有不同的结果。因为我们没有设置GC的GCParallelThreads线程数量的值

  [Parallel Time: 2.9 ms, GC Workers: 8]

// 这些是线程启动，GC Roots扫描，RSet扫描，对象拷贝等等操作的一些耗时情况。具体的一些细节，我们下节课分析GC日志的详细细节的时候，再来解析。

   [GC Worker Start (ms): Min: 6192.9, Avg: 6193.1, Max: 6193.6, Diff: 0.7]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.2, Max: 0.4, Diff: 0.4, Sum: 1.7]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

   [Object Copy (ms): Min: 1.9, Avg: 2.2, Max: 2.3, Diff: 0.4, Sum: 17.8]

   [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

​     [Termination Attempts: Min: 16, Avg: 20.3, Max: 24, Diff: 8, Sum: 162]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.0, Sum: 0.3]

   [GC Worker Total (ms): Min: 1.9, Avg: 2.5, Max: 2.6, Diff: 0.7, Sum: 19.9]

   [GC Worker End (ms): Min: 6195.5, Avg: 6195.5, Max: 6195.6, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.9 ms]

  [Other: 1.0 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.5 ms]

   [Ref Enq: 0.0 ms]

// 卡表更新

   [Redirty Cards: 0.5 ms]

// 大对象相关的操作时间

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

// 在第一次GC之后，Eden区从使用12M，到使用0M，从总大小12M，到总大小10M，发生了动态变化。Survivor区从0变为了2048K，也就是本次GC之后有2048K的对象存活下来，堆内存从12M的使用量，降为了4424K的使用量。说明本次回收的效果还是不错的，基本上回收了百分之九十以上的垃圾。

  [Eden: 12.0M(12.0M)->0.0B(10.0M) Survivors: 0.0B->2048.0K Heap: 12.0M(254.0M)->4424.0K(254.0M)]

 [Times: user=0.00 sys=0.00, real=0.01 secs]

// 第二次GC发生的时机，对于第二次GC，我们着重关注一下Eden survivor 的大小，看看它是否有变化。

17.261: [GC pause (G1 Evacuation Pause) (young), 0.0087213 secs]

  [Parallel Time: 7.7 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 17261.5, Avg: 17261.5, Max: 17261.6, Diff: 0.2]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.3, Diff: 0.3, Sum: 1.0]

   [Update RS (ms): Min: 0.0, Avg: 1.0, Max: 2.9, Diff: 2.9, Sum: 7.8]

​     [Processed Buffers: Min: 0, Avg: 1.3, Max: 3, Diff: 3, Sum: 10]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.1]

   [Object Copy (ms): Min: 4.6, Avg: 6.5, Max: 7.5, Diff: 2.9, Sum: 51.8]

   [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.3]

​     [Termination Attempts: Min: 1, Avg: 2.6, Max: 5, Diff: 4, Sum: 21]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [GC Worker Total (ms): Min: 7.5, Avg: 7.6, Max: 7.7, Diff: 0.1, Sum: 61.1]

   [GC Worker End (ms): Min: 17269.1, Avg: 17269.1, Max: 17269.1, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.3 ms]

  [Other: 0.7 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.5 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

// 从这里我们并没有发现新生代的变化，说明此时还没有进行新生代的调整，但是有一个现像，新生代使用从10MB降为了2048K，而堆内存，从14.9降为了12.9，也就是说，本次只回收了2M左右的对象，那么肯定是有一部分对象进入了老年代分区的。

  [Eden: 10.0M(10.0M)->0.0B(10.0M) Survivors: 2048.0K->2048.0K Heap: 14.9M(254.0M)->12.9M(254.0M)]

 [Times: user=0.09 sys=0.02, real=0.01 secs]

38.020: [GC pause (G1 Evacuation Pause) (young), 0.0101857 secs]

  [Parallel Time: 8.9 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 38019.9, Avg: 38020.0, Max: 38020.1, Diff: 0.1]

   [Ext Root Scanning (ms): Min: 0.1, Avg: 0.3, Max: 1.3, Diff: 1.2, Sum: 2.2]

   [Update RS (ms): Min: 0.0, Avg: 1.4, Max: 1.7, Diff: 1.7, Sum: 10.8]

​     [Processed Buffers: Min: 0, Avg: 1.9, Max: 3, Diff: 3, Sum: 15]

   [Scan RS (ms): Min: 0.0, Avg: 0.1, Max: 0.5, Diff: 0.5, Sum: 0.7]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 6.9, Avg: 7.0, Max: 7.1, Diff: 0.2, Sum: 56.4]

   [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Termination Attempts: Min: 1, Avg: 28.9, Max: 43, Diff: 42, Sum: 231]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

   [GC Worker Total (ms): Min: 8.7, Avg: 8.8, Max: 8.8, Diff: 0.1, Sum: 70.3]

   [GC Worker End (ms): Min: 38028.7, Avg: 38028.7, Max: 38028.7, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.3 ms]

  [Other: 1.0 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.1 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.7 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

  [Eden: 10.0M(10.0M)->0.0B(10.0M) Survivors: 2048.0K->2048.0K Heap: 25.2M(254.0M)->27.9M(254.0M)]

 [Times: user=0.00 sys=0.00, real=0.01 secs]

58.728: [GC pause (G1 Evacuation Pause) (young), 0.0103349 secs]

  [Parallel Time: 8.3 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 58727.8, Avg: 58727.9, Max: 58728.0, Diff: 0.2]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.8]

   [Update RS (ms): Min: 0.7, Avg: 1.0, Max: 1.5, Diff: 0.8, Sum: 8.3]

​     [Processed Buffers: Min: 1, Avg: 1.5, Max: 2, Diff: 1, Sum: 12]

   [Scan RS (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.7]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 6.3, Avg: 6.7, Max: 6.9, Diff: 0.6, Sum: 53.7]

   [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Termination Attempts: Min: 1, Avg: 11.6, Max: 19, Diff: 18, Sum: 93]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

   [GC Worker Total (ms): Min: 7.8, Avg: 8.0, Max: 8.0, Diff: 0.2, Sum: 63.6]

   [GC Worker End (ms): Min: 58735.8, Avg: 58735.8, Max: 58735.8, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.7 ms]

  [Other: 1.2 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.5 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.7 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

  [Eden: 10.0M(10.0M)->0.0B(10.0M) Survivors: 2048.0K->2048.0K Heap: 40.0M(254.0M)->42.0M(254.0M)]

 [Times: user=0.00 sys=0.00, real=0.01 secs]

 

从多次GC的结果来看，新生代GC的情况相对比较稳定，每次的时长都在1ms左右（除了第一次时间比较长）。并且由于我们的代码是没有对象置为null的操作的，所以基本上所有的对象都能存活下来。晋升到老年代。