1、参数设置

还是这么一套参数结构。主要是为了和前面做一些对比。看看我们自己模拟出来的GC场景是否符合上一节课我们对日志的解读。

-Xmx256M -XX:+UseG1GC -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -XX:MaxGCPauseMillis=20 -Xloggc:gc.log

 

2、模拟代码

public class YGCDemo1 {

  // 5% * 256 = 12M左右是第一次发生YGC的时机

  // 因为256MB / 2048 小于 1 ，根据G1的处理，会把一个region定为1M，

  public static void main(String[] args) throws Exception{

 

​    // 设定一个对象为256KB，如果太大，会直接走大对象分配无法模拟YGC

​    byte[] data = new byte[1024 * 256];

​    for(int i = 0; i< 36; i++){

​      data = new byte[1024 * 256];

​    }

 

​    data = new byte[1024 * 256];

  }

}

我们来解析一下代码：

 

（1）首先第一段，我们创建了一个data字节数组，大小是1024 * 256 = 256KB。因为我们知道对象如果超过了region大小的话，是一定会被当作大对象，单独存储的。所以我们要把这个对象的大小设置的合理一点。而对于我们这一套参数，256MB的堆内存，通过计算不难发现，region的大小是1MB，因为256/2048很显然小于1MB，然后会自动调整，调整成1MB大小。那么我们对象就设置成256KB大小。这样就能正常分配到新生代region中。![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/70448000_1641819688.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

（2）然后，我们又搞了一个循环，循环中不断去创建新的数组。并且循环的次数我们设置在了36，为什么设置成36呢？原因是，在G1启动的时候，会初始化新生代的大小，新生代的大小占总空间的5%，也就是256 * 5% 约等于12，也就是说，我们用掉了12个region左右的时候，就会触发第一次YGC。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/71541200_1641819688.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

（3）在触发了YGC之后

data这个变量还在，所以还是会有一个256KB的字节数组存活，加上一些堆内存中存在的一些未知对象也可能会存活。那么在第一次回收之后，应该会有不到1M的对象最终存活下来，进入survivor区域。

 

3、代码运行相关的GC日志

Java HotSpot(TM) 64-Bit Server VM (25.162-b12) for windows-amd64 JRE (1.8.0_162-b12), built on Dec 19 2017 20:00:03 by "java_re" with MS VC++ 10.0 (VS2010)
Memory: 4k page, physical 16629016k(8810728k free), swap 20823320k(5806028k free)
CommandLine flags: -XX:InitialHeapSize=266064256 -XX:MaxGCPauseMillis=20 -XX:MaxHeapSize=268435456 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseG1GC -XX:-UseLargePagesIndividualAllocation
0.155: [GC pause (G1 Evacuation Pause) (young), 0.0017840 secs]
  [Parallel Time: 1.1 ms, GC Workers: 8]
   [GC Worker Start (ms): Min: 155.4, Avg: 155.5, Max: 155.6, Diff: 0.1]
   [Ext Root Scanning (ms): Min: 0.1, Avg: 0.3, Max: 0.5, Diff: 0.3, Sum: 2.2]
   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
     [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]
   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
   [Object Copy (ms): Min: 0.4, Avg: 0.6, Max: 0.7, Diff: 0.3, Sum: 5.0]
   [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.3]
     [Termination Attempts: Min: 1, Avg: 9.6, Max: 12, Diff: 11, Sum: 77]
   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.0, Sum: 0.3]
   [GC Worker Total (ms): Min: 0.9, Avg: 1.0, Max: 1.0, Diff: 0.1, Sum: 7.9]
   [GC Worker End (ms): Min: 156.5, Avg: 156.5, Max: 156.5, Diff: 0.0]
  [Code Root Fixup: 0.0 ms]
  [Code Root Purge: 0.0 ms]
  [Clear CT: 0.3 ms]
  [Other: 0.4 ms]
   [Choose CSet: 0.0 ms]
   [Ref Proc: 0.1 ms]
   [Ref Enq: 0.0 ms]
   [Redirty Cards: 0.2 ms]
   [Humongous Register: 0.0 ms]
   [Humongous Reclaim: 0.0 ms]
   [Free CSet: 0.0 ms]
  [Eden: 12.0M(12.0M)->0.0B(10.0M) Survivors: 0.0B->2048.0K Heap: 12.0M(254.0M)->1120.0K(254.0M)]
 [Times: user=0.00 sys=0.00, real=0.00 secs]
Heap
 garbage-first heap  total 260096K, used 3168K [0x00000000f0000000, 0x00000000f01007f0, 0x0000000100000000)
 region size 1024K, 5 young (5120K), 2 survivors (2048K)
 Metaspace    used 3451K, capacity 4496K, committed 4864K, reserved 1056768K
 class space  used 376K, capacity 388K, committed 512K, reserved 1048576K

 

// 这些内容是我们设置的启动参数，其中需要注意的是，最大停顿时间，和堆内存的大小。再此我们不再赘述。

Java HotSpot(TM) 64-Bit Server VM (25.162-b12) for windows-amd64 JRE (1.8.0_162-b12), built on Dec 19 2017 20:00:03 by "java_re" with MS VC++ 10.0 (VS2010)
Memory: 4k page, physical 16629016k(8810728k free), swap 20823320k(5806028k free)
CommandLine flags: -XX:InitialHeapSize=266064256 -XX:MaxGCPauseMillis=20 -XX:MaxHeapSize=268435456 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseG1GC -XX:-UseLargePagesIndividualAllocation

 

// 接下来我们看这里的内容，GC线程还是8个，本次GC造成的停顿时间是0.00178，很明显是在20ms内的。

0.155: [GC pause (G1 Evacuation Pause) (young), 0.0017840 secs]
  [Parallel Time: 1.1 ms, GC Workers: 8]

 

// 这一段日志，可以看出来，在GC前，Eden区使用了12MB的内存，也就是12个region，在本次GC之后，Survivor从GC开始前的0使用量，到了2048K，也就是survivor使用了2个region（注意啊，这里不是说s区就有这么多的对象，而是s区，总共使用了2个region）。堆内存的使用，从12M最后变为了1120K，大概1M左右

[Eden: 12.0M(12.0M)->0.0B(10.0M) Survivors: 0.0B->2048.0K Heap: 12.0M(254.0M)->1120.0K(254.0M)]
 [Times: user=0.00 sys=0.00, real=0.00 secs]

 

//堆内存的最终使用状态是这样的，总共256MB，使用了3MB左右，region size是1MB，5个region给了新生代，两个region给了survivor

Heap
 garbage-first heap  total 260096K, used 3168K [0x00000000f0000000, 0x00000000f01007f0, 0x0000000100000000)
 region size 1024K, 5 young (5120K), 2 survivors (2048K)
 Metaspace    used 3451K, capacity 4496K, committed 4864K, reserved 1056768K
 class space  used 376K, capacity 388K, committed 512K, reserved 1048576K

 

4、接下来把代码改造一下

public class YGCDemo1 {

  private static final ArrayList<String> strs = new ArrayList<String>();

 

  // 5% * 256 = 12M左右

  // 因为256MB / 2048 小于 1 ，根据G1的处理，会把一个region定为1M，

  public static void main(String[] args) throws Exception{

 

​    // 设定一个对象为256KB，如果太大，会直接走大对象分配无法模拟YGC

​    byte[] data = new byte[1024 * 256];

​    for(int i = 0; i< 36; i++){

​      data = new byte[1024 * 256];

​    }

​    // 创建n个对象，加入到一个list中，不回收这些数组。

​    ArrayList list = new ArrayList();

​    for(int i = 0; i<= 36; i++){

​      byte[] data2 = new byte[1024 * 256];

​      // 一部分字节数组加入到list中

​      if(i % 3 == 0){

​        list.add(data2);

​      }

​    }

  }

}

这段代码可以触发2次YGC，因为我们会保留一些存活对象，所以第二次GC的时候，region的使用情况会发生一些变化。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/71835300_1641819688.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

注意看代码的变化位置。我们加了一个循环，并且每3个数组，会有一个数组加入到list中不会被回收。

5、观察gc 日志

Java HotSpot(TM) 64-Bit Server VM (25.162-b12) for windows-amd64 JRE (1.8.0_162-b12), built on Dec 19 2017 20:00:03 by "java_re" with MS VC++ 10.0 (VS2010)

Memory: 4k page, physical 16629016k(8725244k free), swap 20823320k(5618500k free)

CommandLine flags: -XX:InitialHeapSize=266064256 -XX:MaxGCPauseMillis=20 -XX:MaxHeapSize=268435456 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseG1GC -XX:-UseLargePagesIndividualAllocation

0.150: [GC pause (G1 Evacuation Pause) (young), 0.0018467 secs]

  [Parallel Time: 1.1 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 149.9, Avg: 150.1, Max: 150.9, Diff: 0.9]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.2, Max: 0.4, Diff: 0.4, Sum: 1.7]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.0, Avg: 0.5, Max: 0.6, Diff: 0.6, Sum: 3.9]

   [Termination (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.5]

​     [Termination Attempts: Min: 1, Avg: 7.4, Max: 12, Diff: 11, Sum: 59]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.3]

   [GC Worker Total (ms): Min: 0.0, Avg: 0.8, Max: 1.0, Diff: 0.9, Sum: 6.4]

   [GC Worker End (ms): Min: 150.9, Avg: 150.9, Max: 150.9, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.2 ms]

  [Other: 0.5 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.2 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.2 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

  [Eden: 12.0M(12.0M)->0.0B(10.0M) Survivors: 0.0B->2048.0K Heap: 12.0M(254.0M)->1120.0K(254.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.153: [GC pause (G1 Evacuation Pause) (young), 0.0014333 secs]

  [Parallel Time: 1.0 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 152.9, Avg: 152.9, Max: 153.2, Diff: 0.3]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 1.1]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.5, Avg: 0.6, Max: 0.7, Diff: 0.1, Sum: 5.1]

   [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

​     [Termination Attempts: Min: 1, Avg: 1.9, Max: 4, Diff: 3, Sum: 15]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [GC Worker Total (ms): Min: 0.6, Avg: 0.8, Max: 0.9, Diff: 0.3, Sum: 6.4]

   [GC Worker End (ms): Min: 153.7, Avg: 153.7, Max: 153.7, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Other: 0.3 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.2 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

  [Eden: 10.0M(10.0M)->0.0B(10.0M) Survivors: 2048.0K->2048.0K Heap: 11.1M(254.0M)->3666.5K(254.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

Heap

 garbage-first heap  total 260096K, used 7762K [0x00000000f0000000, 0x00000000f01007f0, 0x0000000100000000)

 region size 1024K, 7 young (7168K), 2 survivors (2048K)

 Metaspace    used 3451K, capacity 4496K, committed 4864K, reserved 1056768K

 class space  used 376K, capacity 388K, committed 512K, reserved 1048576K

 

很明显是发生了两次GC的，并且两次GC之后Eden区的region数量并没有发生变化。