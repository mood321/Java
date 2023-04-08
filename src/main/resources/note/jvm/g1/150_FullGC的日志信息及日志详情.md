1、参数设置

-XX:InitialHeapSize=36M -XX:MaxHeapSize=36M -XX:G1HeapRegionSize=4M -XX:MaxGCPauseMillis=20 -XX:+PrintGC -XX:+PrintGCTimeStamps -XX:+UseG1GC -Xloggc:gc.log

这里我们先不打印详情，看看Full GC出现的时候是什么时候。

2、代码

public class FGCDemo12 {

  private static final ArrayList list = new ArrayList();

  public static void main(String[] args) throws Exception{

 

​    while(true){

​      // 设定一个对象为256KB，如果太大，会直接走大对象分配无法模拟YGC

​      byte[] data = new byte[1024 * 1024];

 

​      for(int i = 0; i< 36; i++){

​        data = new byte[1024 * 1024];

​      }

​      // 创建n个对象，加入到一个list中，不回收这些数组。

​      for(int i = 0; i<= 36; i++){

​        byte[] data2 = new byte[1024 * 1024];

​        // 一部分字节数组加入到list中

​        list.add(data2);

​      }

​      Thread.sleep(1000);

​    }

  }

}

 

3、日志及日志详情

不加打印详情的时候，GC信息是这样子的。

0.160: [GC pause (G1 Evacuation Pause) (young) 4096K->1808K(32M), 0.0028039 secs]

0.163: [GC pause (G1 Evacuation Pause) (young) 5904K->1976K(32M), 0.0017013 secs]

0.165: [GC pause (G1 Evacuation Pause) (young) 6072K->1997K(32M), 0.0012654 secs]

0.167: [GC pause (G1 Evacuation Pause) (young) 6093K->1819K(32M), 0.0012048 secs]

0.171: [GC pause (G1 Evacuation Pause) (young) 13M->2198K(32M), 0.0016891 secs]

0.174: [GC pause (G1 Evacuation Pause) (young) 14M->1900K(32M), 0.0013103 secs]

0.176: [GC pause (G1 Evacuation Pause) (young) 13M->2992K(32M), 0.0016521 secs]

0.179: [GC pause (G1 Evacuation Pause) (young) 14M->14M(32M), 0.0030006 secs]

0.183: [GC pause (G1 Evacuation Pause) (young)-- 30M->32M(32M), 0.0014989 secs]

0.184: [Full GC (Allocation Failure) 32M->23M(32M), 0.0052335 secs]

0.190: [Full GC (Allocation Failure) 23M->23M(32M), 0.0028101 secs]

0.193: [GC pause (G1 Evacuation Pause) (young) 23M->23M(32M), 0.0008639 secs]

0.194: [GC pause (G1 Evacuation Pause) (young) (initial-mark) 23M->23M(32M), 0.0008385 secs]

0.195: [GC concurrent-root-region-scan-start]

0.195: [GC concurrent-root-region-scan-end, 0.0000051 secs]

0.195: [GC concurrent-mark-start]

0.195: [Full GC (Allocation Failure) 23M->22M(32M), 0.0062830 secs]

0.201: [Full GC (Allocation Failure) 22M->22M(32M), 0.0028600 secs]

0.204: [GC concurrent-mark-abort]

0.204: [GC pause (G1 Evacuation Pause) (young) 22M->22M(32M), 0.0006367 secs]

0.205: [GC pause (G1 Evacuation Pause) (young) (initial-mark) 22M->22M(32M), 0.0007129 secs]

0.206: [GC concurrent-root-region-scan-start]

0.206: [GC concurrent-root-region-scan-end, 0.0000050 secs]

0.206: [GC concurrent-mark-start]

// 注意看，在并发标记开始的时候，进入了Full GC，并且进行了两次Full GC，最终并发标记终止，在我们的代码控制台，也抛出了OOM异常。说明我们对Full GC的分析是正确的，会分两次GC，第一次不处理软引用，第二次处理。

0.206: [Full GC (Allocation Failure) 22M->22M(32M), 0.0024892 secs]

0.208: [Full GC (Allocation Failure) 22M->22M(32M), 0.0020168 secs]

0.210: [GC concurrent-mark-abort]

 

修改参数为打印日志详情：

-XX:InitialHeapSize=36M -XX:MaxHeapSize=36M -XX:G1HeapRegionSize=4M -XX:MaxGCPauseMillis=20 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseG1GC -Xloggc:gc.log

 

在添加了打印日志详情 -XX:+PrintGCDetails 参数之后，日志输出如下：

CommandLine flags: -XX:G1HeapRegionSize=4194304 -XX:InitialHeapSize=37748736 -XX:MaxGCPauseMillis=20 -XX:MaxHeapSize=37748736 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseG1GC -XX:-UseLargePagesIndividualAllocation

0.164: [GC pause (G1 Evacuation Pause) (young), 0.0015655 secs]

  [Parallel Time: 1.1 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 163.6, Avg: 163.6, Max: 163.7, Diff: 0.1]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.2, Max: 0.5, Diff: 0.5, Sum: 1.7]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.0, Avg: 0.6, Max: 0.8, Diff: 0.8, Sum: 4.5]

   [Termination (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.7]

​     [Termination Attempts: Min: 1, Avg: 3.0, Max: 6, Diff: 5, Sum: 24]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.2, Max: 1.0, Diff: 0.9, Sum: 1.3]

   [GC Worker Total (ms): Min: 1.0, Avg: 1.0, Max: 1.1, Diff: 0.1, Sum: 8.2]

​    [GC Worker End (ms): Min: 164.7, Avg: 164.7, Max: 164.7, Diff: 0.0]

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

  [Eden: 4096.0K(4096.0K)->0.0B(4096.0K) Survivors: 0.0B->4096.0K Heap: 4096.0K(36.0M)->1896.1K(36.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.166: [GC pause (G1 Evacuation Pause) (young), 0.0016293 secs]

  [Parallel Time: 1.1 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 165.9, Avg: 166.1, Max: 166.8, Diff: 1.0]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.2, Max: 0.6, Diff: 0.6, Sum: 1.5]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.0, Avg: 0.5, Max: 0.8, Diff: 0.8, Sum: 4.0]

   [Termination (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.4]

​     [Termination Attempts: Min: 1, Avg: 2.3, Max: 4, Diff: 3, Sum: 18]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

   [GC Worker Total (ms): Min: 0.0, Avg: 0.8, Max: 1.0, Diff: 1.0, Sum: 6.0]

   [GC Worker End (ms): Min: 166.9, Avg: 166.9, Max: 166.9, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Other: 0.4 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.2 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

  [Eden: 4096.0K(4096.0K)->0.0B(4096.0K) Survivors: 4096.0K->4096.0K Heap: 5992.1K(36.0M)->2202.0K(36.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.168: [GC pause (G1 Evacuation Pause) (young), 0.0014074 secs]

  [Parallel Time: 0.9 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 168.0, Avg: 168.2, Max: 168.8, Diff: 0.8]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.3, Diff: 0.3, Sum: 1.2]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.0, Avg: 0.4, Max: 0.6, Diff: 0.6, Sum: 3.1]

   [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.4]

​     [Termination Attempts: Min: 1, Avg: 5.0, Max: 9, Diff: 8, Sum: 40]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [GC Worker Total (ms): Min: 0.0, Avg: 0.6, Max: 0.8, Diff: 0.8, Sum: 4.7]

   [GC Worker End (ms): Min: 168.8, Avg: 168.8, Max: 168.8, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.2 ms]

  [Other: 0.4 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.2 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.2 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

  [Eden: 4096.0K(4096.0K)->0.0B(4096.0K) Survivors: 4096.0K->4096.0K Heap: 6298.0K(36.0M)->1777.8K(36.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.170: [GC pause (G1 Evacuation Pause) (young), 0.0013150 secs]

  [Parallel Time: 0.9 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 169.9, Avg: 170.1, Max: 170.6, Diff: 0.8]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 1.0]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.0, Avg: 0.4, Max: 0.5, Diff: 0.5, Sum: 3.1]

   [Termination (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.5]

​     [Termination Attempts: Min: 1, Avg: 5.4, Max: 11, Diff: 10, Sum: 43]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [GC Worker Total (ms): Min: 0.0, Avg: 0.6, Max: 0.8, Diff: 0.8, Sum: 4.6]

   [GC Worker End (ms): Min: 170.7, Avg: 170.7, Max: 170.7, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.2 ms]

  [Other: 0.3 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.1 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

  [Eden: 4096.0K(4096.0K)->0.0B(8192.0K) Survivors: 4096.0K->4096.0K Heap: 5873.8K(36.0M)->1948.1K(36.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.173: [GC pause (G1 Evacuation Pause) (young), 0.0016268 secs]

  [Parallel Time: 1.2 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 172.6, Avg: 172.9, Max: 173.6, Diff: 1.0]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.2, Max: 0.3, Diff: 0.3, Sum: 1.2]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.0, Avg: 0.6, Max: 0.9, Diff: 0.9, Sum: 4.4]

   [Termination (ms): Min: 0.0, Avg: 0.2, Max: 0.2, Diff: 0.2, Sum: 1.3]

​     [Termination Attempts: Min: 1, Avg: 3.1, Max: 6, Diff: 5, Sum: 25]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [GC Worker Total (ms): Min: 0.1, Avg: 0.9, Max: 1.1, Diff: 1.0, Sum: 7.0]

   [GC Worker End (ms): Min: 173.8, Avg: 173.8, Max: 173.8, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Other: 0.3 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.1 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

  [Eden: 8192.0K(8192.0K)->0.0B(16.0M) Survivors: 4096.0K->4096.0K Heap: 10140.1K(36.0M)->2080.7K(36.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.178: [GC pause (G1 Evacuation Pause) (young), 0.0015187 secs]

  [Parallel Time: 1.1 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 177.7, Avg: 177.8, Max: 178.6, Diff: 0.9]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.2, Max: 0.4, Diff: 0.4, Sum: 1.6]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.0, Avg: 0.5, Max: 0.7, Diff: 0.7, Sum: 4.4]

   [Termination (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.6]

​     [Termination Attempts: Min: 1, Avg: 4.5, Max: 9, Diff: 8, Sum: 36]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

   [GC Worker Total (ms): Min: 0.1, Avg: 0.8, Max: 1.0, Diff: 0.9, Sum: 6.6]

   [GC Worker End (ms): Min: 178.7, Avg: 178.7, Max: 178.7, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.2 ms]

  [Other: 0.3 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.1 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

  [Eden: 16.0M(16.0M)->0.0B(16.0M) Survivors: 4096.0K->4096.0K Heap: 18.0M(36.0M)->2069.6K(36.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.181: [GC pause (G1 Evacuation Pause) (young), 0.0018350 secs]

  [Parallel Time: 1.4 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 180.7, Avg: 180.8, Max: 181.4, Diff: 0.7]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 1.1]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.1, Avg: 0.7, Max: 1.1, Diff: 1.0, Sum: 5.7]

   [Termination (ms): Min: 0.0, Avg: 0.3, Max: 0.5, Diff: 0.5, Sum: 2.5]

​     [Termination Attempts: Min: 1, Avg: 3.1, Max: 8, Diff: 7, Sum: 25]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

   [GC Worker Total (ms): Min: 0.6, Avg: 1.2, Max: 1.3, Diff: 0.7, Sum: 9.4]

   [GC Worker End (ms): Min: 182.0, Avg: 182.0, Max: 182.0, Diff: 0.0]

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

  [Eden: 16.0M(16.0M)->0.0B(16.0M) Survivors: 4096.0K->4096.0K Heap: 18.0M(36.0M)->6165.5K(36.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.183: [GC pause (G1 Evacuation Pause) (young) (to-space exhausted), 0.0032817 secs]

  [Parallel Time: 2.7 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 183.4, Avg: 184.0, Max: 185.5, Diff: 2.1]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.3, Diff: 0.3, Sum: 1.1]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.1]

​     [Processed Buffers: Min: 0, Avg: 0.5, Max: 3, Diff: 3, Sum: 4]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.0, Avg: 1.5, Max: 2.4, Diff: 2.4, Sum: 12.0]

   [Termination (ms): Min: 0.0, Avg: 0.5, Max: 0.8, Diff: 0.8, Sum: 3.8]

​     [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

   [GC Worker Total (ms): Min: 0.6, Avg: 2.1, Max: 2.7, Diff: 2.1, Sum: 17.1]

   [GC Worker End (ms): Min: 186.1, Avg: 186.1, Max: 186.1, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Other: 0.4 ms]

   [Evacuation Failure: 0.1 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.2 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

  [Eden: 16.0M(16.0M)->0.0B(8192.0K) Survivors: 4096.0K->4096.0K Heap: 22.0M(36.0M)->24.0M(36.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

// 在这里还是开启了初始标记，然后紧接着进入了并发标记阶段

0.187: [GC pause (G1 Evacuation Pause) (young) (initial-mark) (to-space exhausted), 0.0013577 secs]

  [Parallel Time: 0.8 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 187.3, Avg: 187.3, Max: 187.3, Diff: 0.1]

   [Ext Root Scanning (ms): Min: 0.1, Avg: 0.1, Max: 0.2, Diff: 0.1, Sum: 1.1]

   [Update RS (ms): Min: 0.0, Avg: 0.1, Max: 0.5, Diff: 0.5, Sum: 0.9]

​     [Processed Buffers: Min: 0, Avg: 1.1, Max: 2, Diff: 2, Sum: 9]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.0, Avg: 0.1, Max: 0.3, Diff: 0.3, Sum: 1.0]

   [Termination (ms): Min: 0.0, Avg: 0.3, Max: 0.5, Diff: 0.5, Sum: 2.2]

​     [Termination Attempts: Min: 1, Avg: 1.1, Max: 2, Diff: 1, Sum: 9]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [GC Worker Total (ms): Min: 0.6, Avg: 0.7, Max: 0.7, Diff: 0.1, Sum: 5.2]

   [GC Worker End (ms): Min: 188.0, Avg: 188.0, Max: 188.0, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Other: 0.5 ms]

   [Evacuation Failure: 0.1 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.2 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

  [Eden: 8192.0K(8192.0K)->0.0B(8192.0K) Survivors: 4096.0K->0.0B Heap: 32.0M(36.0M)->36.0M(36.0M)]

 [Times: user=0.13 sys=0.00, real=0.00 secs]

// 进入并发标记阶段之后，开始并发标记的时候，直接进入了Full GC状态。

0.189: [GC concurrent-root-region-scan-start]

0.189: [GC concurrent-root-region-scan-end, 0.0000047 secs]

0.189: [GC concurrent-mark-start]

// 我们可以看到Full GC第一次进行的时候，还是回收了一些垃圾的，并且对堆内存进行了整理。在Full GC之后，就没有Eden区，Survivor区的具体细节了，因为这个过程结束后，会把所有分区标记成Old，然后再重新选择一些region成为Eden区。

0.189: [Full GC (Allocation Failure) 36M->23M(36M), 0.0051574 secs]

  [Eden: 0.0B(8192.0K)->0.0B(4096.0K) Survivors: 0.0B->0.0B Heap: 36.0M(36.0M)->23.7M(36.0M)], [Metaspace: 3445K->3445K(1056768K)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

// 然后此时并发标记就直接终止，不再进行了。

0.194: [GC concurrent-mark-abort]

// 程序运行一段时间后，Eden区又一次填满，此时继续进入YGC

0.194: [GC pause (G1 Evacuation Pause) (young) (to-space exhausted), 0.0007054 secs]

  [Parallel Time: 0.2 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 194.4, Avg: 194.4, Max: 194.4, Diff: 0.1]

   [Ext Root Scanning (ms): Min: 0.1, Avg: 0.1, Max: 0.2, Diff: 0.1, Sum: 1.0]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

​     [Processed Buffers: Min: 0, Avg: 0.3, Max: 1, Diff: 1, Sum: 2]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

​     [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [GC Worker Total (ms): Min: 0.1, Avg: 0.2, Max: 0.2, Diff: 0.1, Sum: 1.3]

   [GC Worker End (ms): Min: 194.6, Avg: 194.6, Max: 194.6, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Other: 0.4 ms]

   [Evacuation Failure: 0.1 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.1 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

// 注意看，在Full GC之后，Eden区还是一个region，并且此次YGC之后，没有回收掉任何垃圾。

  [Eden: 4096.0K(4096.0K)->0.0B(8192.0K) Survivors: 0.0B->0.0B Heap: 27.7M(36.0M)->27.7M(36.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

// 紧接着，继续运行，只能再此触发Full GC，并且仅仅压缩整理出了1M的空间

0.195: [Full GC (Allocation Failure) 27M->26M(36M), 0.0026733 secs]

  [Eden: 0.0B(8192.0K)->0.0B(4096.0K) Survivors: 0.0B->0.0B Heap: 27.7M(36.0M)->26.7M(36.0M)], [Metaspace: 3445K->3445K(1056768K)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

// 然后再此进行垃圾回收，此次回收，会把软引用进行处理，很明显我们的程序没有软引用，所以此次Full GC无法腾出足够的空间。

0.198: [Full GC (Allocation Failure) 26M->26M(36M), 0.0020266 secs]

  [Eden: 0.0B(4096.0K)->0.0B(4096.0K) Survivors: 0.0B->0.0B Heap: 26.7M(36.0M)->26.7M(36.0M)], [Metaspace: 3445K->3445K(1056768K)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

// 然后再次进入YGC，因为Eden区为0，此时再次尝试分配，失败，进入YGC，接下来

0.200: [GC pause (G1 Evacuation Pause) (young), 0.0007709 secs]

  [Parallel Time: 0.3 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 200.3, Avg: 200.3, Max: 200.5, Diff: 0.2]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.8]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: Min: 0, Avg: 0.1, Max: 1, Diff: 1, Sum: 1]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.3]

​     [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [GC Worker Total (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 1.2]

   [GC Worker End (ms): Min: 200.5, Avg: 200.5, Max: 200.5, Diff: 0.0]

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

  [Eden: 0.0B(4096.0K)->0.0B(8192.0K) Survivors: 0.0B->0.0B Heap: 26.7M(36.0M)->26.7M(36.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.201: [GC pause (G1 Evacuation Pause) (young) (initial-mark), 0.0006809 secs]

  [Parallel Time: 0.3 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 201.2, Avg: 201.3, Max: 201.4, Diff: 0.2]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 1.1]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: Min: 0, Avg: 0.1, Max: 1, Diff: 1, Sum: 1]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.2]

​     [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [GC Worker Total (ms): Min: 0.1, Avg: 0.2, Max: 0.2, Diff: 0.2, Sum: 1.4]

   [GC Worker End (ms): Min: 201.4, Avg: 201.4, Max: 201.4, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Other: 0.3 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.1 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

  [Eden: 0.0B(8192.0K)->0.0B(8192.0K) Survivors: 0.0B->0.0B Heap: 26.7M(36.0M)->26.7M(36.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.202: [GC concurrent-root-region-scan-start]

0.202: [GC concurrent-root-region-scan-end, 0.0000054 secs]

0.202: [GC concurrent-mark-start]

0.202: [Full GC (Allocation Failure) 26M->25M(36M), 0.0067177 secs]

  [Eden: 0.0B(8192.0K)->0.0B(4096.0K) Survivors: 0.0B->0.0B Heap: 26.7M(36.0M)->25.7M(36.0M)], [Metaspace: 3445K->3445K(1056768K)]

 [Times: user=0.01 sys=0.00, real=0.01 secs]

0.209: [Full GC (Allocation Failure) 25M->25M(36M), 0.0022560 secs]

  [Eden: 0.0B(4096.0K)->0.0B(4096.0K) Survivors: 0.0B->0.0B Heap: 25.7M(36.0M)->25.7M(36.0M)], [Metaspace: 3445K->3445K(1056768K)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.211: [GC concurrent-mark-abort]

0.211: [GC pause (G1 Evacuation Pause) (young), 0.0006518 secs]

  [Parallel Time: 0.3 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 211.5, Avg: 211.6, Max: 211.7, Diff: 0.2]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.7]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: Min: 0, Avg: 0.1, Max: 1, Diff: 1, Sum: 1]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.3]

​     [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [GC Worker Total (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 1.1]

   [GC Worker End (ms): Min: 211.7, Avg: 211.7, Max: 211.7, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Other: 0.3 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.1 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

  [Eden: 0.0B(4096.0K)->0.0B(4096.0K) Survivors: 0.0B->0.0B Heap: 25.7M(36.0M)->25.7M(36.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.212: [GC pause (G1 Evacuation Pause) (young) (initial-mark), 0.0006747 secs]

  [Parallel Time: 0.3 ms, GC Workers: 8]

   [GC Worker Start (ms): Min: 212.4, Avg: 212.4, Max: 212.6, Diff: 0.2]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 1.2]

   [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Processed Buffers: Min: 0, Avg: 0.1, Max: 1, Diff: 1, Sum: 1]

   [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Object Copy (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.2]

​     [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

   [GC Worker Total (ms): Min: 0.1, Avg: 0.2, Max: 0.2, Diff: 0.2, Sum: 1.5]

   [GC Worker End (ms): Min: 212.6, Avg: 212.6, Max: 212.6, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Other: 0.2 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.1 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

   [Humongous Register: 0.0 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.0 ms]

  [Eden: 0.0B(4096.0K)->0.0B(4096.0K) Survivors: 0.0B->0.0B Heap: 25.7M(36.0M)->25.7M(36.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.213: [GC concurrent-root-region-scan-start]

0.213: [GC concurrent-root-region-scan-end, 0.0000046 secs]

0.213: [GC concurrent-mark-start]

0.213: [Full GC (Allocation Failure) 25M->25M(36M), 0.0025106 secs]

  [Eden: 0.0B(4096.0K)->0.0B(4096.0K) Survivors: 0.0B->0.0B Heap: 25.7M(36.0M)->25.7M(36.0M)], [Metaspace: 3445K->3445K(1056768K)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.216: [Full GC (Allocation Failure) 25M->25M(36M), 0.0020506 secs]

  [Eden: 0.0B(4096.0K)->0.0B(4096.0K) Survivors: 0.0B->0.0B Heap: 25.7M(36.0M)->25.7M(36.0M)], [Metaspace: 3445K->3445K(1056768K)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.218: [GC concurrent-mark-abort]

 

 

最终经过多次尝试之后，无法分配，最终OOM。可以看到G1对于OOM其实是比较慎重的。经过了很多次YGC 并发标记，多次Full GC的过程之后，才最终抛出了OOM异常

 

4、总结

Full GC的发生在G1里面比ParNew+CMS要更加困难，并且尝试的次数日志上来看也非常多。Full GC的整个过程比较简单粗暴。如果实在无法压缩出空间，就会走向最终的OOM。