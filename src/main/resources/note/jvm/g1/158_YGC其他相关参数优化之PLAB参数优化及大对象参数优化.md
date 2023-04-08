1、YGC其他相关参数的优化思路

（1）-XX:ParallelGCThreads          STW期间，并行GC线程数，默认值为0。通常来说这个值不需要自己设置。

（2）-XX:G1RsetScanBlockSize 默认是64 一般是不需要调整的。除非对于个位数毫秒级别的优化有需求。

 

RSet是一个KeyValue对组成的数据集合。其实就是一个set，并且一个region共享一个，那么如果说引用关系非常复杂的话，RSet会非常大，所以处理的时候可以分批次去处理。每次处理一部分，这样的话就能计算的更快。不过需要注意的是这个参数和计算机的计算能力是有关的，如果你的处理器比较强大，计算能力比较强，调大这个参数可以提高效率。（相当于减少了取数据的次数）如果计算能力一般，处理器配置比较差，那么最好不要调整这个参数，因为如果过大会导致计算速度下降。反而起到反效果。比如说，CPU 1ms能处理128个数据的计算，那么你调大了这个值，比如调整成128，可能会节省一些提取数据的时间，但是如果CPU1ms只能处理32个数据的计算，那肯定还是调小了整体的性能更优。

不知道大家还记不记得这个张图？如果说引用关系非常复杂，一个key value对就可能会出现非常大的空间占用，所以如果说不分片处理的话，会导致性能比较低。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/40513200_1644392554.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

 

（4）-XX:TargetSurvivorRatio  默认是50 

这个值本身的含义是survivor区的使用率，达到这个值就可能触发对象进入老年代。也就是说，年龄1 + 年龄2 + 年龄3 >=50%的时候，就会把年龄3及以上的对象都晋升至老年代。这个一般来说也是不需要调整的。当然也需要根据具体情况来看，如果说程序中有一些对象晋升至老年代的速度过于快了，可以考虑增大这个值。减缓对象进入老年代的速度。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/43877800_1644392554.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

如图所示，当survivor被占用一半的时候，就会触发动态年龄判断规则，会把一部分年龄稍大的对象晋升至老年代

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/44117000_1644392554.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

这个还是会对效率有一点点影响的。但是基本上也是毫秒级别的影响。一般来说这个值会考虑调大，避免对象进入老年代速度过快，并且可以保证survivor区的内存也能尽可能的被使用。对性能整体的影响在这个层面体现的也并不明显。

 

（5）-XX:ParGCArrayScanChunk 默认值50，也就是如果一个对象数组的长度超过了50，在做GC的时候，不会一次性遍历它， 而是50 50的去做处理。只有最后一次，假如剩余的长度是2 * ParGCArrayScanChunk 的时候，才会直接全部处理。 

这个参数比较冷门，一般不做调整。但是这个参数的调整可以起到一些效果。增大该值，处理大数组的效率会略有提升，但是会增加栈溢出的风险。减小该值，可以减小栈溢出风险，但是处理效率会略有下降。

 

（6）-XX:+ResizePLAB，默认为true 表示在GC结束之后，会调整PLAB的大小。PLAB和TLAB类似，只不过TLAB是指对象分配的缓冲。PLAB指在GC过程中，对象复制时的一块儿缓冲。和TLAB时同样的效果，也是用来提升对象分配的效率的。只不过产生的时机不同。

-XX:YoungPLABSize 默认值4096，是新生代PLAB缓存的大小。4096这个数字不是指字节，也不是byte，也不是KB MB这类的单位。是为了服务于不同JVM而产生的。

在32位JVM中，因为内存单元最少也需要4个字节，一个字节8位，4*8 = 32位。4096 * 4= 16KB，也就是在32位JVM中因为一位需要4个字节，所以用4096这个基数乘以4，如果是64位，则需要4096 * 8 = 32KB。 

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/45180200_1644392554.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

 

对于这个值，因为是在GC过程中的复制缓存，在内存充足的情况下，可增大该值，以提高复制的效率，但是有可能增加内存碎片。减小该值，可以减少内存碎片，但是复制的效率可能会有所下降。当然也不是绝对，因为在新生代GC中，复制操作是给survivor区复制，如果说PLAB过大，造成内存碎片比较多（类似于dummy对象的东西），会造成survivor区被快速耗尽，导致复制的效率变低，或者晋升老年代的概率变高。所以这个参数要慎重调大。一般在实际调优中，都是需要把这个参数调小。

 

（7）-XX:OldPLABSize 默认值1024，指老年代的PLAB缓存大小。32位JVM中为4KB，64位JVM中，为8KB。注意这个值并不是mixed GC产生的复制对应的参数，而是从新生代晋升到老年代的时候，PLAB的大小。同样会带来内存碎片的问题。但是一般来说，老年代内存空间比较大，可以容忍更多的内存碎片，所以追求效率的话，可以考虑把这个值调大。

 

（8）ParrallelGCBufferWastePct 默认值10，表示从Eden区到Survivor区或者对象晋升至老年代的时候，如果PLAB剩余的空间小于这个比例，并且没有办法放入一个晋升过来的新对象，就会丢弃这个PLAB块儿，同时去申请一个新的PLAB。所以说，这个值如果调的比较大，在复制的时候效率会高很多，但是内存浪费会更加严重。类似于TLAB的RefillWaste。

 

（9）XX:+G1ReclaimDeadHumongousObjectsAtYoungGC 这个参数是控制YGC的时候是否回收大对象分区的参数，默认是true，表示在YGC的时候回收大对象。当然，默认情况下只会去回收失去引用的大对象所在的分区。这个参数在后续更名为了XX:+G1EagerReclaimHumongousObjects表达的意思是一样的。如果需要设置的话，开启这个参数就OK

不过有一些研究发现，在YGC的时候，如果回收大对象可能会引起性能问题。具体的问题地址如下：

https://bugs.openjdk.java.net/browse/JDK-8141637

这个问题其实是说，在这个应用里面，虽然说每次YGC的耗时并不长，但是每次YGC中，会有20%的耗时是来源于大对象回收的操作。因为他们的系统会创建一些大对象出来。在一些特殊场景，会导致扫描的过程比较长。如果说，我们自己的应用出现了大对象回收过程中的耗时占比非常长的情况的话，可以考虑关闭这个参数

2015-11-03T19:56:46.503-0500: 3294.913: #116: [GC pause (G1 Evacuation Pause) (young), 0.0058334 secs]

  [Parallel Time: 2.7 ms, GC Workers: 18]

   [GC Worker Start (ms): Min: 3294915.2, Avg: 3294915.7, Max: 3294917.6, Diff: 2.4]

   [Ext Root Scanning (ms): Min: 0.0, Avg: 0.7, Max: 2.6, Diff: 2.6, Sum: 12.2]

   [Update RS (ms): Min: 0.0, Avg: 0.4, Max: 1.2, Diff: 1.2, Sum: 7.8]

​     [Processed Buffers: Min: 0, Avg: 1.6, Max: 11, Diff: 11, Sum: 29]

​     [Scan HCC (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 2.5]

   [Scan RS (ms): Min: 0.0, Avg: 0.4, Max: 0.8, Diff: 0.8, Sum: 7.3]

   [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]

   [Object Copy (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 1.3]

   [Termination (ms): Min: 0.0, Avg: 0.5, Max: 0.7, Diff: 0.7, Sum: 9.8]

​     [Termination Attempts: Min: 1, Avg: 1.3, Max: 2, Diff: 1, Sum: 24]

   [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.3]

   [GC Worker Total (ms): Min: 0.3, Avg: 2.2, Max: 2.7, Diff: 2.4, Sum: 38.9]

   [GC Worker End (ms): Min: 3294917.8, Avg: 3294917.9, Max: 3294917.9, Diff: 0.0]

  [Code Root Fixup: 0.0 ms]

  [Code Root Purge: 0.0 ms]

  [Clear CT: 0.1 ms]

  [Expand Heap After Collection: 0.0 ms]

  [Other: 3.0 ms]

   [Choose CSet: 0.0 ms]

   [Ref Proc: 0.4 ms]

   [Ref Enq: 0.0 ms]

   [Redirty Cards: 0.1 ms]

   [Humongous Register: 1.5 ms]

   [Humongous Reclaim: 0.0 ms]

   [Free CSet: 0.5 ms]

  [Eden: 248.0M(248.0M)->0.0B(248.0M) Survivors: 2048.0K->2048.0K Heap: 1095.6M(5000.0M)->847.6M(5000.0M)]

 

（10）XX:+G1EagerReclaimHumongousObjectsWithStaleRefs，默认为True

这个参数和上面的参数类似。这个参数表示，我是否要在YGC的时候判定那些大对象分区可以回收，如果为true表示大对象分区的RSet的引用数量小于某个阈值的时候（默认为0，可以用参数G1RSetSparseRegionEntries来控制）可以尝试去回收。如果这个值为false，则RSet中的引用数必须是0的时候，才能回收。

这个参数一般是和上面的参数配和起来使用的。如果上面的那个参数设置为false，是不会在YGC的时候回收的大对象的。