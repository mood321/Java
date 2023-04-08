回顾：

1、各种尝试失败之后的Full GC和YGC MixedGC有什么不同？

2、计算对象的新地址

3、更新引用对象的地址

4、对象复制

5、复制后的处理

6、思考题：Full GC为什么特别慢？有没有什么方式提升Full GC的整体效率？

答：其实Full GC慢的原因很简单。

（1）我们上节课讲的整个回收的过程，从对象标记，到对象新位置计算，到更新对象的引用关系，以及对象复制，复制后的善后工作，所有的操作都是串行化处理。也就是说，一个线程，一步一步把上面的步骤全部处理完毕。 串行化

（2）大量的region需要逐个整理，压缩，清理垃圾对象。这个过程，超级慢。因为Full GC触发的条件是非常苛刻的，基本上是实在腾不出任何空间了才会出现，而且G1中的Full GC比ParNew + CMS这些传统的分代模型触发的条件要更加苛刻一些。

 

ParNew + CMS 是老年代放不下了，就会触发Full GC（当然也包括metaspace填满等一些其他的条件）。

新生代区域此时还有可能会存留一些空间，也就是说，老年代的空间其实大概也就一半的堆内存。

 

G1是相当于所有的分区，都已经无法再提供出去分配新对象了，才会触发full gc。而且因为停顿预测模型，自动扩展分区这些机制，ygc mgc的过程，是相对比较保守的方式，一直会清理内存，腾出空间，尝试扩展，并且在对象分配的时候，也会尝试去触发ygc（一定条件下可能是mixedgc）。首先，要先分配对象，然后分配失败有可能ygc，也有可能因为45%这个条件，就直接进入mixed gc，实在是不行，还是会走扩展路线。最终还是无法分配，才会走full gc。

 

所以触发full gc的条件非常之苛刻。这也导致了，一旦G1进入了Full GC，会比普通的传统分代模型的Full GC过程还要慢。就是因为基本上所有的分区都无法使用，同时呢，也是所有的分区都有大量的存活对象，而且无法扩展新的分区。

 

思考：那么到底有没有办法改变这种情况？G1本身是做了什么样的优化？

 

1、优化方向：串行变并行

我们上节课说了，FGC是串行化的。实际上，在JDK10之前，FGC都是串行回收（一个线程，一步一步执行，最终把垃圾对象回收，同时把存活对象做了压缩整理）。而在这之后，G1本身因为分区这个结构的存在，让并行化FGC有了一些可能性。比如，标记对象可以进行并行。为什么呢？

我们在前面讲mixed gc的时候，有讲过是多个线程并行的去处理gc roots，并且，因为RSet的存在，对于GC线程来说，并不需要所有引用了当前region的gc roots都遍历完成，就能把某个region的存活对象标记出来。

RSet本身存储了外界的引用关系，结合gc roots本身来遍历某个region就能完成存活对象的标记。

我们在讲mixed gc的时候，并发标记的过程，我们说的是，根据survivor + gc roots直接引用的老年代的对象，结合RSet来标记，完成整个堆内存的对象标记。如下图所示：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/49129300_1641818833.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

经过了一个完整的并发标记和重新标记阶段，所有的对象都会标记成白或者黑，最终白色对象被回收，黑色对象被集中复制到一个新的分区里。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/92767700_1641818833.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

经过了mixed gc的多次回收之后，最终的状态是这样的：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/94029800_1641818833.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

整个过程其实就是并发的过程。

 

2、mixed gc的并发标记的过程为什么可以并发？

（1）因为并发标记，从**gc roots****出发**，遍历全部存活对象。所以，多个线程从多个gc roots出发遍历即可完成全部引用链的标记。

（2）传统的Full GC为什么要串行化？

第一，要计算新对象的位置，如果多线程去遍历，计算对象新位置，很容易出现位置冲突，出现冲突了，就需要解决冲突，解决冲突就要引入一些额外的机制，可能会性能更差。

 

第二，要压缩整理整个堆内存，更新所有的引用关系。如果一个对象在多个gc roots的引用链上。那么就会出现位置冲突，或者引用更新不全的情况。如下图所示：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/91072700_1641818833.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

如果说，两个线程同时对obj3和 obj两个对象做遍历，那么obj对象和obj0对象的位置不太好处理，搞一套复杂的机制处理冲突，不如单线程串行化处理，更加高效。（因为冲突的可能太多太多了。）

这个本身是因为堆内存是一整儿的场景下，很容易出现我们上面说的情况。

（3）G1本身有什么优势可以支持Full GC的并行处理

首先：G1本身的分区，就是一个相对独立的一个内存区域。

其次：每个region都有一个RSet，只要有gc roots+RSet，就可以完整的对某个分区进行所有存活对象的标记。

上面的两个条件，是G1得天独厚的优势，分区这个机制，可以让一个线程对一部分分区进行标记，而这些分区，只需要找到gc roots + RSet即可完成整个region的标记操作。如下图所示：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/88765200_1641818833.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

Gc roots引用了Obj这个对象，而Obj3这个对象对Obj0的引用，又记录在这个region对应的RSet中。所以，我不需要找到Obj3这个gc roots，就可以对Obj0做好标记，判断它是否存活。

 

所以，即使有多个线程对多个分区同时进行处理，也得到一组正确的结果。如上图所示，最终obj，obj1，obj3，obj4都能按照规则正常的到达自己的位置。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/89080800_1641818833.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

所以说，从这个角度上来说，G1本身的分区机制+RSet，天然就支持并发的Full GC过程。

3、总结

串行Full GC本身的性能问题。

G1在JDK10之前Full GC为什么要串行化。

G1本身的分区机制+RSet天然支持并行化处理。

 

4、思考题

大家思考一下，传统的分代模型，如果要进行Full GC，是并行还是串行？和G1有没有什么区别？