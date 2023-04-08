回顾：

1、YGC的过程：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/47969200_1641817212.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

（1）暂停系统程序运行

（2）选择需要回收的region

（3）标记所有存活对象

（4）复制对象操作

（5）清理垃圾，释放region

2、mixed gc有那些步骤？

3、young gc和mixed gc的关系

4、mixed gc的并发标记是从那些对象开始的？

5、思考题：mixed gc（混合回收）的标记还需要做哪些内容？

本节内容：

1、Young gc的一些细节调整

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/49777100_1641817212.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

实际上，大家可以看到，由于在前面讲解Young gc的时候，为了方便理解，我们直接在最后一步判断是否开启并发标记，可以开启，就开启，不能开启就直接过。

 

实际上这个步骤师不准确的，实际上，要在YGC开始的时候就判断一下本次是否要尝试开启并发标记，为什么呢？大家想，如果说本次老年代 + 本次分配的对象 超过了45%的堆内存，是不是大概率需要开启并发标记？那我这个时候就直接给这个flag标记设置为可以开启，把一些前置的工作提前做一做。然后在ygc结束之后，直接去判断一些其他的条件，就去尝试启动一个并发的线程就行了。

2、在YGC开始前判断的另外的一个原因

上节课我们说，并发标记阶段的起始为止，要从survivor + 老年代RSet + gc roots的一部分引用对象。

那ygc既然是mixed GC的前置部分，在YGC开启前判断，我是不是就能决定，是否把gc roots引用的老年代的对象做一下处理，留待mixed gc的并发标记线程启动的时候使用？如果不需要开启，我就不需要去做这些处理了对不对？

所以，其实，在ygc的gc roots标记进行的时候，其实还是有不同的情况的，当需要开启并发标记的时候，会走一套操作，不需要开启的时候，是另外一套操作，简单来说，就是ygc是否需要关注gc roots直接引用的老年代的对象，我到底要不要管，要不要处理的事儿。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/50258200_1641817212.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

3、YGC为什么可以作为mixed gc的初始标记阶段？

到了这里其实逻辑就比较简单了，大家想啊，在YGC阶段，它首先会判断是否需要开启并发标记，如果需要开启，对于新生代相关的gc roots引用的对象，最终一定会被放入到survivor区中。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/46395200_1641817212.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

同时，一些直接被gc roots引用的老年代对象，一定会被记录下来。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/47263600_1641817212.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/47833600_1641817212.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

 

那么最终，所有的gc roots直接引用的对象一定都会被标记出来。这样一来，在ygc阶段，其实就可以满足把初始标记过程中所有需要的对象全部都标记出来。因此ygc可以做为mixed gc的初始阶段。

 

4、借助survivor + gc roots记录即可完成老年代的标记

此时，我们想啊，除了额外对引用了老年代对象的gc roots做一些特殊处理，我们是不是就可以借助survivor + 老年代gc roots来作为mixed 回收中并发标记的起点。

因为我们用他们作为起点，是一定能找到所有的存活的对象的。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/49421300_1641817212.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

 

5、思考题：

Ygc是需要对所有新生代空间做全部遍历，回收的。Mixed gc是否需要对整个老年代进行回收？如果不需要，我们应该怎么才能避免对整个老年代的遍历？