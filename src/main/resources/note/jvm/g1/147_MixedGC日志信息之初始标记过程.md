1、参数设置

具体参数如下：

-Xmx256M -XX:+UseG1GC -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -XX:+UnlockExperimentalVMOptions -XX:G1LogLevel=finest -XX:MaxGCPauseMillis=20 -Xloggc:gc.log

 

在参数上没有什么特别的变化，主要是添加了两个参数，一个是开启实验选项，用于设置日志的输出级别。日志的输出级别设置为-XX:G1LogLevel=finest

 

2、模拟代码

private static final ArrayList list = new ArrayList();

  // 5% * 256 = 12M左右

  // 因为256MB / 2048 小于 1 ，根据G1的处理，会把一个region定为1M，

  public static void main(String[] args) throws Exception{

 

​    while(true){

​      // 设定一个对象为256KB，如果太大，会直接走大对象分配无法模拟YGC

​      byte[] data = new byte[1024 * 256];

 

​      for(int i = 0; i< 36; i++){

​        data = new byte[1024 * 256];

​      }

​      // 创建n个对象，加入到一个list中，不回收这些数组。

​      for(int i = 0; i<= 36; i++){

​        byte[] data2 = new byte[1024 * 256];

​        // 一部分字节数组加入到list中

​        if(i % 2 == 0){

​          list.add(data2);

​          byte[] bigData1 = new byte[1024 * 512];

​        }

​      }

​      Thread.sleep(1000);

​    }

  }

我们来解析一下代码：

（1）首先第一段，我们创建了一个data字节数组，大小是1024 * 256 = 256KB。因为我们知道对象如果超过了region大小的话，是一定会被当作大对象，单独存储的。所以我们要把这个对象的大小设置的合理一点。而对于我们这一套参数，256MB的堆内存，通过计算不难发现，region的大小是1MB，因为256/2048很显然小于1MB，然后会自动调整，调整成1MB大小。那么我们对象就设置成256KB大小。这样就能正常分配到新生代region中。![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/83801900_1644390736.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

（2）然后，我们又搞了一个循环，循环中不断去创建新的数组。并且循环的次数我们设置在了36，为什么设置成36呢？原因是，在G1启动的时候，会初始化新生代的大小，新生代的大小占总空间的5%，也就是256 * 5% 约等于12，也就是说，我们用掉了12个region左右的时候，就会触发第一次YGC。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/84052500_1644390736.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

（3）在触发了YGC之后

data这个变量还在，所以还是会有一个256KB的字节数组存活，加上一些堆内存中存在的一些未知对象也可能会存活。那么在第一次回收之后，应该会有不到1M的对象最终存活下来，进入survivor区域。

（4）比较大的改变在这里：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/84170200_1644390736.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

代码运行中，这个地方模拟出来，每2次执行就搞点存活对象到数组中。这样做是为了更快的触发MixedGC，因为MixedGC的触发条件是老年代使用了45%，并且，垃圾清理的过程并不一定是跟在Initial Mark之后的，也就是说，即使触发了初始标记过程，也不一定会启动MixedGC。所以，我们要让堆内存里面的存活对象始终在增涨，保证它一定会触发MixedGC

 

 

3、代码运行相关的GC日志

我们从这里来看这个日志：这里触发了一次YGC，并且时间并不长。

0.177: [GC pause (G1 Evacuation Pause) (young), 0.0017408 secs]

  [Parallel Time: 1.2 ms, GC Workers: 8]// 启动了8个线程去执行，并且并行的执行时间1.2ms

   [GC Worker Start (ms): 177.2 177.2 177.2 177.2 177.2 177.6 178.3 178.3

​    Min: 177.2, Avg: 177.5, Max: 178.3, Diff: 1.1]

   [Ext Root Scanning (ms): 0.2 0.4 0.4 0.3 0.2 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.2, Max: 0.4, Diff: 0.4, Sum: 1.5]

​     [Thread Roots (ms): 0.1 0.0 0.4 0.1 0.0 0.0 0.0 0.0

​      Min: 0.0, Avg: 0.1, Max: 0.4, Diff: 0.4, Sum: 0.6]

​     [StringTable Roots (ms): 0.2 0.0 0.0 0.2 0.2 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.5]

​     [Universe Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [JNI Handles Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [ObjectSynchronizer Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [FlatProfiler Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Management Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SystemDictionary Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CLDG Roots (ms): 0.0 0.4 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.4, Diff: 0.4, Sum: 0.4]

​     [JVMTI Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CodeCache Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CM RefProcessor Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Wait For Strong CLD (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Weak CLD Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SATB Filtering (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Update RS (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: 0 0 0 0 0 0 0 0

​     Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]

   [Scan RS (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): 0.7 0.6 0.6 0.7 0.7 0.6 0.0 0.0

​    Min: 0.0, Avg: 0.5, Max: 0.7, Diff: 0.7, Sum: 4.0]

   [Termination (ms): 0.1 0.1 0.1 0.1 0.1 0.1 0.0 0.0

​    Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.5]

​     [Termination Attempts: 8 1 8 8 5 4 1 1

​     Min: 1, Avg: 4.5, Max: 8, Diff: 7, Sum: 36]

   [GC Worker Other (ms): 0.1 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.3]

   [GC Worker Total (ms): 1.1 1.1 1.1 1.1 1.1 0.7 0.0 0.0

​    Min: 0.0, Avg: 0.8, Max: 1.1, Diff: 1.1, Sum: 6.4]

   [GC Worker End (ms): 178.3 178.3 178.3 178.3 178.3 178.3 178.3 178.3

​    Min: 178.3, Avg: 178.3, Max: 178.3, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Other: 0.4 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.2 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

​     [Parallel Redirty: 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Redirtied Cards: 18 0 0 0 0 0 0 0

​     Min: 0, Avg: 2.3, Max: 18, Diff: 18, Sum: 18]

   [Humongous Register: 0.0 ms]

​     [Humongous Total: 0]

​     [Humongous Candidate: 0]

   [Humongous Reclaim: 0.0 ms]

​     [Humongous Reclaimed: 0]

   [Free CSet: 0.0 ms]

​     [Young Free CSet: 0.0 ms]

​     [Non-Young Free CSet: 0.0 ms]

// 这里可以看出，这次GC是一次非常常规的YGC，GC之前，S区没有，GC之后，下一次S区是1个，也就是1M的空间。堆内存在回收前是6144K垃圾，回收后，1048K垃圾

  [Eden: 6144.0K(6144.0K)->0.0B(5120.0K) Survivors: 0.0B->1024.0K Heap: 6144.0K(128.0M)->1048.0K(128.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

// 第二次回收，我们着重看一下对象的增涨速度

0.180: [GC pause (G1 Evacuation Pause) (young), 0.0016041 secs]

  [Parallel Time: 1.1 ms, GC Workers: 8]

   [GC Worker Start (ms): 180.3 180.3 180.3 180.3 180.3 180.3 180.6 181.2

​    Min: 180.3, Avg: 180.4, Max: 181.2, Diff: 1.0]

   [Ext Root Scanning (ms): 0.3 0.4 0.2 0.2 0.2 0.2 0.0 0.0

​    Min: 0.0, Avg: 0.2, Max: 0.4, Diff: 0.4, Sum: 1.4]

​     [Thread Roots (ms): 0.0 0.4 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.1, Max: 0.4, Diff: 0.4, Sum: 0.4]

​     [StringTable Roots (ms): 0.0 0.0 0.2 0.2 0.2 0.2 0.0 0.0

​     Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.6]

​     [Universe Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [JNI Handles Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [ObjectSynchronizer Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [FlatProfiler Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Management Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SystemDictionary Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CLDG Roots (ms): 0.3 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.3, Diff: 0.3, Sum: 0.3]

​     [JVMTI Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CodeCache Roots (ms): 0.0 0.0 0.0 0.1 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.2]

​     [CM RefProcessor Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Wait For Strong CLD (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Weak CLD Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SATB Filtering (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Update RS (ms): 0.0 0.0 0.0 0.1 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.1]

​     [Processed Buffers: 0 0 0 1 1 1 0 0

​     Min: 0, Avg: 0.4, Max: 1, Diff: 1, Sum: 3]

   [Scan RS (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): 0.6 0.5 0.7 0.6 0.7 0.7 0.6 0.0

​    Min: 0.0, Avg: 0.6, Max: 0.7, Diff: 0.7, Sum: 4.4]

   [Termination (ms): 0.1 0.1 0.0 0.1 0.0 0.1 0.1 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.4]

​     [Termination Attempts: 1 7 6 13 9 9 7 1

​     Min: 1, Avg: 6.6, Max: 13, Diff: 12, Sum: 53]

   [GC Worker Other (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

   [GC Worker Total (ms): 1.0 1.0 1.0 1.0 0.9 0.9 0.7 0.0

​    Min: 0.0, Avg: 0.8, Max: 1.0, Diff: 1.0, Sum: 6.5]

   [GC Worker End (ms): 181.3 181.3 181.3 181.3 181.2 181.3 181.3 181.2

​    Min: 181.2, Avg: 181.3, Max: 181.3, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Other: 0.4 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.2 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

​     [Parallel Redirty: 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Redirtied Cards: 3 0 0 0 0 0 0 0

​     Min: 0, Avg: 0.4, Max: 3, Diff: 3, Sum: 3]

   [Humongous Register: 0.0 ms]

​     [Humongous Total: 0]

​     [Humongous Candidate: 0]

   [Humongous Reclaim: 0.0 ms]

​     [Humongous Reclaimed: 0]

   [Free CSet: 0.0 ms]

​     [Young Free CSet: 0.0 ms]

​     [Non-Young Free CSet: 0.0 ms]

// 可以看到这里，对象增涨的速度其实并不快，并且，eden区的个数，调大了一个

  [Eden: 5120.0K(5120.0K)->0.0B(6144.0K) Survivors: 1024.0K->1024.0K Heap: 6168.0K(128.0M)->1427.9K(128.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

这中间，我省略了很多次普通的YGC的过程没有放出来，因为太多了。

……

……

// 到了这次YGC，依然是很正常的YGC，结果还是我们所能理解的常规结果。

7.289: [GC pause (G1 Evacuation Pause) (young), 0.0029624 secs]

  [Parallel Time: 1.8 ms, GC Workers: 8]

   [GC Worker Start (ms): 7288.9 7288.9 7288.9 7289.0 7289.0 7289.0 7289.0 7289.0

​    Min: 7288.9, Avg: 7288.9, Max: 7289.0, Diff: 0.1]

   [Ext Root Scanning (ms): 0.2 0.2 0.1 0.1 0.1 0.1 0.1 0.1

​    Min: 0.1, Avg: 0.1, Max: 0.2, Diff: 0.1, Sum: 0.8]

​     [Thread Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [StringTable Roots (ms): 0.1 0.1 0.1 0.1 0.1 0.1 0.1 0.1

​     Min: 0.1, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.7]

​     [Universe Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [JNI Handles Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [ObjectSynchronizer Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [FlatProfiler Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Management Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​      Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SystemDictionary Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CLDG Roots (ms): 0.1 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.1]

​     [JVMTI Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CodeCache Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

​     [CM RefProcessor Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Wait For Strong CLD (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Weak CLD Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SATB Filtering (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Update RS (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: 0 0 0 0 1 1 0 0

​     Min: 0, Avg: 0.3, Max: 1, Diff: 1, Sum: 2]

   [Scan RS (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): 1.1 1.2 1.0 1.2 1.2 1.0 1.1 1.0

​    Min: 1.0, Avg: 1.1, Max: 1.2, Diff: 0.2, Sum: 8.8]

   [Termination (ms): 0.2 0.0 0.2 0.1 0.0 0.2 0.1 0.2

​    Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.9]

​     [Termination Attempts: 2 1 2 2 4 2 2 1

​     Min: 1, Avg: 2.0, Max: 4, Diff: 3, Sum: 16]

   [GC Worker Other (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [GC Worker Total (ms): 1.4 1.4 1.3 1.3 1.3 1.3 1.3 1.3

​    Min: 1.3, Avg: 1.3, Max: 1.4, Diff: 0.1, Sum: 10.6]

   [GC Worker End (ms): 7290.3 7290.3 7290.3 7290.3 7290.3 7290.3 7290.3 7290.3

​    Min: 7290.3, Avg: 7290.3, Max: 7290.3, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.5 ms]

  [Other: 0.6 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.2 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.3 ms]

​     [Parallel Redirty: 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Redirtied Cards: 3 0 0 0 0 0 0 0

​     Min: 0, Avg: 0.4, Max: 3, Diff: 3, Sum: 3]

   [Humongous Register: 0.0 ms]

​     [Humongous Total: 22]

​     [Humongous Candidate: 22]

   [Humongous Reclaim: 0.0 ms]

​     [Humongous Reclaimed: 22]

   [Free CSet: 0.0 ms]

​     [Young Free CSet: 0.0 ms]

​     [Non-Young Free CSet: 0.0 ms]

// 注意看这里，此时堆内存的使用已经非常多了，Eden区在回收以后，是0B，S区扩展到了6M左右，堆内存总使用量47.5M，此时这个使用量其实已经比较高了。

[Eden: 38.0M(38.0M)->0.0B(57.0M) Survivors: 5120.0K->6144.0K Heap: 89.3M(128.0M)->47.5M(128.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

// 我们在看这里，发现已经开启了初始标记。

8.309: [GC pause (G1 Humongous Allocation) (young) (initial-mark), 0.0014780 secs]

  [Parallel Time: 1.0 ms, GC Workers: 8]

   [GC Worker Start (ms): 8308.9 8308.9 8308.9 8308.9 8308.9 8308.9 8309.0 8309.1

​    Min: 8308.9, Avg: 8309.0, Max: 8309.1, Diff: 0.2]

   [Ext Root Scanning (ms): 0.2 0.2 0.2 0.2 0.2 0.1 0.1 0.0

​    Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 1.1]

​     [Thread Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

​     [StringTable Roots (ms): 0.0 0.1 0.1 0.1 0.1 0.1 0.1 0.0

​     Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.7]

​     [Universe Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [JNI Handles Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [ObjectSynchronizer Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [FlatProfiler Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Management Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SystemDictionary Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CLDG Roots (ms): 0.1 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.1]

​     [JVMTI Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CodeCache Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

​     [CM RefProcessor Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Wait For Strong CLD (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.2]

​     [Weak CLD Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SATB Filtering (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Update RS (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

​     [Processed Buffers: 0 0 1 0 0 1 0 1

​     Min: 0, Avg: 0.4, Max: 1, Diff: 1, Sum: 3]

   [Scan RS (ms):  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): 0.8 0.7 0.7 0.7 0.7 0.7 0.8 0.7

​    Min: 0.7, Avg: 0.7, Max: 0.8, Diff: 0.1, Sum: 5.9]

   [Termination (ms): 0.0 0.1 0.1 0.1 0.1 0.1 0.0 0.1

​    Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.5]

​     [Termination Attempts: 1 1 1 1 1 1 1 1

​     Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]

   [GC Worker Other (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

   [GC Worker Total (ms): 1.0 1.0 1.0 1.0 1.0 1.0 0.9 0.8

​    Min: 0.8, Avg: 1.0, Max: 1.0, Diff: 0.2, Sum: 7.7]

   [GC Worker End (ms): 8309.9 8309.9 8309.9 8309.9 8309.9 8309.9 8309.9 8309.9

​    Min: 8309.9, Avg: 8309.9, Max: 8309.9, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Other: 0.3 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.1 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

​     [Parallel Redirty: 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Redirtied Cards: 2 0 0 0 0 0 0 0

​     Min: 0, Avg: 0.3, Max: 2, Diff: 2, Sum: 2]

   [Humongous Register: 0.0 ms]

​     [Humongous Total: 16]

​     [Humongous Candidate: 16]

   [Humongous Reclaim: 0.0 ms]

​     [Humongous Reclaimed: 16]

   [Free CSet: 0.0 ms]

​     [Young Free CSet: 0.0 ms]

​     [Non-Young Free CSet: 0.0 ms]

// 这次回收之后的数据，从数据中可以看到，确实是遵循45%这个规则来的。垃圾回收之后，即将分配的对象+老年代的所有对象占用的空间达到了45%的时候，就会触发mixed gc也就是开启并发标记。

 [Eden: 24.0M(57.0M)->0.0B(49.0M) Survivors: 6144.0K->6144.0K Heap: 78.8M(128.0M)->53.0M(128.0M)]

 [Times: user=0.09 sys=0.00, real=0.00 secs]

8.311: [GC concurrent-root-region-scan-start]

8.311: [GC concurrent-root-region-scan-end, 0.0000467 secs]

8.311: [GC concurrent-mark-start]

8.313: [GC concurrent-mark-end, 0.0018556 secs]

8.313: [GC remark 8.313: [Finalize Marking, 0.0001905 secs] 8.313: [GC ref-proc, 0.0003040 secs] 8.313: [Unloading 8.313: [System Dictionary Unloading, 0.0000130 secs] 8.313: [Parallel Unloading, 0.0005763 secs] 8.314: [Deallocate Metadata, 0.0000144 secs], 0.0006416 secs], 0.0014267 secs]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

8.314: [GC cleanup 75M->75M(128M), 0.0016325 secs]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

9.331: [GC pause (G1 Evacuation Pause) (young) (to-space exhausted), 0.0008772 secs]

  [Parallel Time: 0.4 ms, GC Workers: 8]

   [GC Worker Start (ms): 9331.0 9331.0 9331.0 9331.0 9331.0 9331.0 9331.0 9331.0

​    Min: 9331.0, Avg: 9331.0, Max: 9331.0, Diff: 0.1]

   [Ext Root Scanning (ms): 0.1 0.1 0.1 0.1 0.1 0.1 0.1 0.1

​    Min: 0.1, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 1.0]

​     [Thread Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [StringTable Roots (ms): 0.1 0.1 0.1 0.1 0.1 0.1 0.1 0.1

​     Min: 0.1, Avg: 0.1, Max: 0.1, Diff: 0.0, Sum: 0.8]

​     [Universe Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [JNI Handles Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [ObjectSynchronizer Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [FlatProfiler Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Management Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SystemDictionary Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CLDG Roots (ms): 0.1 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.1]

​     [JVMTI Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CodeCache Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

​     [CM RefProcessor Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Wait For Strong CLD (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Weak CLD Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SATB Filtering (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Update RS (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

​     [Processed Buffers: 1 1 0 0 0 0 1 1

​     Min: 0, Avg: 0.5, Max: 1, Diff: 1, Sum: 4]

   [Scan RS (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): 0.2 0.1 0.1 0.1 0.1 0.1 0.1 0.1

​    Min: 0.1, Avg: 0.1, Max: 0.2, Diff: 0.1, Sum: 0.9]

   [Termination (ms): 0.0 0.0 0.1 0.1 0.1 0.0 0.1 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.4]

​     [Termination Attempts: 1 1 1 1 1 1 1 1

​     Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]

   [GC Worker Other (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

   [GC Worker Total (ms): 0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.3

​    Min: 0.3, Avg: 0.3, Max: 0.3, Diff: 0.1, Sum: 2.4]

   [GC Worker End (ms): 9331.3 9331.3 9331.3 9331.3 9331.3 9331.3 9331.3 9331.3

​    Min: 9331.3, Avg: 9331.3, Max: 9331.3, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Other: 0.4 ms]

   [Evacuation Failure: 0.1 ms]

​     [Recalculate Used: 0.0 ms]

​     [Remove Self Forwards: 0.1 ms]

​     [Restore RemSet: 0.0 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.1 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

​     [Parallel Redirty: 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​      Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Redirtied Cards: 6 0 0 0 0 0 0 0

​     Min: 0, Avg: 0.8, Max: 6, Diff: 6, Sum: 6]

   [Humongous Register: 0.0 ms]

​     [Humongous Total: 37]

​     [Humongous Candidate: 37]

   [Humongous Reclaim: 0.0 ms]

​     [Humongous Reclaimed: 37]

   [Free CSet: 0.0 ms]

​     [Young Free CSet: 0.0 ms]

​     [Non-Young Free CSet: 0.0 ms]

  [Eden: 37.0M(49.0M)->0.0B(6144.0K) Survivors: 6144.0K->0.0B Heap: 108.5M(128.0M)->78.8M(128.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

10.347: [GC pause (G1 Evacuation Pause) (mixed), 0.0017163 secs]

  [Parallel Time: 1.2 ms, GC Workers: 8]

   [GC Worker Start (ms): 10347.3 10347.3 10347.3 10347.3 10347.3 10347.3 10347.5 10347.6

​    Min: 10347.3, Avg: 10347.4, Max: 10347.6, Diff: 0.3]

   [Ext Root Scanning (ms): 0.2 0.2 0.2 0.2 0.2 0.1 0.0 0.0

​    Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 1.0]

​     [Thread Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [StringTable Roots (ms): 0.1 0.2 0.1 0.1 0.2 0.1 0.0 0.0

​     Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.8]

​     [Universe Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [JNI Handles Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [ObjectSynchronizer Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [FlatProfiler Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Management Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SystemDictionary Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CLDG Roots (ms): 0.1 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.1]

​     [JVMTI Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CodeCache Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.1 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.2]

​     [CM RefProcessor Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Wait For Strong CLD (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Weak CLD Roots (ms): 0.0 0.0  0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SATB Filtering (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Update RS (ms): 0.0 0.0 0.0 0.0 0.0 0.1 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.2]

​     [Processed Buffers: 2 1 1 0 0 1 0 0

​     Min: 0, Avg: 0.6, Max: 2, Diff: 2, Sum: 5]

   [Scan RS (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): 0.8 0.9 0.9 0.8 0.8 0.8 0.8 0.7

​    Min: 0.7, Avg: 0.8, Max: 0.9, Diff: 0.2, Sum: 6.4]

   [Termination (ms): 0.1 0.1 0.0 0.1 0.1 0.1 0.1 0.1

​    Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.7]

​     [Termination Attempts: 1 1 1 1 1 1 1 1

​     Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]

   [GC Worker Other (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [GC Worker Total (ms): 1.1 1.1 1.1 1.1 1.1 1.1 0.9 0.8

​    Min: 0.8, Avg: 1.0, Max: 1.1, Diff: 0.3, Sum: 8.4]

   [GC Worker End (ms): 10348.4 10348.4 10348.4 10348.4 10348.4 10348.4 10348.4 10348.4

​    Min: 10348.4, Avg: 10348.4, Max: 10348.4, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Other: 0.4 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.2 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

​     [Parallel Redirty: 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Redirtied Cards: 4 0 0 0 0 0 0 0

​     Min: 0, Avg: 0.5, Max: 4, Diff: 4, Sum: 4]

   [Humongous Register: 0.0 ms]

​     [Humongous Total: 1]

​     [Humongous Candidate: 1]

   [Humongous Reclaim: 0.0 ms]

​     [Humongous Reclaimed: 1]

   [Free CSet: 0.0 ms]

​     [Young Free CSet: 0.0 ms]

​     [Non-Young Free CSet: 0.0 ms]

  [Eden: 6144.0K(6144.0K)->0.0B(5120.0K) Survivors: 0.0B->1024.0K Heap: 85.3M(128.0M)->78.8M(128.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

10.350: [GC pause (G1 Evacuation Pause) (mixed), 0.0015104 secs]

  [Parallel Time: 0.8 ms, GC Workers: 8]

   [GC Worker Start (ms): 10350.1 10350.2 10350.2 10350.2 10350.2 10350.2 10350.2 10350.3

​    Min: 10350.1, Avg: 10350.2, Max: 10350.3, Diff: 0.2]

   [Ext Root Scanning (ms): 0.2 0.2 0.2 0.2 0.1 0.1 0.1 0.0

​    Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 1.0]

​     [Thread Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [StringTable Roots (ms): 0.1 0.1 0.1 0.1 0.1 0.1 0.1 0.0

​     Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.9]

​     [Universe Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [JNI Handles Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [ObjectSynchronizer Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [FlatProfiler Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Management Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SystemDictionary Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CLDG Roots (ms): 0.1 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.1]

​     [JVMTI Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CodeCache Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.2]

​     [CM RefProcessor Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Wait For Strong CLD (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Weak CLD Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SATB Filtering (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Update RS (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.2]

​     [Processed Buffers: 1 1 0 0 1 1 1 0

​     Min: 0, Avg: 0.6, Max: 1, Diff: 1, Sum: 5]

   [Scan RS (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): 0.5 0.4 0.5 0.5 0.6 0.5 0.5 0.5

​    Min: 0.4, Avg: 0.5, Max: 0.6, Diff: 0.1, Sum: 4.0]

   [Termination (ms): 0.1 0.1 0.1 0.1 0.0 0.1 0.1 0.1

​    Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.6]

​     [Termination Attempts: 1 1 1 1 1 1 1 1

​     Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]

   [GC Worker Other (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [GC Worker Total (ms): 0.8 0.8 0.8 0.8 0.7 0.7 0.7 0.6

​    Min: 0.6, Avg: 0.7, Max: 0.8, Diff: 0.2, Sum: 5.9]

   [GC Worker End (ms): 10350.9 10350.9 10350.9 10350.9 10350.9 10350.9 10350.9 10350.9

​    Min: 10350.9, Avg: 10350.9, Max: 10350.9, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Other: 0.5 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.3 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

​     [Parallel Redirty: 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Redirtied Cards: 4 0 0 0 0 0 0 0

​     Min: 0, Avg: 0.5, Max: 4, Diff: 4, Sum: 4]

   [Humongous Register: 0.0 ms]

​     [Humongous Total: 0]

​     [Humongous Candidate: 0]

   [Humongous Reclaim: 0.0 ms]

​     [Humongous Reclaimed: 0]

   [Free CSet: 0.0 ms]

​     [Young Free CSet: 0.0 ms]

​     [Non-Young Free CSet: 0.0 ms]

  [Eden: 5120.0K(5120.0K)->0.0B(27.0M) Survivors: 1024.0K->1024.0K Heap: 83.8M(128.0M)->79.5M(128.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

10.353: [GC pause (G1 Humongous Allocation) (young) (initial-mark), 0.0009719 secs]

  [Parallel Time: 0.4 ms, GC Workers: 8]

   [GC Worker Start (ms): 10352.6 10352.6 10352.7 10352.7 10352.7 10352.7 10352.7 10352.7

​    Min: 10352.6, Avg: 10352.7, Max: 10352.7, Diff: 0.1]

   [Ext Root Scanning (ms): 0.2 0.1 0.1 0.1 0.1 0.1 0.1 0.1

​    Min: 0.1, Avg: 0.1, Max: 0.2, Diff: 0.1, Sum: 1.1]

​     [Thread Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [StringTable Roots (ms): 0.1 0.1 0.1 0.1 0.1 0.1 0.1 0.1

​     Min: 0.1, Avg: 0.1, Max: 0.1, Diff: 0.0, Sum: 0.9]

​     [Universe Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [JNI Handles Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [ObjectSynchronizer Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [FlatProfiler Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Management Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SystemDictionary Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CLDG Roots (ms): 0.1 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.1]

​     [JVMTI Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CodeCache Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

​     [CM RefProcessor Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Wait For Strong CLD (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Weak CLD Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [SATB Filtering (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Update RS (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

​     [Processed Buffers: 1 0 1 0 1 0 1 1

​     Min: 0, Avg: 0.6, Max: 1, Diff: 1, Sum: 5]

   [Scan RS (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): 0.0 0.0 0.0 0.1 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.1]

   [Termination (ms): 0.0 0.1 0.1 0.0 0.1 0.1 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.4]

​     [Termination Attempts: 1 1 1 1 1 1 1 1

​     Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]

   [GC Worker Other (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​    Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [GC Worker Total (ms): 0.3 0.2 0.2 0.2 0.2 0.2 0.2 0.2

​    Min: 0.2, Avg: 0.2, Max: 0.3, Diff: 0.1, Sum: 1.7]

   [GC Worker End (ms): 10352.9 10352.9 10352.9 10352.9 10352.9 10352.9 10352.9 10352.9

​    Min: 10352.9, Avg: 10352.9, Max: 10352.9, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.2 ms]

  [Other: 0.4 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.2 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

​     [Parallel Redirty: 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Redirtied Cards: 1 0 0 0 0 0 0 0

​     Min: 0, Avg: 0.1, Max: 1, Diff: 1, Sum: 1]

   [Humongous Register: 0.0 ms]

​     [Humongous Total: 0]

​     [Humongous Candidate: 0]

​    [Humongous Reclaim: 0.0 ms]

​     [Humongous Reclaimed: 0]

   [Free CSet: 0.0 ms]

​     [Young Free CSet: 0.0 ms]

​     [Non-Young Free CSet: 0.0 ms]

  [Eden: 3072.0K(27.0M)->0.0B(28.0M) Survivors: 1024.0K->1024.0K Heap: 82.0M(128.0M)->79.8M(128.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

10.354: [GC concurrent-root-region-scan-start]

10.354: [GC concurrent-root-region-scan-end, 0.0000481 secs]

10.354: [GC concurrent-mark-start]

10.356: [GC concurrent-mark-end, 0.0019111 secs]

10.356: [GC remark 10.356: [Finalize Marking, 0.0002410 secs] 10.357: [GC ref-proc, 0.0002787 secs] 10.357: [Unloading 10.357: [System Dictionary Unloading, 0.0000089 secs] 10.357: [Parallel Unloading, 0.0005675 secs] 10.357: [Deallocate Metadata, 0.0000145 secs], 0.0006318 secs], 0.0014424 secs]

 [Times: user=0.01 sys=0.00, real=0.00 secs]

10.358: [GC cleanup 101M->101M(128M), 0.0006501 secs]

 [Times: user=0.00 sys=0.00, real=0.00 secs]