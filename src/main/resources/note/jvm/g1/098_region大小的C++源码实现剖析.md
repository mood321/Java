本节内容

1、regionSize如果不符合规则，G1是怎么处理的？

最后留一个问题，regionSize如果我设置成3MB 1.5MB 64MB，和给定的范围不同，会怎么样？或者说，如果我的堆内存给的是3G，然后计算出来的regionSize是一个非2的n次幂，G1会做什么处理？

超过了G1的region size 上下限会怎么样？

答案其实很简单，G1会自动和2^n对齐。

那我们需要再思考一个问题，对齐的规则到底是什么？因为1.5 1.9这种不同的数字，肯定是要对应不同的值的，是走四舍五入，还是什么操作呢？

如果超过了大小范围会怎么办？[1,32]

 

2、对齐的规则是什么？超过了大小限制，会怎么做？

**关键词：向2****的n****次幂对齐**

也就是说，计算regionSize得到的结果不是2^n的话，就会向2^n对齐，具体的对齐规则就是，从计算得到的数字中，找到数字里包含的最大的2^n幂。

 

举个例子：如果说，我们计算出来的结果是1.5MB，那么regionSize此时就是1MB，如果计算出来是3MB，那么regionSize就是2MB，如果计算出来是9MB，那么regionSize就是8MB

如果说是自己设置的regionSize其实也是一样。

 

3、从源码中探索一下具体的regionSize实现

void HeapRegion::setup_heap_region_size(size_t initial_heap_size, size_t max_heap_size) {

 uintx region_size = G1HeapRegionSize;

 if (FLAG_IS_DEFAULT(G1HeapRegionSize)) {

  size_t average_heap_size = (initial_heap_size + max_heap_size) / 2;

 // 这边就是根据内存大小，做的第一步计算。没有完全确定region_size的大小

  region_size = MAX2(average_heap_size / TARGET_REGION_NUMBER,

​            (uintx) MIN_REGION_SIZE);

 }

 // 实际上，就是获取这个region_size的2的指数。

 // 2MB = 2 * 1024 * 1024 B = 2^21 B

 // 1.5MB = 1.5 * 1024 * 1024 = 2^20<1.5 * 2^20 <2 ^ 21 -->20

 // 3MB = 3 * 1024 * 1024  2^21 <1.5 * 2 * 1024 * 1024 < 2^22 -->21

 // region [1,32],64MB = 64 * 1024 * 1024 = 2^26  -->26

 int region_size_log = log2_long((jlong) region_size);

 // Recalculate the region size to make sure it's a power of

 // 2. This means that region_size is the largest power of 2 that's

 // <= what we've calculated so far.

 // 确保，region_size是最大的2的n次幂的值。

 // 也就是说，我们上面计算出来的最大的一个region_size_log，用1 << region_size_log位

 // 计算出最终的region_size,1左移多少位，和1*2^多少次方

 // 64mb,计算结果是region_size 64MB,很显然不对

 // 1 * 2,1 << 1，结果是不是一致？2  0000 0000 0000 0100

 // 1 * 2^21 1<<21,在计算机中，左移操作和数学计算乘以2^n是一致的，并且位运算，效率远远高于普通的数学计算。

 region_size = ((uintx)1 << region_size_log);

 

 // Now make sure that we don't go over or under our limits.[1,32]

 if (region_size < MIN_REGION_SIZE) {

  region_size = MIN_REGION_SIZE;

 } else if (region_size > MAX_REGION_SIZE) {

  region_size = MAX_REGION_SIZE;

 }

 

 // 重新计算regionSize的2的指数

 // And recalculate the log.

 region_size_log = log2_long((jlong) region_size);

 

 // 设置一些全局变量

 // Now, set up the globals.

 guarantee(LogOfHRGrainBytes == 0, "we should only set it once");

 LogOfHRGrainBytes = region_size_log;

 

 guarantee(LogOfHRGrainWords == 0, "we should only set it once");

 LogOfHRGrainWords = LogOfHRGrainBytes - LogHeapWordSize;

 

 guarantee(GrainBytes == 0, "we should only set it once");

 // The cast to int is safe, given that we've bounded region_size by

 // MIN_REGION_SIZE and MAX_REGION_SIZE.

 GrainBytes = (size_t)region_size;

 

 guarantee(GrainWords == 0, "we should only set it once");

 GrainWords = GrainBytes >> LogHeapWordSize;

 guarantee((size_t) 1 << LogOfHRGrainWords == GrainWords, "sanity");

 

 // 卡表的一些设置

 guarantee(CardsPerRegion == 0, "we should only set it once");

 CardsPerRegion = GrainBytes >> CardTableModRefBS::card_shift;

}

计算的时候，上面说的公式，只是第一步计算的结果。然后后续会对这个值做一个修正。

region_size = 1.5MB

region_size_log=20（因为2^20 < 1.5 * 1024 * 1024 <2^21 ）2MB/2

region_size = 2^20 = 1MB

 

region_size = 64MB

region_size_log = 26（因为2^26<=64MB）

region_size = 2^26 = 64MB

region_size = 32MB（因为region的大小，必须要在1-32MB之间）

 

思考：region_size是一个固定的值，前面我们又介绍了，分区数量是2048，那堆内存是怎么变化的？这很显然不满足我们所说的G1的一个动态调整的规则啊？分区数量会跟随内存动态扩展，来变化。

 

4、基于regionSize分区数量的变动过程

首先要明确一点，我们从源码中可以看出来，在计算regionSize的时候，会使用一个默认的2048这么一个参数来计算regionSize，然后regionSize会被动态调整成一个合理的值。所以说我们所说的2048只是一个默认值，也就是说，在使用2048这个值计算完成之后，如果regionSize没有调整，并且堆内存不会动态扩展的时候，堆分区的数量才是2048这个值！

 

小结：如果要计算分区大小，肯定需要一个heap size，计算出来分区大小，同时又有了这么一个heapsize，肯定能自动计算出来有多少个分区，而又因为堆内存是很有可能出现变化的，那么，分区数量是一定会随着堆内存变化而变化的，这其实就是G1扩展内存的方式：扩展新的分区以达到扩展内存的效果。

注意：G1不能手动指定分区个数！！！