1、我们先来看看YGC相关的一些参数

（1）-XX:+UseG1GC 设置使用G1垃圾回收器

（2）-XX:G1HeapRegionSize region分区大小，可以指定，最小值1MB，最大值32MB，且只能是2的n次幂，1 2 4 8 16 32等

（3）-Xms -Xmx设置堆内存最大值最小值

（4）-XX:NewSize -XX:MaxNewSize 设置新生代最小值和最大值，注意，这个最大值，最小值，在G1里面其实是可以不设置的，G1会自动计算出来一个值，从5%的region数量开始，慢慢增加到最大为60%

（一半可以不自己指定新生代的最大值和最小值，按照默认的5% - 60%走就可以了）

（5）新生代region数量

下限：-XX:G1NewSizeRercent，默认5%

上限：-XX:G1MaxNewSizePercent，默认60%

（6）新生代Eden和Survivor的比例：-XX:SurvivorRatio=n。默认为8，即eden:s1:s2=8:1:1

这个比例和ParNew的原理是一致的，也就是说，总的新生代如果是100个region，那么eden区有80个，两个survivor区各有10个

（7）-XX:MaxGCPauseMills=n 设置最大GC暂停时间。这是一个大概值，JVM会尽可能的满足此值，例如设置200ms，那么G1就会在每次GC的时候努力保证GC的停顿时间在这个范围内。

（8）-XX:NewRatio=n new/old年大的大小比例，默认值2只设置一个newratio和只设置一个Xmn是相当。

（9）-XX:ParallelGCThreads = n 参与回收的线程数量 默认和CPU荷属相等

 

2、YGC和MixedGC、Full GC是什么关系

其实在G1中的YGC以及mixed gc以及full gc和parnew + CMS还是有相似指出的，比如说，在新生代gc之后，会有存活对象进入老年代，如果老年代对象占用达到了某个阈值，就会触发老年代的回收。

在ParNew + CMS中，是直接触发Full GC，而在G1中是触发mixed gc。

在G1中，首先会进行ygc，ygc会选择所有新生代的分区进行回收。当程序不断运行，存活对象越来越多，老年代的对象越来越多的时候，就会在某次ygc的时候，触发一个并发标记过程。然后等待ygc和并发标记过程结束之后，就会真实进入mixed gc，mixed gc会从老年代中选择部分回收价值比较高的region，进行回收，以满足用户设置的MaxGCPauseMills值，当mixed gc之后，对象还是无法分配成功的时候，就会触发full gc，Full gc会暂停程序运行，对整个堆进行全面的垃圾回收，包括新生代，老年代，大对象等。

Ygc mixedgc full gc之间的过程转换关系图如下图所示：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/1084100_1644670281.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

3、YGC使用的算法是什么算法？新生代的垃圾回收流程是怎样的？

YGC使用的算法是复制算法。意思就是，把新生代的所有region按照Eden，Survivor做类型标识。在执行垃圾回收的时候，首先对存活对象进行标记，然后把存活对象复制到Survivor区域，紧接着就把所有的垃圾对象全部回收掉。

注意的是，G1的region分布，对于一个代，不一定是连续。

（1）不均匀的分区分布

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/2522600_1644670281.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

（2）对象在Eden区的分布

 

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/1541000_1644670281.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

（3）Eden区占满的时候，触发ygc

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/3883800_1644670281.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

（4）标记存活对象

首先是要从GC roots出发，标记存活对象。然后呢，再一个一个标记gc roots讲解引用的对象。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/55903600_1644670281.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

（5）复制存活对象到Survivor区

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/57470600_1644670281.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

（6）回收垃圾对象

如果说是JVM的ParNew + CMS到这里新生代的回收基本上算是结束了。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/58750100_1644670281.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

（7）动态调整新生代区域region数量

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/57991500_1644670281.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

我回收掉的哪些region，有可能是要成为自由分区。什么意思，因为我们回收之后，会动态判断。如果需要更多的region，我们需要增加，如果回收时间太长，发现我们ygc下一次可能没有那么强的能力，那就只能，减少几个region。

 

（8）是否需要开启并发标记？如果需要，就开启并发标记

（9）新生代的垃圾回收流程结束

 

这个就是G1新生代垃圾回收的基本流程，但是其实大家页能发现，我们这节课好像没有设计到我们前面说的Rset啊，卡表啊等等这些东西的修改，使用什么的。

我们知道Rset肯定是需要用到的，并且在对象复制了之后，Rset肯定是有更新的，那么历用RSet找到新生代的对象，以及对象复制后，Rset的更新操作，及卡表的更新操作到底是在哪里的呢？下节课，我们会深入到YGC的一些细节中区，把整个YGC设计到的一些细节给大家讲清楚。