回顾：

1、为什么要使用位图？

2、位图引入的前奏：Bottom 、Prev、Next、Top指针

3、到底引入了哪些位图？

4、思考

这种接续的思想，大家可以在哪里运用？如果运用的话，可以从哪些方面来考虑？什么时候清理？什么时候使用？有没有具体的场景可以使用？结合自己的系统，或者业务去思考思考这种思路该如何运用。欢迎各位同学在交流群中相互交流。

 

本节内容：

把发生了两次标记的场景用图示的方式演示出来

1、在第一次进入并发标记前（已经完成了初始标记）

此时，第一次进入并发标记状态，意味着，我们的bottom指针和top指针的位置已经确定好了。就是region的起始位置，和当前已经使用的内存的结束地址。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/13758200_1641817669.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

那么此时PrevBitMap肯定是空的，并且prev指针，next指针的位置和bottom、top位置时一致的，如下图所示：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/15529500_1641817669.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

并且NextBitMap也是空的。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/17139000_1641817669.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

2、并发标记开始后

程序不断运行，创建新的对象，同时并发标记开始进行。那么位图会不断被补充。并且top指针会不断移动。因为这是第一次标记，PrevBitMap是不需要发生任何改变的。

只需要在NextBitMap中标记信息就好了。如下图所示：

在标记进行的同时，prev指针也会不断的迁移。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/16150900_1641817669.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

假如说此时标记失败。那么只能进入到下一次并发标记。那么此时，就会把NextBitMap给到PrevBitMap。用于下一次并发标记使用。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/18155800_1641817669.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

3、进入第二次并发标记

进入第二次并发标记的时候，next指针会变化到top指针的位置。而prev指针就是上一次并发标记指向的位置。

 

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/19785800_1641817669.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

 

那么此时NextBitMap是不是就可以使用一些PrevBitMap中的一些标记信息？

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/19207400_1641817669.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

 

然后继续并发标记的后续过程。把所有的next前的对象都给标记上。并且还在不断创建新的对象。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/20207600_1641817669.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

 

此时并发标记就接近尾声了。但是还有一个问题：其实本次并发标记和上一次并发标记过程，对于前面（prevBitMap）中标记的标记状态是有可能不一致的。因为对象的状态可能从存活变成了死亡。所以最终的状态很可能是这样的：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/20901900_1641817669.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

那么，这种问题，怎么解决呢？我们怎么才能沿用上一次标记的状态呢？同时又能保证标记是准确的，这个是一个非常难解决的问题。

 

其实这个问题，也是G1里面，并发标记阶段，最难解决的问题。引入了一个标记策略，叫三色标记法来标记解决。

同时也引入了一个STAP快照机制，来解决这个问题。

 

这个问题留给大家思考，下节课我们来详细分析，怎么保证并发标记的nextbitmap一定是正确的！并且怎么使用的PrevBitMap来提升整体的标记效率