<blockquote>
<p>作者：彤哥</p>
<p>出处：<a href="https://www.cnblogs.com/tong-yuan/">https://www.cnblogs.com/tong-yuan/</a></p>
</blockquote>
<hr />
<hr />
<h2><span id="i">概览</span></h2>
<p>我们先来看一看java中所有集合的类关系图。</p>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/collection1.png" alt="qrcode" /></p>
<p>这里面的类太多了，请放大看，如果放大还看不清，请再放大看，如果还是看不清，请放弃。</p>
<p>我们下面主要分成五个部分来逐个击破。</p>
<h2><span id="List">List</span></h2>
<p>List中的元素是有序的、可重复的，主要实现方式有动态数组和链表。</p>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/List.png" alt="qrcode" /></p>
<p>java中提供的List的实现主要有ArrayList、LinkedList、CopyOnWriteArrayList，另外还有两个古老的类Vector和Stack。</p>
<p>关于List相关的问题主要有：</p>
<p>（1）ArrayList和LinkedList有什么区别？</p>
<p>（2）ArrayList是怎么扩容的？</p>
<p>（3）ArrayList插入、删除、查询元素的时间复杂度各是多少？</p>
<p>（4）怎么求两个集合的并集、交集、差集？</p>
<p>（5）ArrayList是怎么实现序列化和反序列化的？</p>
<p>（6）集合的方法toArray()有什么问题？</p>
<p>（7）什么是fail-fast？</p>
<p>（8）LinkedList是单链表还是双链表实现的？</p>
<p>（9）LinkedList除了作为List还有什么用处？</p>
<p>（10）LinkedList插入、删除、查询元素的时间复杂度各是多少？</p>
<p>（11）什么是随机访问？</p>
<p>（12）哪些集合支持随机访问？他们都有哪些共性？</p>
<p>（13）CopyOnWriteArrayList是怎么保证并发安全的？</p>
<p>（14）CopyOnWriteArrayList的实现采用了什么思想？</p>
<p>（15）CopyOnWriteArrayList是不是强一致性的？</p>
<p>（16）CopyOnWriteArrayList适用于什么样的场景？</p>
<p>（17）CopyOnWriteArrayList插入、删除、查询元素的时间复杂度各是多少？</p>
<p>（18）CopyOnWriteArrayList为什么没有size属性？</p>
<p>（19）比较古老的集合Vector和Stack有什么缺陷？</p>
<p>关于List的问题大概就这么多，你都能回答上来吗？</p>
<p>点击下面链接可以直接到相应的章节查看：</p>
<p><a href="http://cmsblogs.com/?p=4727">【死磕 Java 集合】— ArrayList源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4725">【死磕 Java 集合】— LinkedList源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4729">【死磕 Java 集合】— CopyOnWriteArrayList源码分析</a></p>
<h3>list 基于上述问题总结</h3>
<h4>ArrayList 要点</h4>
<p>（1）ArrayList内部使用数组存储元素，当数组长度不够时进行扩容，每次加一半的空间，ArrayList不会进行缩容；</p>
<p>（2）ArrayList支持随机访问，通过索引访问元素极快，时间复杂度为O(1)；</p>
<p>（3）ArrayList添加元素到尾部极快，平均时间复杂度为O(1)；</p>
<p>（4）ArrayList添加元素到中间比较慢，因为要搬移元素，平均时间复杂度为O(n)；</p>
<p>（5）ArrayList从尾部删除元素极快，时间复杂度为O(1)；</p>
<p>（6）ArrayList从中间删除元素比较慢，因为要搬移元素，平均时间复杂度为O(n)；</p>
<p>（7）ArrayList支持求并集，调用addAll(Collection&lt;? extends E> c)方法即可；</p>
<p>（8）ArrayList支持求交集，调用retainAll(Collection&lt;? extends E> c)方法即可；</p>
<p>（9）ArrayList支持求单向差集，调用removeAll(Collection&lt;? extends E> c)方法即可；</p>
<p>（10） ArrayList 实现了List RandomAcces,Cloneable,Serializable </p>
<p>表示ArrayList 具有列表的基础功能,随机访问,复制,序列化</p>
<p>（11）ArrayList 的序列化是自己重写的 他并不会直接序列化elementData（transient） 数组内的值 而是通过size 去取数组实际存在的值 序列化</p>
<p>ArrayList 初始化的时候 默认容器大小为10 实际存储值的数组为空数组 </p>
<h4>LinkedList 要点</h4>
<p>（1）LinkedList是一个以双链表实现的List；</p>
<p>（2）LinkedList还是一个双端队列，具有队列、双端队列、栈的特性；</p>
<p>（3）LinkedList在队列首尾添加、删除元素非常高效，时间复杂度为O(1)；</p>
<p>（4）LinkedList在中间添加、删除元素比较低效，时间复杂度为O(n)；</p>
<p>（5）LinkedList不支持随机访问，所以访问非队列首尾的元素比较低效；</p>
<p>（6）LinkedList在功能上等于ArrayList + ArrayDeque；</p>
<h4>CopyOnWriteArrayList 要点</h4>
<p>（1）CopyOnWriteArrayList使用ReentrantLock重入锁加锁，保证线程安全；</p>
<p>（2）CopyOnWriteArrayList的写操作都要先拷贝一份新数组，在新数组中做修改，修改完了再用新数组替换老数组，所以空间复杂度是O(n)，性能比较低下；</p>
<p>（3）CopyOnWriteArrayList的读操作支持随机访问，时间复杂度为O(1)；</p>
<p>（4）CopyOnWriteArrayList采用读写分离的思想，读操作不加锁，写操作加锁，且写操作占用较大内存空间，所以适用于读多写少的场合；</p>
<p>（5）CopyOnWriteArrayList只保证最终一致性，不保证实时一致性；</p>
<p>（6）CopyOnWriteArrayList size属性</em></p>
<p>因为每次修改都是拷贝一份正好可以存储目标个数元素的数组，所以不需要size属性了，数组的长度就是集合的大小，而不像ArrayList数组的长度实际是要大于集合的大小的。</p>
<p>比如，add(E e)操作，先拷贝一份n+1个元素的数组，再把新元素放到新数组的最后一位，这时新数组的长度为len+1了，也就是集合的size了。</p>
<p>（7）CopyOnWriteArrayList 基于ReentranLock保证了增加元素和删除元素动作的互斥。<strong>每一次写操作（remove，add等相关的）都将会创建数组复制元素，这将造成频繁写极大的性能消耗</strong>。<strong>在读操作上没有加锁，保证了读的性能，但是却会出现脏读的问题（get、iterator、size、isEmpty）</strong>。综上CopyOnWriteArrayList <strong>适合读多写少</strong>，<strong>对实时性不敏感</strong>的应用场景。<br>
允许null元素</p>
<h2><span id="Map">Map</span></h2>
<p>Map是一种(key/value)的映射结构，其它语言里可能称作字典（Dictionary），包括java早期也是叫做字典，Map中的元素是一个key只能对应一个value，不能存在重复的key。</p>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/Map.png" alt="qrcode" /></p>
<p>java中提供的Map的实现主要有HashMap、LinkedHashMap、WeakHashMap、TreeMap、ConcurrentHashMap、ConcurrentSkipListMap，另外还有两个比较古老的Map实现HashTable和Properties。</p>
<p>关于Map的问题主要有：</p>
<p>（1）什么是散列表？</p>
<p>（2）怎么实现一个散列表？</p>
<p>（3）java中HashMap实现方式的演进？</p>
<p>（4）HashMap的容量有什么特点？</p>
<p>（5）HashMap是怎么进行扩容的？</p>
<p>（6）HashMap中的元素是否是有序的？</p>
<p>（7）HashMap何时进行树化？何时进行反树化？</p>
<p>（8）HashMap是怎么进行缩容的？</p>
<p>（9）HashMap插入、删除、查询元素的时间复杂度各是多少？</p>
<p>（10）HashMap中的红黑树实现部分可以用其它数据结构代替吗？</p>
<p>（11）LinkedHashMap是怎么实现的？</p>
<p>（12）LinkedHashMap是有序的吗？怎么个有序法？</p>
<p>（13）LinkedHashMap如何实现LRU缓存淘汰策略？</p>
<p>（14）WeakHashMap使用的数据结构？</p>
<p>（15）WeakHashMap具有什么特性？</p>
<p>（16）WeakHashMap通常用来做什么？</p>
<p>（17）WeakHashMap使用String作为key是需要注意些什么？为什么？</p>
<p>（18）什么是弱引用？</p>
<p>（19）红黑树具有哪些特性？</p>
<p>（20）TreeMap就有序的吗？怎么个有序法？</p>
<p>（21）TreeMap是否需要扩容？</p>
<p>（22）什么是左旋？什么是右旋？</p>
<p>（23）红黑树怎么插入元素？</p>
<p>（24）红黑树怎么删除元素？</p>
<p>（25）为什么要进行平衡？</p>
<p>（26）如何实现红黑树的遍历？</p>
<p>（27）TreeMap中是怎么遍历的？</p>
<p>（28）TreeMap插入、删除、查询元素的时间复杂度各是多少？</p>
<p>（29）HashMap在多线程环境中什么时候会出现问题？</p>
<p>（30）ConcurrentHashMap的存储结构？</p>
<p>（31）ConcurrentHashMap是怎么保证并发安全的？</p>
<p>（32）ConcurrentHashMap是怎么扩容的？</p>
<p>（33）ConcurrentHashMap的size()方法的实现知多少？</p>
<p>（34）ConcurrentHashMap是强一致性的吗？</p>
<p>（35）ConcurrentHashMap不能解决什么问题？</p>
<p>（36）ConcurrentHashMap中哪些地方运用到分段锁的思想？</p>
<p>（37）什么是伪共享？怎么避免伪共享？</p>
<p>（38）什么是跳表？</p>
<p>（40）ConcurrentSkipList是有序的吗？</p>
<p>（41）ConcurrentSkipList是如何保证线程安全的？</p>
<p>（42）ConcurrentSkipList插入、删除、查询元素的时间复杂度各是多少？</p>
<p>（43）ConcurrentSkipList的索引具有什么特性？</p>
<p>（44）为什么Redis选择使用跳表而不是红黑树来实现有序集合？</p>
<p>关于Map的问题大概就这么多，你都能回答上来吗？</p>
<p>点击下面链接可以直接到相应的章节查看：</p>
<p><a href="http://cmsblogs.com/?p=4731">【死磕 Java 集合】— HashMap源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4733">【死磕 Java 集合】— LinkedHashMap源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4735">【死磕 Java 集合】— WeakHashMap源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4737">【死磕 Java 集合】— TreeMap源码分析（一）</a></p>
<p><a href="http://cmsblogs.com/?p=4739">【死磕 Java 集合】— TreeMap源码分析（二）</a></p>
<p><a href="http://cmsblogs.com/?p=4741">【死磕 Java 集合】— TreeMap源码分析（三）</a></p>
<p><a href="http://cmsblogs.com/?p=4743">【死磕 Java 集合】— TreeMap源码分析（四）</a></p>
<p><a href="http://cmsblogs.com/?p=4775">【死磕 Java 集合】— ConcurrentHashMap源码分析（一）</a></p>
<p><a href="http://cmsblogs.com/?p=4777">【死磕 Java 集合】— ConcurrentHashMap源码分析（二）</a></p>
<p><a href="http://cmsblogs.com/?p=4779">【死磕 Java 集合】— ConcurrentHashMap源码分析（三）</a></p>
<p><a href="http://cmsblogs.com/?p=4773">【死磕 Java 集合】— ConcurrentSkipListMap源码分析</a></p>
<h3>list 基于上述问题总结</h3>
<h4>基础概念<h4>
<h4>散列表</h4>
<p>采用key/value存储结构，每个key对应唯一的value，查询和修改的速度都很快，能达到O(1)的平均时间复杂度</p>
<h4>红黑树</h4>
<p>（1）节点是红色或黑色。</p>
<p>（2）根节点是黑色。</p>
<p>（3）每个叶节点（NIL节点，空节点）是黑色的。</p>
<p>（4）每个红色节点的两个子节点都是黑色。(从每个叶子到根的所有路径上不能有两个连续的红色节点)</p>
<p>（5）从任一节点到其每个叶子的所有路径都包含相同数目的黑色节点。</p>
<p>红黑树的时间复杂度为O(log n)，与树的高度成正比。</p>
<p>红黑树每次的插入、删除操作都需要做平衡，平衡时有可能会改变根节点的位置，颜色转换，左旋，右旋等。</p>
<h4>跳表</h4>
<p>跳表是一个随机化的数据结构，实质就是一种可以进行<strong>二分</strong>查找的<strong>有序链表</strong>。</p>
<p>跳表在原有的有序链表上面增加了多级索引，通过索引来实现快速查找。</p>
<p>跳表不仅能提高搜索性能，同时也可以提高插入和删除操作的性能。</p>
<p >（1）跳表是可以实现二分查找的有序链表；</p>
<p >（2）每个元素插入时随机生成它的level；</p>
<p >（3）最低层包含所有的元素；</p>
<p >（4）如果一个元素出现在level(x)，那么它肯定出现在x以下的level中；</p>
<p >（5）每个索引节点包含两个指针，一个向下，一个向右；</p>
<p >（6）跳表查询、插入、删除的时间复杂度为O(log&nbsp;n)，与平衡二叉树接近；</p>

<h4>引用级别</h4>
<p>JDK1.2版本开始，把对象的引用分为四种级别，从而使程序能更加灵活的控制对象的生命周期。这四种级别由高到低依次为：强引用、软引用、弱引用和虚引用</p>
<ul>1．强引用</ul>
    
    以前我们使用的大部分引用实际上都是强引用，这是使用最普遍的引用。如果一个对象具有强引用，那就类似于必不可少的生活用品，垃圾回收器绝不会回收它。
    当内存空间不足，Java虚拟机宁愿抛出OutOfMemoryError错误，使程序异常终止，也不会靠随意回收具有强引用的对象来解决内存不足问题。
    
<ul>2、软引用（SoftReference）</ul>
    
    如果一个对象只具有软引用，那就类似于可有可物的生活用品。如果内存空间足够，垃圾回收器就不会回收它，如果内存空间不足了，就会回收这些对象的内存。
    只要垃圾回收器没有回收它，该对象就可以被程序使用。软引用可用来实现内存敏感的高速缓存
    
<ul> 3．弱引用（WeakReference）</ul>
     
     如果一个对象只具有弱引用，那就类似于可有可物的生活用品。弱引用与软引用的区别在于：只具有弱引用的对象拥有更短暂的生命周期。在垃圾回收器线程扫描它
      所管辖的内存区域的过程中，一旦发现了只具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存。不过，由于垃圾回收器是一个优先级很低的线程， 因此不一定会很快发现那些只具有弱引用的对象。
<ul> 4．虚引用（PhantomReference）</ul>
    
     “虚引用”顾名思义，就是形同虚设，与其他几种引用都不同，虚引用并不会决定对象的生命周期。如果一个对象仅持有虚引用，那么它就和没有任何引用一样，在任何时候都可能被垃圾回收。
     虚引用主要用来跟踪对象被垃圾回收的活动。虚引用与软引用和弱引用的一个区别在于：虚引用必须和引用队列（ReferenceQueue）联合使用。当垃圾回收器准备回收一个对象时，如果发现它还有虚引用，就会在回收对象的内存之前，
     把这个虚引用加入到与之关联的引用队列中。程序可以通过判断引用队列中是否已经加入了虚引用，来了解被引用的对象是否将要被垃圾回收。程序如果发现某个虚引用已经被加入到引用队列，那么就可以在所引用的对象的内存被回收之前采取必要的行动。
     
     特别注意，在实际程序设计中一般很少使用弱引用与虚引用，使用软用的情况较多，这是因为软引用可以加速JVM对垃圾内存的回收速度，可以维护系统的运行安全，防止内存溢出（OutOfMemory）等问题的产生
<ul>总结：
    
    强引用： 
    String str = “abc”; 
    list.add(str); 
    软引用： 
    如果弱引用对象回收完之后，内存还是报警，继续回收软引用对象 
    弱引用： 
    如果虚引用对象回收完之后，内存还是报警，继续回收弱引用对象 
    虚引用： 
    虚拟机的内存不够使用，开始报警，这时候垃圾回收机制开始执行System.gc(); String s = “abc”;如果没有对象回收了， 就回收没虚引用的对象

<h4>伪共享<a href="https://www.jianshu.com/p/7758bb277985">(https://www.jianshu.com/p/7758bb277985)</a></h4>

        定义: 当多线程修改互相独立的变量时，如果这些变量共享同一个缓存行，就会无意中影响彼此的性能，这就是伪共享
        java 解决方案：JDK1.8之前解决方式-padding方式  填充缓存行  一个缓存行 64字节  即 8个long的长度  使变量不分开
           JDK1.8之后解决方式- Contended注解方式  在JDK1.8中，新增了一种注解@sun.misc.Contended，来使各个变量在Cache line中分隔开。
                    注意，jvm需要添加参数-XX:-RestrictContended才能开启此功能  
        ps: 在concurrentHashmap 中各个桶的长度 CounterCell 就使用了这个
<h4>LRU算法 缓存淘汰策略 <a href="https://www.cnblogs.com/Dhouse/p/8615481.html">https://www.cnblogs.com/Dhouse/p/8615481.html</a></h4>

    LRU（Least recently used，最近最少使用）算法根据数据的历史访问记录来进行淘汰数据，其核心思想是“如果数据最近被访问过，那么将来被访问的几率也更高”。
<h4> 二叉树的遍历</h4>

    我们知道二叉查找树的遍历有前序遍历、中序遍历、后序遍历。
    （1）前序遍历，先遍历我，再遍历我的左子节点，最后遍历我的右子节点；
    （2）中序遍历，先遍历我的左子节点，再遍历我，最后遍历我的右子节点；
    （3）后序遍历，先遍历我的左子节点，再遍历我的右子节点，最后遍历我；
<p>
<h4> HashMap 要点</h4>
<p>（1）HashMap是一种散列表，采用（数组 + 链表 + 红黑树）的存储结构；</p>
<p>（2）HashMap的默认初始容量为16（1&lt;&lt;4），默认装载因子为0.75f，容量总是2的n次方；</p>
<p>（3）HashMap扩容时每次容量变为原来的两倍；</p>
<p>（4）当桶的数量小于64时不会进行树化，只会扩容；</p>
<p>（5）当桶的数量大于64且单个桶中元素的数量大于8时，进行树化；</p>
<p>（6）当单个桶中元素数量小于6时，进行反树化；</p>
<p>（7）HashMap是非线程安全的容器；</p>
<p>（8）HashMap查找添加元素的时间复杂度都为O(1)；</p>
<h4> LinkedHashMap 要点</h4>
<p>（1）LinkedHashMap继承自HashMap，具有HashMap的所有特性；</p>
<p>（2）LinkedHashMap内部维护了一个双向链表存储所有的元素；</p>
<p>（3）如果accessOrder为false，则可以按插入元素的顺序遍历元素；</p>
<p>（4）如果accessOrder为true，则可以按访问元素的顺序遍历元素；</p>
<p>（5）LinkedHashMap的实现非常精妙，很多方法都是在HashMap中留的钩子（Hook），直接实现这些Hook就可以实现对应的功能了，并不需要再重写put()等方法；</p>
<p>（6）默认的LinkedHashMap并不会移除旧元素，如果需要移除旧元素，则需要重写removeEldestEntry()方法设定移除策略；</p>
<p>（7）LinkedHashMap可以用来实现LRU缓存淘汰策略；</p>
<h4> WeakHashMap 要点</h4>
<p>（1）WeakHashMap使用（数组 + 链表）存储结构；</p>
<p>（2）WeakHashMap中的key是弱引用，gc的时候会被清除；</p>
<p>（3）每次对map的操作都会剔除失效key对应的Entry；</p>
<p>（4）使用String作为key时，一定要使用new String()这样的方式声明key，才会失效，其它的基本类型的包装类型是一样的；</p>
<p>（5）WeakHashMap常用来作为缓存使用；</p>
<h4> TreeMap 要点</h4>
<p>（1）每个节点或者是黑色，或者是红色。</p>
<p>（2）根节点是黑色。</p>
<p>（3）每个叶子节点（NIL）是黑色。（注意：这里叶子节点，是指为空(NIL或NULL)的叶子节点！）</p>
<p>（4）如果一个节点是红色的，则它的子节点必须是黑色的。</p>
<p>（5）从一个节点到该节点的子孙节点的所有路径上包含相同数目的黑节点。</p>
<p>除了上述这些标准的红黑树的特性，你还能讲出来哪些TreeMap的特性呢？</p>
<p>（1）TreeMap的存储结构只有一颗红黑树；</p>
<p>（2）TreeMap中的元素是有序的，按key的顺序排列；</p>
<p>（3）TreeMap比HashMap要慢一些，因为HashMap前面还做了一层桶，寻找元素要快很多；</p>
<p>（4）TreeMap没有扩容的概念；</p>
<p>（5）TreeMap的遍历不是采用传统的递归式遍历；</p>
<p>（6）TreeMap可以按范围查找元素，查找最近的元素；</p>

 <h4>  ConcurrentHashMap 要点</h4>
 <p>（1）ConcurrentHashMap是HashMap的线程安全版本；</p>
 <p>（2）ConcurrentHashMap采用（数组 + 链表 + 红黑树）的结构存储元素；</p>
 <p>（3）ConcurrentHashMap相比于同样线程安全的HashTable，效率要高很多；</p>
 <p>（4）ConcurrentHashMap采用的锁有 synchronized，CAS，自旋锁，分段锁，volatile等；</p>
 <p>（5）ConcurrentHashMap中没有threshold和loadFactor这两个字段，而是采用sizeCtl来控制；</p>
 <p>（6）sizeCtl = -1，表示正在进行初始化；</p>
 <p>（7）sizeCtl = 0，默认值，表示后续在真正初始化的时候使用默认容量；</p>
 <p>（8）sizeCtl > 0，在初始化之前存储的是传入的容量，在初始化或扩容后存储的是下一次的扩容门槛；</p>
 <p>（9）sizeCtl = (resizeStamp &lt;&lt; 16) + (1 + nThreads)，表示正在进行扩容，高位存储扩容邮戳，低位存储扩容线程数加1；</p>
 <p>（10）更新操作时如果正在进行扩容，当前线程协助扩容；</p>
 <p>（11）更新操作会采用synchronized锁住当前桶的第一个元素，这是分段锁的思想；</p>
 <p>（12）整个扩容过程都是通过CAS控制sizeCtl这个字段来进行的，这很关键；</p>
 <p>（13）迁移完元素的桶会放置一个ForwardingNode节点，以标识该桶迁移完毕；</p>
 <p>（14）元素个数的存储也是采用的分段思想，类似于LongAdder的实现；</p>
 <p>（15）元素个数的更新会把不同的线程hash到不同的段上，减少资源争用；</p>
 <p>（16）元素个数的更新如果还是出现多个线程同时更新一个段，则会扩容段（CounterCell）；</p>
 <p>（17）获取元素个数是把所有的段（包括baseCount和CounterCell）相加起来得到的；</p>
 <p>（18）查询操作是不会加锁的，所以ConcurrentHashMap不是强一致性的；</p>
 <p>（19）ConcurrentHashMap中不能存储key或value为null的元素；</p>
 <h5><span id="i-5">ConcurrentHashMap 值得学习的技术</span></h5>
 <p>ConcurrentHashMap中有哪些值得学习的技术呢？</p>
 <p>（1）CAS + 自旋，乐观锁的思想，减少线程上下文切换的时间；</p>
 <p>（2）分段锁的思想，减少同一把锁争用带来的低效问题；</p>
 <p>（3）CounterCell，分段存储元素个数，减少多线程同时更新一个字段带来的低效；</p>
 <p>（4）@sun.misc.Contended（CounterCell上的注解），避免伪共享；（p.s.伪共享我们后面也会讲的^^）</p>
 <p>（5）多线程协同进行扩容；</p>
 
<h2><span id="Set">Set</span></h2>
<p>java里面的Set对应于数学概念上的集合，里面的元素是不可重复的，通常使用Map或者List来实现。</p>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/Set.png" alt="qrcode" /></p>
<p>java中提供的Set的实现主要有HashSet、LinkedHashSet、TreeSet、CopyOnWriteArraySet、ConcurrentSkipSet。</p>
<p>关于Set的问题主要有：</p>
<p>（1）HashSet怎么保证添加元素不重复？</p>
<p>（2）HashSet是有序的吗？</p>
<p>（3）HashSet是否允许null元素？</p>
<p>（4）Set是否有get()方法？</p>
<p>（5）LinkedHashSet是有序的吗？怎么个有序法？</p>
<p>（6）LinkedHashSet支持按元素访问顺序排序吗？</p>
<p>（8）TreeSet真的是使用TreeMap来存储元素的吗？</p>
<p>（9）TreeSet是有序的吗？怎么个有序法？</p>
<p>（10）TreeSet和LinkedHashSet有何不同？</p>
<p>（11）TreeSet和SortedSet有什么区别和联系？</p>
<p>（12）CopyOnWriteArraySet是用Map实现的吗？</p>
<p>（13）CopyOnWriteArraySet是有序的吗？怎么个有序法？</p>
<p>（14）CopyOnWriteArraySet怎么保证并发安全？</p>
<p>（15）CopyOnWriteArraySet以何种方式保证元素不重复？</p>
<p>（16）如何比较两个Set中的元素是否完全一致？</p>
<p>（17）ConcurrentSkipListSet的底层是ConcurrentSkipListMap吗？</p>
<p>（18）ConcurrentSkipListSet是有序的吗？怎么个有序法？</p>
<p>关于Set的问题大概就这么多，你都能回答上来吗？</p>
<p>点击下面链接可以直接到相应的章节查看：</p>
<p><a href="http://cmsblogs.com/?p=4745">【死磕 Java 集合】— HashSet源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4747">【死磕 Java 集合】— LinkedHashSet源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4749">【死磕 Java 集合】— TreeSet源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4751">【死磕 Java 集合】— CopyOnWriteArraySet源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4753">【死磕 Java 集合】— ConcurrentSkipListSet源码分析</a></p>
 
 <h4>  HashSet 要点</h4>
 <p>（1）HashSet内部使用HashMap的key存储元素，以此来保证元素不重复；</p>
 <p>（2）HashSet是无序的，因为HashMap的key是无序的；</p>
 <p>（3）HashSet中允许有一个null元素，因为HashMap允许key为null；</p>
 <p>（4）HashSet是非线程安全的；</p>
 <p>（5）HashSet是没有get()方法的；</p>
  
 <h4>  LinkedHashSet 要点</h4>
<p>（1）LinkedHashSet的底层使用LinkedHashMap存储元素。</p>
<p>（2）LinkedHashSet是有序的，它是按照插入的顺序排序的。</p>

 <h4>  TreeSet 要点</h4>
 <p>（1）TreeSet底层使用NavigableMap存储元素；</p>
 <p>（2）TreeSet是有序的；</p>
 <p>（3）TreeSet是非线程安全的；</p>
 <p>（4）TreeSet实现了NavigableSet接口，而NavigableSet继承自SortedSet接口；</p>
 <p>（5）TreeSet实现了SortedSet接口；</p>
 
  <h4>  TreeSet 要点</h4>
  <p>（1）CopyOnWriteArraySet是用CopyOnWriteArrayList实现的；</p>
  <p>（2）CopyOnWriteArraySet是有序的，因为底层其实是数组，数组是不是有序的？！</p>
  <p>（3）CopyOnWriteArraySet是并发安全的，而且实现了读写分离；</p>
  <p>（4）CopyOnWriteArraySet通过调用CopyOnWriteArrayList的addIfAbsent()方法来保证元素不重复；</p>

  <h4>  ConcurrentSkipListSet 要点</h4>
 <p>（1）ConcurrentSkipListSet底层是使用ConcurrentNavigableMap实现的；</p>
 <p>（2）ConcurrentSkipListSet有序的，基于元素的自然排序或者通过比较器确定的顺序；</p>
 <p>（3）ConcurrentSkipListSet是线程安全的；</p>
 <h4>Set大汇总：</h4>
 <table>
 <thead>
 <tr>
 <th>Set</th>
 <th>有序性</th>
 <th>线程安全</th>
 <th>底层实现</th>
 <th>关键接口</th>
 <th>特点</th>
 </tr>
 </thead>
 <tbody>
 <tr>
 <td>HashSet</td>
 <td>无</td>
 <td>否</td>
 <td>HashMap</td>
 <td>无</td>
 <td>简单</td>
 </tr>
 <tr>
 <td>LinkedHashSet</td>
 <td>有</td>
 <td>否</td>
 <td>LinkedHashMap</td>
 <td>无</td>
 <td>插入顺序</td>
 </tr>
 <tr>
 <td>TreeSet</td>
 <td>有</td>
 <td>否</td>
 <td>NavigableMap</td>
 <td>NavigableSet</td>
 <td>自然顺序</td>
 </tr>
 <tr>
 <td>CopyOnWriteArraySet</td>
 <td>有</td>
 <td>是</td>
 <td>CopyOnWriteArrayList</td>
 <td>无</td>
 <td>插入顺序，读写分离</td>
 </tr>
 <tr>
 <td>ConcurrentSkipListSet</td>
 <td>有</td>
 <td>是</td>
 <td>ConcurrentNavigableMap</td>
 <td>NavigableSet</td>
 <td>自然顺序</td>
 </tr>
 </tbody>
 </table>
 <p>从中我们可以发现一些规律：</p>
 <p>（1）除了HashSet其它Set都是有序的；</p>
 <p>（2）实现了NavigableSet或者SortedSet接口的都是自然顺序的；</p>
 <p>（3）使用并发安全的集合实现的Set也是并发安全的；</p>
 <p>（4）TreeSet虽然不是全部都是使用的TreeMap实现的，但其实都是跟TreeMap相关的（TreeMap的子Map中组合了TreeMap）；</p>
 <p>（5）ConcurrentSkipListSet虽然不是全部都是使用的ConcurrentSkipListMap实现的，但其实都是跟ConcurrentSkipListMap相关的（ConcurrentSkipListeMap的子Map中组合了ConcurrentSkipListMap）；</p>

 
<h2><span id="Queue">Queue</span></h2>
<p>Queue是一种叫做队列的数据结构，队列是遵循着一定原则的入队出队操作的集合，一般来说，入队是在队列尾添加元素，出队是在队列头删除元素，但是，也不一定，比如优先级队列的原则就稍微有些不同。</p>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/Queue.png" alt="qrcode" /></p>
<p>java中提供的Queue的实现主要有PriorityQueue、ArrayBlockingQueue、LinkedBlockingQueue、SynchronousQueue、PriorityBlockingQueue、LinkedTransferQueue、DelayQueue、ConcurrentLinkedQueue。</p>
<p>关于Queue的问题主要有：</p>
<p>（1）什么是堆？什么是堆化？</p>
<p>（2）什么是优先级队列？</p>
<p>（3）PriorityQueue是怎么实现的？</p>
<p>（4）PriorityQueue是有序的吗？</p>
<p>（5）PriorityQueue入队、出队的时间复杂度各是多少？</p>
<p>（6）PriorityQueue是否需要扩容？扩容规则呢？</p>
<p>（7）ArrayBlockingQueue的实现方式？</p>
<p>（8）ArrayBlockingQueue是否需要扩容？</p>
<p>（9）ArrayBlockingQueue怎么保证线程安全？</p>
<p>（9）ArrayBlockingQueue有什么缺点？</p>
<p>（10）LinkedBlockingQueue的实现方式？</p>
<p>（11）LinkedBlockingQueue是有界的还是无界的队列？</p>
<p>（12）LinkedBlockingQueue怎么保证线程安全？</p>
<p>（13）LinkedBlockingQueue与ArrayBlockingQueue对比？</p>
<p>（14）SynchronousQueue的实现方式？</p>
<p>（15）SynchronousQueue真的是无缓冲的吗？</p>
<p>（16）SynchronousQueue怎么保证线程安全？</p>
<p>（17）SynchronousQueue的公平模式和非公平模式有什么区别？</p>
<p>（18）SynchronousQueue在高并发情景下会有什么问题？</p>
<p>（19）PriorityBlockingQueue的实现方式？</p>
<p>（20）PriorityBlockingQueue是否需要扩容？</p>
<p>（21）PriorityBlockingQueue怎么保证线程安全？</p>
<p>（22）PriorityBlockingQueue为什么不需要notFull条件？</p>
<p>（23）什么是双重队列？</p>
<p>（24）LinkedTransferQueue是怎么实现阻塞队列的？</p>
<p>（25）LinkedTransferQueue是怎么控制并发安全的？</p>
<p>（26）LinkedTransferQueue与SynchronousQueue有什么异同？</p>
<p>（27）ConcurrentLinkedQueue是阻塞队列吗？</p>
<p>（28）ConcurrentLinkedQueue如何保证并发安全？</p>
<p>（29）ConcurrentLinkedQueue能用于线程池吗？</p>
<p>（30）DelayQueue是阻塞队列吗？</p>
<p>（31）DelayQueue的实现方式？</p>
<p>（32）DelayQueue主要用于什么场景？</p>
<p>关于Queue的问题大概就这么多，你都能回答上来吗？</p>
<p>点击下面链接可以直接到相应的章节查看：</p>
<p><a href="http://cmsblogs.com/?p=4757">【死磕 Java 集合】— PriorityQueue源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4755">【死磕 Java 集合】— ArrayBlockingQueue源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4759">【死磕 Java 集合】— LinkedBlockingQueue源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4761">【死磕 Java 集合】— SynchronousQueue源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4763">【死磕 Java 集合】— PriorityBlockingQueue源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4765">【死磕 Java 集合】— LinkedTransferQueue源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4767">【死磕 Java 集合】— ConcurrentLinkedQueue源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4769">【死磕 Java 集合】— DelayQueue源码分析</a></p>
<h4>基础概念</p>
<h4> 堆 </h4>
<p>堆是一种特殊的树，只要满足下面两个条件，它就是一个堆：</p>
<p>（1）堆是一颗完全二叉树；</p>
<p>（2）堆中某个节点的值总是不大于（或不小于）其父节点的值。</p>
<p>其中，我们把根节点最大的堆叫做大顶堆，根节点最小的堆叫做小顶堆。</p>
<p>一般用数组实现 </p>
<ul> 满二叉树</ul>
    
    满二叉树是指所有层都达到最大节点数的二叉树
<ul> 完全二叉树</ul>
    
    完全二叉树是指除了最后一层其它层都达到最大节点数，且最后一层节点都靠左排列
<p>插入元素时进行的堆化，也叫自下而上的堆化</p>
<p>删除元素时进行的堆化，也叫自上而下的堆化</p>
<p>（1）堆是一颗完全二叉树；</p>
<p>（2）小（大）顶堆中的每一个节点都不小于（不大于）它的父节点；</p>
<p>（3）堆的插入、删除元素的时间复杂度都是O(log n)；</p>
<p>（4）建堆的时间复杂度是O(n)；</p>
<p>（5）堆排序的时间复杂度是O(nlog n)；</p>
<p>（6）堆排序的空间复杂度是O(1)；</p>

<h4> PriorityQueue 要点</h4>
<p>（1）PriorityQueue是一个小顶堆；</p>
<p>（2）PriorityQueue是非线程安全的；</p>
<p>（3）PriorityQueue不是有序的，只有堆顶存储着最小的元素；</p>
<p>（4）入队就是堆的插入元素的实现；</p>
<p>（5）出队就是堆的删除元素的实现；</p>
<p>（6）还不懂堆？看一看这篇文章【<a href="https://mp.weixin.qq.com/s/AF2tMHfofG8b51yIyaIReg">拜托，面试别再问我堆（排序）了！</a>】。</p>
<p>（7） 默认容量是11 旧容量小于64时，容量翻倍  旧容量大于等于64，容量只增加旧容量的一半 </p>


<h4> ArrayBlockingQueue 要点</h4>
<p>（1）ArrayBlockingQueue不需要扩容，因为是初始化时指定容量，并循环利用数组；</p>
<p>（2）ArrayBlockingQueue利用takeIndex和putIndex循环利用数组；</p>
<p>（3）入队和出队各定义了四组方法为满足不同的用途；</p>
<p>（4）利用重入锁和两个条件保证并发安全；</p>

<h4> LinkedBlockingQueue 要点</p>
<p>（1）LinkedBlockingQueue采用单链表的形式实现；</p>
<p>（2）LinkedBlockingQueue采用两把锁的锁分离技术实现入队出队互不阻塞；</p>
<p>（3）LinkedBlockingQueue是有界队列，不传入容量时默认为最大int值；</p>

<p>（1）LinkedBlockingQueue与ArrayBlockingQueue对比？</p>
<p>a）后者入队出队采用一把锁，导致入队出队相互阻塞，效率低下；</p>
<p>b）前才入队出队采用两把锁，入队出队互不干扰，效率较高；</p>
<p>c）二者都是有界队列，如果长度相等且出队速度跟不上入队速度，都会导致大量线程阻塞；</p>
<p>d）前者如果初始化不传入初始容量，则使用最大int值，如果出队速度跟不上入队速度，会导致队列特别长，占用大量内存；</p>


<h4> SynchronousQueue 要点</h4>
<p>（1）SynchronousQueue是java里的无缓冲队列，用于在两个线程之间直接移交元素；</p>
<p>（2）SynchronousQueue有两种实现方式，一种是公平（队列）方式，一种是非公平（栈）方式；</p>
<p>（3）栈方式中的节点有三种模式：生产者、消费者、正在匹配中；</p>
<p>（4）栈方式的大致思路是如果栈顶元素跟自己一样的模式就入栈并等待被匹配，否则就匹配，匹配到了就返回；</p>
<p>（5）队列方式的大致思路是QNode中有一个字段是isData 如果模式相同或空队列时进行等待操作，互补的情况下就进行消费操作。</p>
<h5><span id="i-11"> 其他</span></h5>
<p>（1）SynchronousQueue真的是无缓冲的队列吗？</p>
<p>通过源码分析，我们可以发现其实SynchronousQueue内部或者使用栈或者使用队列来存储包含线程和元素值的节点，如果同一个模式的节点过多的话，它们都会存储进来，且都会阻塞着，所以，严格上来说，SynchronousQueue并不能算是一个无缓冲队列。</p>
<p>（2）SynchronousQueue有什么缺点呢？</p>
<p>试想一下，如果有多个生产者，但只有一个消费者，如果消费者处理不过来，是不是生产者都会阻塞起来？反之亦然。</p>
<p>这是一件很危险的事，所以，SynchronousQueue一般用于生产、消费的速度大致相当的情况，这样才不会导致系统中过多的线程处于阻塞状态。</p>

<h4> PriorityBlockingQueue 要点</h4>
<p>（1）PriorityBlockingQueue整个入队出队的过程与PriorityQueue基本是保持一致的；</p>
<p>（2）PriorityBlockingQueue使用一个锁+一个notEmpty条件控制并发安全；</p>
<p>（3）PriorityBlockingQueue扩容时使用一个单独变量的CAS操作来控制只有一个线程进行扩容；</p>
<p>（4）入队使用自下而上的堆化；</p>
<p>（5）出队使用自上而下的堆化；</p>
<h5><span id="i-10">其他</span></h5>
<p>为什么PriorityBlockingQueue不需要notFull条件？</p>
<p>因为PriorityBlockingQueue在入队的时候如果没有空间了是会自动扩容的，也就不存在队列满了的状态，也就是不需要等待通知队列不满了可以放元素了，所以也就不需要notFull条件了。</p>
	
	
<h4> LinkedTransferQueue 要点</h4>
<pre><code>a）NOW，立即返回，没有匹配到立即返回，不做入队操作
    对应的方法有：poll()、tryTransfer(e)
b）ASYNC，异步，元素入队但当前线程不会阻塞（相当于无界LinkedBlockingQueue的元素入队）
    对应的方法有：add(e)、offer(e)、put(e)、offer(e, timeout, unit)
c）SYNC，同步，元素入队后当前线程阻塞，等待被匹配到
    对应的方法有：take()、transfer(e)
d）TIMED，有超时，元素入队后等待一段时间被匹配，时间到了还没匹配到就返回元素本身
    对应的方法有：poll(timeout, unit)、tryTransfer(e, timeout, unit)
</code></pre>
<p>（1）LinkedTransferQueue可以看作LinkedBlockingQueue、SynchronousQueue（公平模式）、ConcurrentLinkedQueue三者的集合体；</p>
<p>（2）LinkedTransferQueue的实现方式是使用一种叫做<code>双重队列</code>的数据结构；</p>
<p>（3）不管是取元素还是放元素都会入队；</p>
<p>（4）先尝试跟头节点比较，如果二者模式不一样，就匹配它们，组成CP，然后返回对方的值；</p>
<p>（5）如果二者模式一样，就入队，并自旋或阻塞等待被唤醒；</p>
<p>（6）至于是否入队及阻塞有四种模式，NOW、ASYNC、SYNC、TIMED；</p>
<p>（7）LinkedTransferQueue全程都没有使用synchronized、重入锁等比较重的锁，基本是通过 自旋+CAS 实现；</p>
<p>（8）对于入队之后，先自旋一定次数后再调用LockSupport.park()或LockSupport.parkNanos阻塞；</p>
<h5><span id="i-13">其他</span></h5>
<p>LinkedTransferQueue与SynchronousQueue（公平模式）有什么异同呢？</p>
<p>（1）在java8中两者的实现方式基本一致，都是使用的双重队列；</p>
<p>（2）前者完全实现了后者，但比后者更灵活；</p>
<p>（3）后者不管放元素还是取元素，如果没有可匹配的元素，所在的线程都会阻塞；</p>
<p>（4）前者可以自己控制放元素是否需要阻塞线程，比如使用四个添加元素的方法就不会阻塞线程，只入队元素，使用transfer()会阻塞线程；</p>
<p>（5）取元素两者基本一样，都会阻塞等待有新的元素进入被匹配到；</p>
	
<h4> LinkedTransferQueue 要点</h4>
<p>（1）ConcurrentLinkedQueue不是阻塞队列；</p>
<p>（2）ConcurrentLinkedQueue不能用在线程池中；</p>
<p>（3）ConcurrentLinkedQueue使用（CAS+自旋）更新头尾节点控制出队入队操作；</p>
<h5><span id="i-10">其他</span></h5>
<p>ConcurrentLinkedQueue与LinkedBlockingQueue对比？</p>
<p>（1）两者都是线程安全的队列；</p>
<p>（2）两者都可以实现取元素时队列为空直接返回null，后者的poll()方法可以实现此功能；</p>
<p>（3）前者全程无锁，后者全部都是使用重入锁控制的；</p>
<p>（4）前者效率较高，后者效率较低；</p>
<p>（5）前者无法实现如果队列为空等待元素到来的操作；</p>
<p>（6）前者是非阻塞队列，后者是阻塞队列；</p>
<p>（7）前者无法用在线程池中，后者可以；</p>

	
<h4> LinkedTransferQueue 要点</h4>
<p>（1）DelayQueue是阻塞队列；</p>
<p>（2）DelayQueue内部存储结构使用优先级队列；</p>
<p>（3）DelayQueue使用重入锁和条件来控制并发安全；</p>
<p>（4）DelayQueue常用于定时任务；</p>
<p>（5）使用要搭配Delayed接口的实现  完成计时的任务</p>
<h2><span id="i-11">其他</span></h2>
<p>java中的线程池实现定时任务是直接用的DelayQueue吗？</p>
<p>当然不是，ScheduledThreadPoolExecutor中使用的是它自己定义的内部类DelayedWorkQueue，其实里面的实现逻辑基本都是一样的，只不过DelayedWorkQueue里面没有使用现在的PriorityQueue，而是使用数组又实现了一遍优先级队列，本质上没有什么区别。</p>

<h2><span id="Deque">Deque</span></h2>
<p>Deque是一种特殊的队列，它的两端都可以进出元素，故而得名双端队列（Double Ended Queue）。</p>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/Queue.png" alt="qrcode" /></p>
<p>java中提供的Deque的实现主要有ArrayDeque、LinkedBlockingDeque、ConcurrentLinkedDeque、LinkedList。</p>
<p>关于Deque的问题主要有：</p>
<p>（1）什么是双端队列？</p>
<p>（2）ArrayDeque是怎么实现双端队列的？</p>
<p>（3）ArrayDeque是有界的吗？</p>
<p>（4）LinkedList与ArrayDeque的对比？</p>
<p>（5）双端队列是否可以作为栈使用？</p>
<p>（6）LinkedBlockingDeque是怎么实现双端队列的？</p>
<p>（7）LinkedBlockingDeque是怎么保证并发安全的？</p>
<p>（8）ConcurrentLinkedDeque是怎么实现双端队列的？</p>
<p>（9）ConcurrentLinkedDeque是怎么保证并发安全的？</p>
<p>（10）LinkedList是List和Deque的集合体？</p>
<p>关于Deque的问题大概就这么多，你都能回答上来吗？</p>
<p>点击下面链接可以直接到相应的章节查看（LinkedBlockingDeque和LinkedBlockingQueueConcurrentLinkedDeque跟相应的Queue的实现方式基本一致，所以笔者没写这两个类的源码分析）：</p>
<p><a href="http://cmsblogs.com/?p=4771">【死磕 Java 集合】— ArrayDeque源码分析</a></p>
<p><a href="http://cmsblogs.com/?p=4725">【死磕 Java 集合】— LinkedList源码分析</a></p>
<h4> ArrayDeque 要点</h4>
<p>（1）ArrayDeque是采用数组方式实现的双端队列；</p>
<p>（2）ArrayDeque的出队入队是通过头尾指针循环利用数组实现的；</p>
<p>（3）ArrayDeque容量不足时是会扩容的，每次扩容容量增加一倍；</p>
<p>（4）ArrayDeque可以直接作为栈使用；</p>

<h4> LinkedList 要点</h4>
<p>（1）LinkedList是一个以双链表实现的List；</p>
<p>（2）LinkedList还是一个双端队列，具有队列、双端队列、栈的特性；</p>
<p>（3）LinkedList在队列首尾添加、删除元素非常高效，时间复杂度为O(1)；</p>
<p>（4）LinkedList在中间添加、删除元素比较低效，时间复杂度为O(n)；</p>
<p>（5）LinkedList不支持随机访问，所以访问非队列首尾的元素比较低效；</p>
<p>（6）LinkedList在功能上等于ArrayList + ArrayDeque；</p>

<h2><span id="i-2">总结</span></h2>
<p>其实上面的问题很多都具有共性，我觉得以下几个问题在看每个集合类的时候都要掌握清楚：</p>
<p>（1）使用的数据结构？</p>
<p>（2）添加元素、删除元素的基本逻辑？</p>
<p>（3）是否是fail-fast的？</p>
<p>（4）是否需要扩容？扩容规则？</p>
<p>（5）是否有序？是按插入顺序还是自然顺序还是访问顺序？</p>
<p>（6）是否线程安全？</p>
<p>（7）使用的锁？</p>
<p>（8）优点？缺点？</p>
<p>（9）适用的场景？</p>
<p>（10）时间复杂度？</p>
<p>（11）空间复杂度？</p>
<p>（12）还有呢？</p>
<h2><span id="i-3">彩蛋</span></h2>
<p>到这里整个集合的内容就全部完毕了，其实看了这么多集合的源码之后，笔者发现，基本上所有集合类使用的数据结构都是数组和链表，包括树和跳表也可以看成是链表的一种方式。</p>
<p>对于并发安全的集合，还要再加上相应的锁策略，要不就是重入锁，要不就是CAS+自旋，偶尔也来个synchronized。</p>
<p>所以，掌握集合的源码不算什么，数据结构和锁才是王道。</p>
		</article>