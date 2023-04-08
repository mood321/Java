回顾：

1、优化点之并行化处理

2、并行FGC开始前的前置工作

保存一些元数据信息

3、并行标记过程（STW）

线程的标记栈，基于这个标记栈出发，来标记出来所有的存活的对象。

4、Full GC的并行标记过程和并发标记的区别

5、Full GC标记过程中的任务窃取

6、总结

（1）标记的前置工作

（2）并行标记的过程

（3）并行标记过程中的特殊数据结构（任务栈）

（4）并行标记的性能另一个性能优化点：任务窃取

7、思考：在FULL GC的其他环节，有没有优化的空间？如果是我们自己来做，应该怎么做，才能充分提升Full GC的从时间层面的效率？引入并行化之后，空间方面有没有优化的空间呢？

提示：串行处理，需要一个一个分区去处理。Gc roots遍历到哪个分区就处理哪个分区。并行化处理，一个线程可以处理多个分区。

 

本节内容：

1、计算对象的新地址做了哪些改进？

上节课我们说，在最终标记的过程中，为了极致的性能优化，G1采取了多线程并行处理，以及最后的任务窃取的方式，保证执行的效率。那么在计算对象新地址的层面上，它还做了哪些优化？

我们先思考一个问题，如果说，FULL GC之后，只是对region本身进行了压缩回收整理，而没有腾出来新的空闲region，这个时候，系统程序如果要用一个空闲的region，是不是只能走扩展的路线？非常之麻烦。如下图所示，经过了串行化FULL GC之后，存活对象在region内压缩处理，但是却没有一个完整的空闲region。

假如一个大对象需要分配，此时没有一个完全空闲的region，是有问题。还是无法分配。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/11880800_1641819102.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

要解决这个问题，要么就是直接跨region的进行压缩。把存活对象集中搞到一个region中。要么就只能扩展内存，扩展出一个新的region出来了。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/17256200_1641819102.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

所以说，串行化，还是有它的弊端：

因为它只能一个一个region去处理，就造成它没办法把全部的region的存活对象都集中复制到其中一些region中去，以腾空出来一些空闲region。

而并行化的Full GC因为同时对多个region进行处理，并且这些region是不会被其他的线程干扰的，那么它就有了一定的操作空间：也就是说，它可以尝试把对象集中压缩的其中一些region，腾出来一块儿或者几块儿完全空闲的region。如下图所示：线程1处理了3个分区，此时存活的对象可能只有2个。完全可以用一个分区把存活对象放进去。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/27203000_1641819102.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/27359200_1641819102.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

经过新地址计算，对象的位置被确定在第一个分区的头部位置。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/67849000_1641819102.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

注意啊：此时对象还没有复制到新的位置，只是计算出了新的位置。并且把对象头里面对这个对象的引用（或者也可以说是对象头中存储的对象的位置信息）修改到了新的位置。具体可以结合前面课程讲的Full GC过程来看。

 

2、如果压缩过程中出现了跨分区应该怎么办？

在计算新位置的过程中，因为是可以把对象位置定位到其他分区的，那么就可能会出现，一个分区剩余的内存空间，不足以放下另外一个存活对象的情况。如下图，第一个region此时还剩下一些空间，比如说是1KB的空间。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/69448300_1641819102.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

此时，又有一个存活对象要计算新的位置，但是新的对象是2KB，应该怎么存放？跨分区存放吗？肯定是不行的，因为在G1里面只能是大对象才能够跨分区存放，其他的对象都不允许跨分区存放。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/69177500_1641819102.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

此时2KB的这个对象想要尝试放到第一个region里面是肯定不会成功的。它此时只能进入第二个region中，并且对象的起始位置，就是这个region内存的开始地址。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/70396200_1641819102.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

需要提一点的是：这个过程，G1引入了一个组件，叫做G1FullGCCompactionPoint，这个东西来记录某个GC线程在计算对象位置的时候，所使用的分区情况，说白了就是，我用了哪个分区，用到了哪个位置。

通过这个就可以判断，对象如果要计算新位置，应该放到哪个分区的哪个位置，能不能放的下。

整个标记过程处理完成后，所有的对象的对象头存储的就是对象的新位置了。注意：这里再次强调，只是计算出了对象需要存储的位置，还没有把对象真正的复制到对应的位置。

 

总结：

1、空间上做的优化就是，可以集中一组对象到一个region中。可以提升程序后续分配对象的效率。因为扩展的操作。大对象分配跨分区存储的这个场景。

2、普通对象，在压缩的时候，不能跨分区处理。引入G1FullGCCompactionPoint东西，来帮助我们计算新位置，实现多个region存活对象，压缩到同一个region的工作。