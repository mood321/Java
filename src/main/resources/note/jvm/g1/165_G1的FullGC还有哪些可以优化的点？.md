1、full gc的基本原理回顾

（1）Full GC的并行处理

首先：G1本身的分区，就是一个相对独立的一个内存区域。

其次：每个region都有一个RSet，只要有gc roots+RSet，就可以完整的对某个分区进行所有存活对象的标记。

上面的两个条件，是G1得天独厚的优势

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/62911700_1646624258.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

Gc roots引用了Obj这个对象，而Obj3这个对象对Obj0的引用，又记录在这个region对应的RSet中。所以，我不需要找到Obj3这个gc roots，就可以对Obj0做好标记，判断它是否存活。

 

所以，即使有多个线程对多个分区同时进行处理，也得到一组正确的结果。如上图所示，最终obj，obj1，obj3，obj4都能按照规则正常的到达自己的位置。

（2）full gc的流程回顾

并行FGC开始前的前置工作：在Full GC开始之前，肯定还是要做一些准备操作。

对象头、锁信息等信息的保存处理。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/70865800_1646624258.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

在保存完一些对象头相关的信息之后，就要开始Full GC了。

 

具体步骤和串行化的Full GC是类似的。

标记存活对象；

计算对象的新地址；

更新引用对象的地址；

移动对象完成压缩（其实就是复制覆盖操作）；

对象移动后的后续处理；

 

3、并行标记过程（STW）

FullGC变成并行过程之后，并行标记过程和串行化过程差别不是很大。都是要标记出来所有的存活的对象。

需要注意的是，因为是并行化处理，所以，多个线程在进行并行标记的时候，比起串行化处理，多使用了一个栈结构来给每个线程做一个标记栈，也就是把起始对象分成多份儿，每一个GC线程，持有一部分。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/73016000_1646624258.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

Full GC会对所有堆分区里面的对象都进行标记，而且系统程序会STW。

 

5、Full GC标记过程中的任务窃取

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/72669900_1646624258.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

这个时候为了整体的性能，线程1就会从线程3那里窃取一些任务。如下图所示：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/72837000_1646624258.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

Full gc的一些优化细节，大家可以自行回顾一下我们前面讲过的内容。可以看到，full gc的过程，还是非常繁琐的，而且非常非常耗时。

 

2、遇到了full gc，到底应该怎么处理？

（1）尽可能避免

我们需要明白的一点是，full gc是我们身为程序员，需要极力避免的，我们很多人在开始学习JVM的时候，所学习的优化手段，多数都是如何尽可能的减少full gc的出现。

比如调整分代比例，调整region大小，调整回收停顿时间，调整老年代预留空间比例等等。

所以，对于full gc的处理，最最重要的手段，就是避免它！

（2）尝试优化full gc的速率

这个角度，因为涉及到的内容非常复杂，我们这边儿给出一个思考的方向：

如果是正常的full gc，优化它的速率的方法，简单来说就是，减少full gc需要处理的量。总结来说就是，full gc的时候，避免堆中存在大量的需要复杂处理的对象。

 

3、我们应该如何操作来规避full gc？

关于如何规避full gc，方式其实有很多种，不同的项目遇到的问题可能还不太一样，基本的思路，其实还是要避免触发产生full gc的条件。

而产生full gc的条件主要是以下两种：

（1）混合回收不及时，导致垃圾对象存活过多，造成空间不够。

这种场景，实际上是并发标记的启动时机问题，如果说并发标记启动的频率，要远远落后于垃圾产生的速率，那么就会出现大量空间被垃圾对象占用，导致不必要的full gc。

另外，就是回收的速度太低，导致停顿时间内回收垃圾的数量太少。最终造成空间不够。

 

（2）存活对象实在太多，各种gc都尝试过了，无法腾出来足够的空间给分配新对象。执行了ygc + mixedgc之后出现晋升失败，不得不进行full gc。

具体分两种场景：

场景1：G1使用的垃圾回收算法，是标记复制算法+标记整理算法，在进行垃圾回收的时候，新创建的对象，以及存活的对象，没有足够的空间可以使用的话，复制的操作是无法实现的，因为存活对象每次gc都是要复制到空闲的region中去的。

场景2：多次gc之后仍然无法给新对象腾出足够的空间，导致full gc的。

 

针对这些场景出现的full gc，我们所能做的就是尽可能的去合理的优化参数，保证不会触发这些场景即可。具体需要调整哪些参数，如何调整，我们会在参数调优章节详细展开。

 

4、我们应该如何操作来加快full gc的速度？

Full gc的速度，在JVM层面其实已经做了很多的优化，包括上面我们看到的并行优化等等。那么在此基础之上，我们还能通过什么策略来提升Full gc的速度？

上面我们说到了，要减少Full gc处理的总量，大家思考一下，直接靠减少堆内存来减少full gc要处理的总量，靠谱吗？很显然时不靠谱的。

所以，针对full gc的速度，要做的优化，不能简单的从堆内存空间大小来考虑。G1给我们提供了一种思路，叫做“弥留空间”，当G1发现，JVM经历了n次GC之后，就会允许一定比例的空间，用来作为把死亡对象当成存活对象来处理的空间。什么意思呢？我们看下图：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/67681600_1646624258.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

图中的灰色对象代表死亡对象，这些死亡对象所在的一定范围的区域，可以成为弥留空间，虽然这个弥留空间是死亡对象，但是在GC处理的时候，是当作存活对象来处理的。也就是说，full gc在处理这块儿弥留空间里面的对象的时候，是直接把他们复制到所在region的开始位置的。把他们直接当作存活对象处理，不需要做各种复杂的标记，判定引用，指针替换等等各种操作，因为明知他们已经是死亡对象了，但是我此时先不做全部空间的标记压缩整理。而只做部分。

此时这块儿区域就能快速被跳过，就从处理空间上减少了一部分。能一定程度提高full gc的速率。

弥留空间，本身就是为了提升full gc的效率