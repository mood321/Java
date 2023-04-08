回顾：

1、初始标记阶段给了我们什么东西？

2、一个简单的思路：G1对增量对象的处理

3、对象分配的连续性的特性

4、指针法来确定增量对象的范围

5、思考：如何提升标记效率呢？Mixed gc可是相当于需要遍历全部堆内存里面的对象的，如果说对每个对象打一个标签的话，虽然可行，但是在垃圾回收的时候怎么办？继续遍历对象吗？

答：时间久了，大家都轻车熟路了，其实很简单，借助一些额外的存储结构来描述，所以这里使用了位图的数据结构。Bitmap

 

本节内容：

1、为什么要使用位图？

首先我们按照上节课讲的内容，一个region是一块儿连续的内存，并且里面的对象分配，TLAB分配都是连续的，如下图所示。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/34449900_1641817510.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

基于这个图我们来分析一下，如果说没有位图，我们在每次标记的时候，对于一个region的处理，只能是从头开始进行标记，因为我们不知道它到底哪些地方被标记了，哪些地方没有被标记，即使我们使用了top指针，end指针，能找到并发标记开启前的对象使用位置，也无法知道具体哪些对象被标记了，哪些没有被标记，要想知道，还是只能遍历所有对象。

因此我们需要一组数据告诉我们到底哪些内存被使用了，对象是否被标记，标记的状态时啥？

所以，为什么要使用一个额外的数据结构来描述？其实就是为了我们如果想要拿到标记情况，或者根据标记情况来做清除操作，如果不记录的话，即使标记过，也只能遍历的方式，所以，我们需要把标记的内容记录下来。而位图就是比较合适的数据结构。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/35515500_1641817510.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

这样子的话，只要我标记好了，并且没有新的改变，我每次都可以拿到标记的数据直接用。

 

2、位图引入的前奏：Bottom 、Prev、Next、Top指针

我们前面已经知道了top指针和end指针是怎么回事。其实关于位图和并发标记过程的内存情况，还有另外几个指针。几个指针分别代表几个不同的含义：

 

Bottom指针：region中内存使用的起始地址，就是一个region从哪里开始的

Top指针：region中内存使用的结束地址

Prev指针：上一次并发处理的之后处理到的地址，假如有两次并发标记，我第一次处理到了Prev位置

Next指针：并发标记开始之前已经使用的内存结束地址

End指针：region的结束地址

Bottom和end其实就是region的起止地址，这个比较好理解。

Top指针一直会跟随新对象分配的最新地址，这个咱们在对象分配的时候已经有讲解。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/72080900_1641817510.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

那么Prev指针是指什么意思呢？简单来说就是，并发标记过程，是有可能进行失败的。也就是说，每一次并发标记，并不一定是绝对都会走到最后成功的。那么它就有可能会出现标记了一半儿，然后标记终止，这个指针就是记录上一次并发标记的标记到的位置的。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/72076300_1641817510.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

Next指针，其实也比较好理解，简单来说就是，每次并发标记开始的时候，它就指向top。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/74558700_1641817510.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

并发标记开启之后，它一直不变，top指针会随着新对象创建而移动。那么Next -> top这段内存，其实就是并发标记过程中新创建对象的内存范围。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/74826600_1641817510.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

大家注意看，如果说又进入了一次并发标记过程，那么此时top指针指向的所示最新的对象使用的内存结束地址，next指针还是指向上一次top指针指向的位置，也就是并发标记开始前的top指针的位置。

 

3、到底引入了哪些位图？

首先第一个位图：从bottom指针到prev指针之间的内存区域，有一个PrevBitMap，这个位图记录了什么东西呢？

简单来说就是，上一次并发标记标记到的内存范围，这个位图里面，记录了从bottom到prev这块儿内存所有对象的标记状态，是否标记啊，是否存活呀。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/77644200_1641817510.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

第二个位图：从bottom指针到NextBitMap的内存使用的标记状态。如图所示：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/78343700_1641817510.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

NextBitMap其实记录了本次并发标记过程中整个region，从开始到开启并发标记那个时刻的所有的标记状态。也就是bottom到next指针这个范围内的内存使用的标记状态。

看到这里其实大家可能会奇怪，为什么有了NextBitMap还需要一个PrevBitMap呢？一个NextBitMap就足够了啊，所有的信息都记录下来了啊。为啥还要多余的一个PrevBitMap呢？完全没有意义啊！其实是有意义的，大家想，如果说我们本次并发标记开始时，发现上次并发标记失败了，是不是意味着，我本次并发标记，要从bottom开始来标记，标记到next指针的位置？可是虽然我上次并发标记失败了，依然做出了一些标记操作，为啥这次不能继续使用它，而是自己重新再搞一遍标记呢？完全没必要吧！所以，到这里大家应该也能理解为啥要两个bitmap了。

原因就是：如果说进行了多次并发标记，那么每次最新的并发标记过程，都可以接续上次并发标记的内容继续标记，而不需要重新遍历，大大节省了时间。如下图所示：

如果上一次标记，出现了一些变化，比如说，出现了一些死亡的对象。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/78437800_1641817510.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

4、思考

这种接续的思想，大家可以在哪里运用？如果运用的话，可以从哪些方面来考虑？什么时候清理？什么时候使用？有没有具体的场景可以使用？结合自己的系统，或者业务去思考思考这种思路该如何运用。欢迎各位同学在交流群中相互交流。