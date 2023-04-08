1、参数设置

具体参数如下：

-Xmx128M -XX:+UseG1GC -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -XX:+UnlockExperimentalVMOptions -XX:G1LogLevel=finest -XX:+UnlockDiagnosticVMOptions -XX:+G1PrintRegionLivenessInfo -XX:+G1SummarizeConcMark -XX:MaxGCPauseMillis=20 -Xloggc:gc.log

主要的区别是这几个参数：

打开数据分析选项-XX:+UnlockDiagnosticVMOptions 

打印所有的region的使用详情信息：-XX:+G1PrintRegionLivenessInfo

打开并发标记的详细信息-XX:+G1SummarizeConcMark

 

2、模拟代码

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

​       }

​      Thread.sleep(100);

​    }

}

3、代码运行相关的GC日志

 

CommandLine flags: -XX:G1LogLevel=finest -XX:+G1PrintRegionLivenessInfo -XX:+G1SummarizeConcMark -XX:InitialHeapSize=134217728 -XX:MaxGCPauseMillis=20 -XX:MaxHeapSize=134217728 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UnlockDiagnosticVMOptions -XX:+UnlockExperimentalVMOptions -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseG1GC -XX:-UseLargePagesIndividualAllocation

0.130: [GC pause (G1 Humongous Allocation) (young) (initial-mark), 0.0020150 secs]

  [Parallel Time: 1.0 ms, GC Workers: 8]

   [GC Worker Start (ms): 129.7 129.8 129.8 129.8 129.8 129.8 129.8 129.8

​    Min: 129.7, Avg: 129.8, Max: 129.8, Diff: 0.1]

   [Ext Root Scanning (ms): 0.9 0.3 0.2 0.3 0.3 0.3 0.3 0.3

​    Min: 0.2, Avg: 0.4, Max: 0.9, Diff: 0.7, Sum: 2.8]

​     [Thread Roots (ms): 0.0 0.3 0.0 0.1 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.1, Max: 0.3, Diff: 0.3, Sum: 0.4]

​     [StringTable Roots (ms): 0.0 0.0 0.0 0.1 0.1 0.1 0.1 0.1

​     Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.5]

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

​     [CodeCache Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [CM RefProcessor Roots (ms): 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Wait For Strong CLD (ms): 0.6 0.0 0.2 0.1  0.2 0.2 0.2 0.2

​     Min: 0.0, Avg: 0.2, Max: 0.6, Diff: 0.6, Sum: 1.5]

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

   [Object Copy (ms): 0.0 0.5 0.4 0.4 0.4 0.4 0.4 0.4

​    Min: 0.0, Avg: 0.4, Max: 0.5, Diff: 0.5, Sum: 3.1]

   [Termination (ms): 0.0 0.1 0.0 0.0 0.0 0.0 0.0 0.1

​    Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.3]

​     [Termination Attempts: 1 6 3 7 5 3 3 6

​     Min: 1, Avg: 4.3, Max: 7, Diff: 6, Sum: 34]

   [GC Worker Other (ms): 0.0 0.0 0.2 0.0 0.0 0.1 0.0 0.0

​    Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.5]

   [GC Worker Total (ms): 0.9 0.9 0.8 0.8 0.8 0.8 0.8 0.8

​    Min: 0.8, Avg: 0.8, Max: 0.9, Diff: 0.1, Sum: 6.7]

   [GC Worker End (ms): 130.6 130.7 130.6 130.6 130.6 130.6 130.6 130.7

​    Min: 130.6, Avg: 130.6, Max: 130.7, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.4 ms]

  [Other: 0.7 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.1 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.5 ms]

​     [Parallel Redirty: 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0

​     Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]

​     [Redirtied Cards: 0 0 0 0 0 0 0 0

​     Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]

   [Humongous Register: 0.0 ms]

​     [Humongous Total: 29]

​     [Humongous Candidate: 29]

   [Humongous Reclaim: 0.1 ms]

​     [Humongous Reclaimed: 28]

   [Free CSet: 0.0 ms]

​     [Young Free CSet: 0.0 ms]

​     [Non-Young Free CSet: 0.0 ms]

  [Eden: 3072.0K(6144.0K)->0.0B(5120.0K) Survivors: 0.0B->1024.0K Heap: 31.1M(128.0M)->1848.1K(128.0M)]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.132: [GC concurrent-root-region-scan-start]

0.133: [GC concurrent-root-region-scan-end, 0.0008120 secs]

0.133: [GC concurrent-mark-start]

0.133: [GC concurrent-mark-end, 0.0001406 secs]

0.133: [GC remark 0.133: [Finalize Marking, 0.0004982 secs] 0.134: [GC ref-proc, 0.0002247 secs] 0.134: [Unloading 0.134: [System Dictionary Unloading, 0.0000091 secs] 0.134: [Parallel Unloading, 0.0005040 secs] 0.134: [Deallocate Metadata, 0.0000141 secs], 0.0005701 secs], 0.0016329 secs]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

0.135: [GC cleanup

\### PHASE Post-Marking @ 0.135

\### HEAP reserved: 0x00000000f8000000-0x0000000100000000 region-size: 1048576

\###  type//类型      address-range//地址范围 used//使用量 prev-live next-live     gc-eff **GC****效率**  remsetRset大小 code-roots

\###                         (bytes)  (bytes)   (bytes)   (bytes/ms)  (bytes)  (bytes)

\###  HUMS 0x00000000f8000000-0x00000000f8100000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f8100000-0x00000000f8200000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000f8200000-0x00000000f8300000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f8300000-0x00000000f8400000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000f8400000-0x00000000f8500000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f8500000-0x00000000f8600000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000f8600000-0x00000000f8700000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f8700000-0x00000000f8800000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000f8800000-0x00000000f8900000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f8900000-0x00000000f8a00000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000f8a00000-0x00000000f8b00000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f8b00000-0x00000000f8c00000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000f8c00000-0x00000000f8d00000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f8d00000-0x00000000f8e00000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000f8e00000-0x00000000f8f00000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f8f00000-0x00000000f9000000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000f9000000-0x00000000f9100000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f9100000-0x00000000f9200000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000f9200000-0x00000000f9300000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f9300000-0x00000000f9400000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000f9400000-0x00000000f9500000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f9500000-0x00000000f9600000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000f9600000-0x00000000f9700000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f9700000-0x00000000f9800000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000f9800000-0x00000000f9900000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f9900000-0x00000000f9a00000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000f9a00000-0x00000000f9b00000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f9b00000-0x00000000f9c00000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000f9c00000-0x00000000f9d00000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f9d00000-0x00000000f9e00000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000f9e00000-0x00000000f9f00000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000f9f00000-0x00000000fa000000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000fa000000-0x00000000fa100000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000fa100000-0x00000000fa200000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000fa200000-0x00000000fa300000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000fa300000-0x00000000fa400000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000fa400000-0x00000000fa500000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000fa500000-0x00000000fa600000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000fa600000-0x00000000fa700000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000fa700000-0x00000000fa800000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000fa800000-0x00000000fa900000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000fa900000-0x00000000faa00000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000faa00000-0x00000000fab00000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000fab00000-0x00000000fac00000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000fac00000-0x00000000fad00000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000fad00000-0x00000000fae00000     16     16     16       0.0    2904     16

\###  HUMS 0x00000000fae00000-0x00000000faf00000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000faf00000-0x00000000fb000000     16     16     16       0.0    2904     16

\###  FREE 0x00000000fb000000-0x00000000fb100000     0     0      0       0.0    2904     16

\###  FREE 0x00000000fb100000-0x00000000fb200000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fb200000-0x00000000fb300000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fb300000-0x00000000fb400000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fb400000-0x00000000fb500000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fb500000-0x00000000fb600000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fb600000-0x00000000fb700000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fb700000-0x00000000fb800000     0     0     0       0.0    2904     16

\###  HUMS 0x00000000fb800000-0x00000000fb900000  1048576  1048576  1048576       0.0    2904     16

\###  HUMC 0x00000000fb900000-0x00000000fba00000     16     16     16       0.0    2904     16

\###  FREE 0x00000000fba00000-0x00000000fbb00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fbb00000-0x00000000fbc00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fbc00000-0x00000000fbd00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fbd00000-0x00000000fbe00000      0     0     0       0.0    2904     16

\###  FREE 0x00000000fbe00000-0x00000000fbf00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fbf00000-0x00000000fc000000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fc000000-0x00000000fc100000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fc100000-0x00000000fc200000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fc200000-0x00000000fc300000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fc300000-0x00000000fc400000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fc400000-0x00000000fc500000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fc500000-0x00000000fc600000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fc600000-0x00000000fc700000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fc700000-0x00000000fc800000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fc800000-0x00000000fc900000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fc900000-0x00000000fca00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fca00000-0x00000000fcb00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fcb00000-0x00000000fcc00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fcc00000-0x00000000fcd00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fcd00000-0x00000000fce00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fce00000-0x00000000fcf00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fcf00000-0x00000000fd000000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fd000000-0x00000000fd100000     0     0      0       0.0    2904     16

\###  FREE 0x00000000fd100000-0x00000000fd200000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fd200000-0x00000000fd300000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fd300000-0x00000000fd400000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fd400000-0x00000000fd500000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fd500000-0x00000000fd600000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fd600000-0x00000000fd700000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fd700000-0x00000000fd800000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fd800000-0x00000000fd900000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fd900000-0x00000000fda00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fda00000-0x00000000fdb00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fdb00000-0x00000000fdc00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fdc00000-0x00000000fdd00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fdd00000-0x00000000fde00000      0     0     0       0.0    2904     16

\###  FREE 0x00000000fde00000-0x00000000fdf00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fdf00000-0x00000000fe000000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fe000000-0x00000000fe100000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fe100000-0x00000000fe200000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fe200000-0x00000000fe300000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fe300000-0x00000000fe400000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fe400000-0x00000000fe500000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fe500000-0x00000000fe600000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fe600000-0x00000000fe700000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fe700000-0x00000000fe800000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fe800000-0x00000000fe900000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fe900000-0x00000000fea00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fea00000-0x00000000feb00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000feb00000-0x00000000fec00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fec00000-0x00000000fed00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fed00000-0x00000000fee00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fee00000-0x00000000fef00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000fef00000-0x00000000ff000000     0     0     0       0.0    2904     16

\###  FREE 0x00000000ff000000-0x00000000ff100000     0     0     0       0.0    2904     16

\###  FREE 0x00000000ff100000-0x00000000ff200000     0     0     0       0.0    2904     16

\###  FREE 0x00000000ff200000-0x00000000ff300000     0     0     0       0.0    2904     16

\###  FREE 0x00000000ff300000-0x00000000ff400000     0     0     0       0.0    2904     16

\###  FREE 0x00000000ff400000-0x00000000ff500000     0     0      0       0.0    2904     16

\###  FREE 0x00000000ff500000-0x00000000ff600000     0     0     0       0.0    2904     16

\###  FREE 0x00000000ff600000-0x00000000ff700000     0     0     0       0.0    2904     16

\###  FREE 0x00000000ff700000-0x00000000ff800000     0     0     0       0.0    2904     16

\###  FREE 0x00000000ff800000-0x00000000ff900000     0     0     0       0.0    2904     16

\###  FREE 0x00000000ff900000-0x00000000ffa00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000ffa00000-0x00000000ffb00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000ffb00000-0x00000000ffc00000     0     0     0       0.0    2904     16

\###  SURV 0x00000000ffc00000-0x00000000ffd00000   843840   843840   843840       0.0    3056    168

\###  FREE 0x00000000ffd00000-0x00000000ffe00000     0     0     0       0.0    2904     16

\###  FREE 0x00000000ffe00000-0x00000000fff00000     0     0     0       0.0    2904     16

\###  EDEN 0x00000000fff00000-0x0000000100000000   104904   104904   104904       0.0    2904     16

\###

\### SUMMARY capacity: 128.00 MB used: 25.91 MB / 20.24 % prev-live: 25.91 MB / 20.24 % next-live: 25.91 MB / 20.24 % remset: 0.36 MB code-roots: 0.00 MB

 

 

\### PHASE Post-Sorting @ 0.136

\### HEAP reserved: 0x00000000f8000000-0x0000000100000000 region-size: 1048576

\###

\###  type             address-range    used prev-live next-live     gc-eff   remset code-roots

\###                         (bytes)  (bytes)  (bytes)   (bytes/ms)  (bytes)  (bytes)

\###

\### SUMMARY capacity: 0.00 MB used: 0.00 MB / 0.00 % prev-live: 0.00 MB / 0.00 % next-live: 0.00 MB / 0.00 % remset: 0.01 MB code-roots: 0.00 MB

 

 25M->25M(128M), 0.0010444 secs]

 [Times: user=0.00 sys=0.00, real=0.00 secs]

// 堆内存退出前的使用状态

Heap

 garbage-first heap  total 131072K, used 65513K [0x00000000f8000000, 0x00000000f8100400, 0x0000000100000000)

 region size 1024K, 1 young (1024K), 0 survivors (0K)

 Metaspace    used 3969K, capacity 4568K, committed 4864K, reserved 1056768K

 class space  used 437K, capacity 460K, committed 512K, reserved 1048576K

// 并发标记的状态，其中包括，标记类型，比如初始标记状态，init marks，并发标记remarks，最终标记final marks，weak refs，弱引用处理，

 Concurrent marking:

   0  init marks: total time =   0.00 s (avg =   0.00 ms).

   2   remarks: total time =   0.00 s (avg =   1.36 ms).

​      [std. dev =   0.00 ms, max =   1.36 ms]

​     2 final marks: total time =   0.00 s (avg =   0.21 ms).

​       [std. dev =   0.02 ms, max =   0.23 ms]

​     2  weak refs: total time =   0.00 s (avg =   1.14 ms).

​       [std. dev =   0.02 ms, max =   1.16 ms]

   2   cleanups: total time =   0.00 s (avg =   0.78 ms).

​      [std. dev =   0.33 ms, max =   1.11 ms]

// 对对象进行计数统计花费的时间

Final counting total time =   0.00 s (avg =   0.24 ms).

// RSet的处理hi将

RS scrub total time =   0.00 s (avg =   0.22 ms).

// 所有STW的阶段加起来一共停顿了多久

 Total stop_world time =   0.00 s.

// 所有GC线程在整个并发标记过程中的总花费时间。（多个线程并发，每个线程的耗时求和）

 Total concurrent time =   0.03 s (  0.03 s marking).

 

4、这些数据有什么作用？

平常的话， -XX:+UnlockExperimentalVMOptions -XX:G1LogLevel=finest -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark这几个参数我们是不需要打开的。基本上我们借助普通的gc日志就能发现问题。

那么如果说我们要更加详细的了解每一个阶段的耗时，每一个region的回收效率去整体分析的话，这些选项就比较有用了。比如说，我们统计出来并发标记各个阶段的耗时。统计每个region里面的数据到底是怎样分布的等等。以此为基础，来调整停顿时间，regionSize等等参数，来做优化。