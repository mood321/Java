>和深入java 同样是jvm知识,但更多实战

### 01 
### 02 
### 03 
### 04
### 05 
### 06 
### 07
### 08
### 09 
### 10 
### 11 
### 12 
### 13
### 14 
### 15
### 16 
### 017、大厂面试题：年轻代和老年代分别适合什么样的垃圾回收算法？
<li> 躲过15次GC之后进入老年代
<li> 动态对象年龄判断
<li> 大对象直接进入老年代
<li> Minor GC后的对象太多，无法放入Survivor区怎么办？
<li> 老年代空间分配担保规则
<li> 老年代垃圾回收算法

#### 四种进入老年的方法
  + 1、躲过15次GC之后进入老年代
   > 这个具体是多少岁进入老年代，可以通过JVM参数“-XX:MaxTenuringThreshold”来设置，默认是15岁
  + 2、动态对象年龄判断
    >这里跟这个对象年龄有另外一个规则可以让对象进入老年代，不用等待15次GC过后才可以。
    
    >他的大致规则就是，假如说当前放对象的Survivor区域里，一批对象的总大小大于了这块Survivor区域的内存大小的50%，
    那么此时大于等于这批对象年龄的对象，就可以直接进入老年代了。
  + 3、大对象直接进入老年代
    >有一个JVM参数，就是“-XX:PretenureSizeThreshold”，可以把他的值设置为字节数，比如“1048576”字节，就是1MB。
    
    >他的意思就是，如果你要创建一个大于这个大小的对象，比如一个超大的数组，或者是别的啥东西，此时就直接把这个大对象放到老年
    代里去。压根儿不会经过新生代
  + 4、老年代空间分配担保规则
    >如果在Minor GC之后发现剩余的存活对象太多了，没办法放入另外一块Survivor区,这时候放到老年代
    
    + 担保流程
    >  在执行任何一次Minor GC之前，JVM会先检查一下老年代可用的可用内存空间，是否大于新生代所有对象的总大小
    
    >“-XX:HandlePromotionFailure”参数在JDK 1.6以后就被废弃了，所以现在一般都不会在生产环境里设置这个参数了。在JDK 1.6以后，只要判断“老年代可用空间”> “新生代对象总和”，或者“老年代可用空间”> “历次Minor GC升入老年代对象的平均大小”，两个条件满足一个，就可以直接进行Minor GC，不需要提前触发Full GC了。
        
     
   1. 第一种可能，Minor GC过后，剩余的存活对象的大小，是小于Survivor区的大小的，那么此时存活对象进入Survivor 区域即可。
   2.   第二种可能，Minor GC过后，剩余的存活对象的大小，是大于 Survivor区域的大小，但是是小于老年代可用内存大小 的，此时就直接进入老年代即可。
   3.   第三种可能，很不幸，Minor GC过后，剩余的存活对象的大小，大于了Survivor区域的大小，也大于了老年代可用内 存的大小。此时老年代都放不下这些存活对象了，
   + 3.1 检查“-XX:-HandlePromotionFailure”的参数是否设置了 , 老年代的内存大小，是否大于之前每一次Minor GC后进入老年代的对象的平均大小。大于放入老年代
   + 3.2 判断失败或者没设置,这个时候就会触 发一次“Full GC”。
   4.   Full GC就是对老年代进行垃圾回收，同时也一般会对新生代进行垃圾回收。因为这个时候必须得把老年代里的没人引用的对象给回收掉，然后才可能让Minor GC过后剩余的存活对象进入老年代 里面。
  
  如果要是Full GC过后，老年代还是没有足够的空间存放Minor GC过后的剩余存活对象，那么此时就会导致所谓的“OOM”内存溢出了
  
#### <b>老年代垃圾回收算法</b>
>对老年代触发垃圾回收的时机，一般就是两个：

+ 要不然是在Minor GC之前，一通检查发现很可能Minor GC之后要进入老年代的对象太多了，老年代放不下，此时需要提前触发Full GC然后再带着进行Minor GC；
+ 要不然是在Minor GC之后，发现剩余对象太多放入老年代都放不下了。

>一般老年代采取的是标记整理算法

注意一点，这个老年代的垃圾回收算法的速度至少比新生代的垃圾回收算法的速度慢10倍。如果系统频繁出现老年代的Full GC垃圾回收，会导致系统性能被严重影响，出现频繁卡顿的情况

### 018、大厂面试题：JVM中都有哪些常见的垃圾回收器，各自的特点是什么？

#### 小case 分析  看原来的
#### 老年代垃圾回收器
>常用的ParNew、CMS和G1三种垃圾回收器

>Serial和Serial Old垃圾回收器：分别用来回收新生代和老年代的垃圾对象

>工作原理就是单线程运行，垃圾回收的时候会停止我们自己写的系统的其他工作线程，让我们系统直接卡死不动，然后让他们垃圾回收，这个现在一般写后台Java系统几乎不用。

>ParNew和CMS垃圾回收器：ParNew现在一般都是用在新生代的垃圾回收器，CMS是用在老年代的垃圾回收器，他们都是多线程并发的机制，性能更好，现在一般是线上生产系统的标配组合。下周会着重分析这两个垃圾回收器。

>G1垃圾回收器：统一收集新生代 和老年代，采用了更加优秀的算法和设计机制，

### 019、“Stop the World”问题分析：JVM最让人无奈的痛点！

> 为了维护回收时的引用关系,他会直接停止我们写的Java系统的所有工作线程，让我们写的代码不再运行！

>一旦垃圾回收完毕，就可以继续恢复我们写的Java系统的工作线程的运行了，然后我们的那些代码就可以继续运行，继续在Eden中创建新的对象

#### 不同的垃圾回收器的不同的影响
>不断的在优化垃圾回收器的机制和算法，就是尽可能的降低垃圾回收的过程对我们的系统运行的影响。

###  20 画出各种垃圾回收算法和垃圾回收器的原理图
 [![UixNAx.jpg](https://s1.ax1x.com/2020/07/06/UixNAx.jpg)](https://imgchr.com/i/UixNAx)
 
###  022、一步一图：深入揭秘JVM的年轻代垃圾回收器ParNew是如何工作的！

#### 最常用的新生代垃圾回收器：ParNew
>新生代的ParNew垃圾回收器主打的就是多线程垃圾回收机制，另外一种Serial垃圾回收器主打的是单线程垃圾回收，他们俩都是回收新生代的，唯一的区别就是单线程和多线程的区别，但是垃圾回收算法是完全一样的。复制算法

>“-XX:+UseParNewGC” 表示使用ParNew 他默认给自己设置的垃圾回收线程的数量就是跟CPU的核数是一样的,也是可以的，也可以使用“-XX:ParallelGCThreads”设置线程的数量 不推荐

### 023、一步一图：那JVM老年代垃圾回收器CMS工作时，内部又干了些啥？

#### CMS垃圾回收的基本原理
>标记清理算法   标记方法去标记出哪些对象是垃圾对象，然后就把这些垃圾对象清理掉

>先通过追踪GC Roots的方法，看看各个对象是否被GC Roots给引用了，如果是的话，那就是存活对象，否则就是垃圾对象。先将垃圾对象都标记出来，然后一次性把垃圾对象都回收掉

> 缺点:内存碎片

基础回收算法是先停止一切工作线程，然后慢慢的去执行“标记-清理”算法，会导致系统卡死时间过长

所以CMS垃圾回收器采取的是垃圾回收线程和系统工作线程尽量同时执行的模式来处理的。

#### CMS在执行一次垃圾回收的过程一共分为4个阶段：
``` 
初始标记
并发标记
重新标记
并发清理   
````

- 第一个阶段，初始标记，虽然说要造成“Stop the World”暂停一切工作线程，但是其实影响不大，因为他的速度很快，仅仅标记GC Roots直接引用的那些对象罢了。

- 第二个阶段，是并发标记，这个阶段会让系统线程可以随意创建各种新对象，继续运行

    对老年代所有对象进行GC Roots追踪，其实是最耗时的
    
    他需要追踪所有对象是否从根源上被GC Roots引用了，但是这个最耗时的阶段，是跟系统程序并发运行的，所以其实这个阶段不会对系统运行造成影响的。

- 第三个阶段，重新标记阶段

    因为第二阶段里，你一边标记存活对象和垃圾对象，一边系统在不停运行创建新对象，让老对象变成垃圾 ,所以第二阶段结束之后，绝对会有很多存活对象和垃圾对象，是之前第二阶段没标记出来的
    
    这个重新标记的阶段，是速度很快的，他其实就是对在第二阶段中被系统程序运行变动过的少数对象进行标记，所以运行速度很快
    
- 第四阶段：并发清理 

    这个阶段就是让系统程序随意运行，然后他来清理掉之前标记为垃圾的对象即可。
    
    这个阶段其实是很耗时的，因为需要进行对象的清理，但是他也是跟系统程序并发运行的，所以其实也不影响系统程序的执行
    
### 024、动手实验：线上部署系统时，如何设置垃圾回收相关参数？
> CMS垃圾回收器有一个最大的问题，虽然能在垃圾回收的同时让系统同时工作，但是大家发现没有，在并发标记和并发清理两个最耗时
  的阶段，垃圾回收线程和系统工作线程同时工作，会导致有限的CPU资源被垃圾回收线程占用了一部分
  
> CMS默认启动的垃圾回收线程的数量是（CPU核数 + 3）/ 4。

#### Concurrent Mode Failure问题
>这个阶段系统一直在运行，可能会随着系统运行让一些对象进入老年代，同时还变成垃圾对象，这种垃圾对象是“浮动垃圾

>“-XX:CMSInitiatingOccupancyFaction”参数可以用来设置老年代占用多少比例的时候触发CMS垃圾回收，JDK 1.6里面默认的值是
92%。

>也就是说，老年代占用了92%空间了，就自动进行CMS垃圾回收，预留8%的空间给并发回收期间，系统程序把一些新对象放入老年代中。

如发生Concurrent Mode Failure，就是说并发垃圾回收失败了，我一边回收，你一边把对象放入老年代，内存都不够了。

此时就会自动用“Serial Old”垃圾回收器替代CMS，就是直接强行把系统程序“Stop the World”，重新进行长时间的GC Roots追踪，标记出来全部垃圾对象，不允许新的对象产生

然后一次性把垃圾对象都回收掉，完事儿了再恢复系统线程

#### 内存碎片问题
> CMS不是完全就仅仅用“标记-清理”算法的，因为太多的内存碎片实际上会导致更加频繁的Full GC。

>CMS有一个参数是“-XX:+UseCMSCompactAtFullCollection”，默认就打开了
 他意思是在Full GC之后要再次进行“Stop the World”，停止工作线程，然后进行碎片整理，就是把存活对象挪到一起，空出来大片
 连续内存空间，避免内存碎片。
 
> 还有一个参数是“-XX:CMSFullGCsBeforeCompaction”，这个意思是执行多少次Full GC之后再执行一次内存碎片整理的工作，默认
  是0，意思就是每次Full GC之后都会进行一次内存整理。

### 025、案例实战：每日上亿请求量的电商系统，年轻代垃圾回收参数如何优化？
+ 大促高峰期订单系统的内存使用模型估算
    >估算新生成对象大小
+ 内存到底该如何分配
    > 新生成对象是不用进老年代的
+ 新生代垃圾回收优化之一：Survivor空间够不够
 
    Survivor 注意两点
    > 1 Minor GC 对象大于Survivor 直接进入老年代
    
    > 2 Minor GC后,放在Survivor 因为这是一批同龄对象，直接超过了Survivor区空间的50%，此时也可能会导致对象进入老年代。
    
+ 新生代对象躲过多少次垃圾回收后进入老年代
    > “-XX:MaxTenuringThreshold”参数的默认值15次 ,具体看运行情况,可大可小
    
+ 多大的对象直接进入老年代？
    >-XX:PretenureSizeThreshold=1M  指定1M 足够
 
 + 指定垃圾回收器
    > -XX:+UseParNewGC -XX:+UseConcMarkSweepGC  常用ParNew和CMS
    
得到参数 “-Xms3072M -Xmx3072M -Xmn2048M -Xss1M  -XX:PermSize=256M -XX:MaxPermSize=256M  -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=5 -XX:PretenureSizeThreshold=1M -XX:+UseParNewGC -XX:+UseConcMarkSweepGC”
  
### 026、案例实战：每日请求上亿的电商系统，老年代的垃圾回收参数又该如何优化呢？

年轻代就是上面的参数
+ 什么时候对象会进入老年代？
    > -XX:MaxTenuringThreshold=5 这个参数会让在一两分钟内连续躲过5次Minor GC的对象迅速进入老年代中。  
    
    > 大对象,之前设为1M  ,应该少
    
    > Minor GC过后可能存活的对象超过200MB放不下Survivor了，或者是一下子占到超过Surviovr的50%，此时会有一些对象进入老年代中。 也在上面参数尽可能避免
    
+ 多久会触发一次Full GC
    
    Full GC的触发条件目前我们学习到的有以下4种
    + （1） 没有打开“ -XX:HandlePromotionFailure”选项，结果老年代可用内存最多也就1G，新生代对象总大小最多可以有1.8G
     
      那么会导致每次Minor GC前一检查，都发现“老年代可用内存” < “新生代总对象大小”，这会导致每次Minor GC前都触发Full GC。
      
      JDK 1.6以后的版本废弃了这个参数 所以1.8不存在
    
    + （2）每次Minor GC之前，都检查一下“老年代可用内存空间” < “历次Minor GC后升入老年代的平均对象大小”
    
    + （3）可能某次Minor GC后要升入老年代的对象有几百MB，但是老年代可用空间不足了
    
    + （4）设置了“-XX:CMSInitiatingOccupancyFaction”参数，比如设定值为92%，那么此时可能前面几个条件都没满足，但是刚好发现这个条件满足了，比如就是老年代空间使用超过92%了，此时就会自行触发Full GC
    
   
+ 老年代GC的时候会发生“Concurrent Mode Failure”吗？
     > 有可能, 但加上-XX:CMSInitiatingOccupancyFaction=92 在cms 清除的时候,按老年代上面的参数,留给它近200M, 全部沾满 几率不大,
     
+ CMS垃圾回收之后进行内存碎片整理的频率应该多高？
    在CMS完成Full GC之后，一般需要执行内存碎片的整理，可以设置多少次Full GC之后执行一次内存碎片整理，但是我们有必要修改这些参数吗？
    
    > 答案不大必要,对象大多在年轻代,full gc 几个小时才发生,性能足够
 
 得到参数  “-Xms3072M -Xmx3072M -Xmn2048M -Xss1M  -XX:PermSize=256M -XX:MaxPermSize=256M  -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=5 -XX:PretenureSizeThreshold=1M -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFaction=92 -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0”
 
    
###  027、第4周作业：看看你们的线上系统是怎么设置的JVM垃圾回收参数？设置的合理吗？
   > 我们线上全是默认   分析有时间做

### 029、大厂面试题：最新的G1垃圾回收器的工作原理，你能聊聊吗？ 
    ````
    1、ParNew + CMS的组合让我们有哪些痛点？
    2、G1垃圾回收器
    3、G1是如何做到对垃圾回收导致的系统停顿可控的？
    4、Region可能属于新生代也可能属于老年代
    ````
#### ParNew + CMS的组合让我们有哪些痛点？
> Stop the World，这个是大家最痛的一个点！
    
#### G1垃圾回收器

 > 他最大的一个特点，就是把Java堆内存拆分为多个大小相等的Region,G1也会有新生代和老年代的概念，但是只不过是逻辑上的概念
 
 > G1最大的一个特点，就是可以让我们设置一个垃圾回收的预期停顿时间
 
 #### G1是如何做到对垃圾回收导致的系统停顿可控的？
> G1可以做到让你来设定垃圾回收对系统的影响，他自己通过把内存拆分为大量(2048 )小Region，以及追踪每个Region中可以回收的对象大小和预估时间，最后在垃圾回收的时候，尽量把垃圾回收对系统造成的影响控制在你指定的时间范围内，同时在有限的时间内尽量回收尽可能多的垃圾对象。
 
 #### Region可能属于新生代也可能属于老年代
 >刚开始Region可能谁都不属于，然后接着就分配给了新生代，然后放了很多属于新生代的对象，接着就触发了垃圾回收这个Region

 >下一次同一个Region可能又被分配了老年代了，用来放老年代的长生存周期的对象

 >实际上新生代和老年代各自的内存区域是不停的变动的，由G1自动控制。
 
 ### 030、G1分代回收原理深度图解：为什么回收性能比传统GC更好？
 
 #### 设定G1对应的内存大小
 
 到底有多少个Region呢？每个Region的大小是多大呢？
 >VM启动的时候一旦发现你使用的是G1垃圾回收器，可以使用“-XX:+UseG1GC”来指定使用G1垃圾回收器，此时会自动用堆大小除以2048

 > 因为JVM最多可以有2048个Region，然后Region的大小必须是2的倍数，比如说1MB、2MB、4MB之类的   -XX:G1HeapRegionSize 手动设置
 
 >刚开始的时候，默认新生代对堆内存的占比是5%，也就是占据200MB左右的内存，对应大概是100个Region，这个是可以通过“-XX:G1NewSizePercent”来设置新生代初始占比的，其实维持这个默认值即可。
 
 >因为在系统运行中，JVM其实会不停的给新生代增加更多的Region，但是最多新生代的占比不会超过60%，可以通过“-XX:G1MaxNewSizePercent”。
 
 >而且一旦Region进行了垃圾回收，此时新生代的Region数量还会减少
 
#### 新生代还有Eden和Survivor的概念吗？

>G1中虽然把内存划分为了很多的 Region，但是其实还是有新生代、老年代的区分 也有Eden和Survivor的划分的

>比如 -XX:SurvivorRatio=8”，新生代之前说刚开始初始的时候，有100个Region，那么可能80个Region就是Eden，两个Survivor各自占10个Region

#### G1的新生代垃圾回收
>既然G1的新生代也有Eden和Survivor的区分，那么触发垃圾回收的机制都是类似的

>随着不停的在新生代的Eden对应的Region中放对象，JVM就会不停的给新生代加入更多的Region，直到新生代占据堆大小的最大比例60%。

>一旦新生代达到了设定的占据堆内存的最大大小60%，比如都有1200个Region了，里面的Eden可能占据了1000个Region，每个Survivor是100个Region，而且Eden区还占满了对象

> 会触发新生代的GC，G1就会用之前说过的复制算法来进行垃圾回收，进入一个“Stop the World”状态 然后把Eden对应的Region中的存活对象放入S1对应的Region中，接着回收掉Eden对应的Region中的垃圾对象

>个过程跟之前是有区别的，因为G1是可以设定目标GC停顿时间的，也就是G1执行GC的时候最多可以让系统停顿多长时间，可以通过“-XX:MaxGCPauseMills”参数来设定，默认值是200ms

#### 对象什么时候进入老年代？

还是这么几个条件：

+ （1）对象在新生代躲过了很多次的垃圾回收，达到了一定的年龄了，“-XX:MaxTenuringThreshold”参数可以设置这个年龄，他就会进入老年代

+ （2）动态年龄判定规则，如果一旦发现某次新生代GC过后，存活对象超过了Survivor的50%
 
#### 大对象Region
> G1的大对象 有点不一样,G1提供了专门的Region来存放大对象，而不是让大对象进入老年代的Region中。
               
> 在G1中，大对象的判定规则就是一个大对象超过了一个Region大小的50%，比如按照上面算的，每个Region是2MB，只要一个大对象超过了1MB，就会被放入大对象专门的Region中
               
> 而且一个大对象如果太大，可能会横跨多个Region来存放

 大对象的分配和回收
>比如新生代现在占据了1200个Region，但是一次垃圾回收之后，就让里面1000个Region都空了，此时那1000个Region就可以不属于新生代了，里面很多Region可以用来存放大对象

>新生代、老年代在回收的时候，会顺带带着大对象Region一起回收，所以这就是在G1内存模型下对大对象的分配和回收的策略。

### 031、动手实验：线上系统部署如果采用G1垃圾回收器，应该如何设置参数？

#### 什么时候触发新生代+老年代的混合垃圾回收？
>G1有一个参数，是”-XX:InitiatingHeapOccupancyPercent"，它的默认值是45%。意思是说，如果老年代占据了堆内存的45%的Region的时候，此时就会尝试触发一个新生代+老年代一起回收的混合回收阶段。

>G1整体是基于复制算法进行Region垃圾回收，不会出现内存碎片问题，不需要像CMS那样标记-清理之后再进行内存碎片的整理。 

>g1垃圾回收器新生代初始占比默认为5%，新生代最大占比默认为60%。

>如果堆内存为4G，此时除以2048，得出每个region的大小为2mb，刚开始新生代就占5%的region，可以认为新生代就是只有100个region，有200mb的内存空间。

>g1有一个参数"-XX:MaxGCPauseMills"，它的默认值是200ms

>一旦老年代频繁达到占用堆内存45%的阈值，那么就会频繁触发mixed gc。

#### G1垃圾回收的过程

+ 首先会触发一个“初始标记”的操作，这个过程是需要进入“Stop the World”的，仅仅只是标记一下GC Roots直接能引用的对象， 这个过程速度是很快的
  
+ 接着会进入“并发标记”的阶段，这个阶段会允许系统程序的运行，同时进行GC Roots追踪，从GC Roots开始追踪所有的存活对象，

+ 然后在并发标记阶段，就会进行GC Roots追踪，会从GC Root对象直接关联的对象开始往下追踪

    这个并发标记阶段还是很耗时的，因为要追踪全部的存活对象。 但是是并行,影响不大,会对并发标记阶段对对象做出的一些修改记录起来，比如说哪个对象被新建了，哪个对象失去了引用。
+  然后是下一个阶段，最终标记阶段，这个阶段会进入“Stop the World”，系统程序是禁止运行的，但是会根据并发标记 阶段记录的那些对象修改，最终标记一下有哪些存活对象，有哪些是垃圾对象

+ 最后一个阶段，就是“混合回收“阶段，这个阶段会计算老年代中每个Region中的存活对象数量，存活对象的占比，还有执行垃圾回收的预期性能和效率。
  
  接着会停止系统程序，然后全力以赴尽快进行垃圾回收，此时会选择部分Region进行回收，因为必须让垃圾回收的停顿时间控制在我 们指定的范围内。
  
#### G1垃圾回收器的一些参数
>“-XX:G1MixedGCCountTarget”参数，就是在一次混合回收的过程中，最后一个阶段执行几次混合回收，默认值是8次

 意味着最后一个阶段，先停止系统运行，混合回收一些Region，再恢复系统运行，接着再次禁止系统运行，混合回收一些Region，反 复8次。
 
> “-XX:G1HeapWastePercent”，默认值是5%

他的意思就是说，在混合回收的时候，对Region回收都是基于复制算法进行的，都是把要回收的Region里的存活对象放入其他Region，然后这个Region中的垃圾对象全部清理掉

这样的话在回收过程就会不断空出来新的Region，一旦空闲出来的Region数量达到了堆内存的5%，此时就会 立即停止混合回收，意味着本次混合回收就结束了

>“-XX:G1MixedGCLiveThresholdPercent”，他的默认值是85%，

意思就是确定要回收的Region的时候，必须是存 活对象低于85%的Region才可以进行回收 ,否则要是一个Region的存活对象多余85%，你还回收他干什么？这个时候要把85%的对象都拷贝到别的Region


#### 回收失败时的Full GC
如果在进行Mixed回收的时候，无论是年轻代还是老年代都基于复制算法进行回收，都要把各个Region的存活对象拷贝到别的Region里去

此时万一出现拷贝的过程中发现没有空闲Region可以承载自己的存活对象了，就会触发 一次失败。

一旦失败，立马就会切换为停止系统程序，然后采用单线程进行标记、清理和压缩整理，空闲出来一批Region，这个过程是极慢极慢的。


### 032-33 案例实战：百万级用户的在线教育平台，如何基于G1垃圾回收器优化性能

#### 系统的运行压力
>核心点就是搞明白在晚上两三小时高峰期内，每秒钟会有多少请求，每个请求会连带产生多少对象，占用多少内存，每个请求要处理多长时间。

#### G1垃圾回收器的默认内存布局

我们对机器上的JVM，分配4G给堆内存，其中新生代默认初始占比为5%，最大占比为60%，每个Java线程的栈内存为1MB，元数据区域（永久代）的内存为256M，此时JVM参数如下：
````
“-Xms4096M -Xmx4096M  -Xss1M  -XX:PermSize=256M -XX:MaxPermSize=256M -XX:+UseG1GC“
“-XX:G1NewSizePercent”参数是用来设置新生代初始占比的，不用设置，维持默认值为5%即可。
“-XX:G1MaxNewSizePercent”参数是用来设置新生代最大占比的，也不用设置，维持默认值为60%即可。
````

#### GC停顿时间如何设置
> “-XX:MaxGCPauseMills”，他的默认值是200毫秒

#### 多长时间会触发新生代GC？

>G1它本身是这样的一个运行原理，他会根据你预设的gc停顿时间，给新生代分配一些Region，然后到一定程度就触发gc，并且把gc时间控制在预设范围内，尽量避免一次性回收过多的Region导致gc停顿时间超出预期。

#### 新生代gc如何优化？

个人觉得G1 优化 能做的不多

> “-XX:MaxGCPauseMills”参数

>如果这个参数设置的小了，那么说明每次gc停顿时间可能特别短，此时G1一旦发现你对几十个Region占满了就立即触发新生代gc，然后gc频率特别频繁，虽然每次gc时间很短。

>大了呢  那么可能G1会允许你不停的在新生代理分配新的对象，然后积累了很多对象了，再一次性回收几百个Regio

#### mixed gc如何优化？

mixed gc的触发，老年代在堆内存里占比超过45%就会触发。

> 进入老年代的几个条件了，要不然是新生代gc过后存活对象太多没法放入Survivor区域，要不然是对象年龄太大，要不然是动态年龄判定规则。

核心的点，还是“-XX:MaxGCPauseMills”这个参数。

> 太小 ,年龄增长过快, 太大  一次存活的对象太多 最直接进入老年代

### 034、第一阶段复习：当你开发完一个系统准备部署上线时，如何设置JVM参数？
> 个人理解 G1 适合大内存服务器, 他会自己检测 Y GC, ParNew+ CMS 到达阈值触发 ,STW 时间太长

> 参数设置 有时间写

### 036、糟糕！运行着的线上系统突然卡死无法访问，万恶的JVM GC！

#### 基于JVM运行的系统最怕什么？
> STW 不解释  不管Mintor 还是old  gc 都会系统停顿

#### 年轻代gc到底多久一次对系统影响不大？
> 新生代采用的复制算法效率极高，因为新生代里存活的对象很少，只要迅速标记出这少量存活对象，移动到Survivor区，然后回收掉其他全部垃圾对象即可，速度很快。

> 大内存机器上 ParNew 会在Eden 没有内存时候触发, 如果内存太大 ,影响就大了,如果几十g 可能能停顿好几秒

#### 解决大内存机器的新生代GC过慢的问题？
用G1垃圾回收器
>G1基于他的Region内存划分原理，就可以在运行一段时间之后，比如就针对2G内存的Region进行垃圾回收，此时就仅仅停顿20ms，然后回收掉2G的内存空间，腾出来了部分内存，接着还可以继续让系统运行。

#### 要命的频繁老年代gc问题

新生代gc一般问题不会太大，但是真正问题最大的地方，在于频繁触发老年代的GC。

进入老年代的条件
````
第一个，对象年龄太大了，这种对象一般很少，都是系统中确实需要长期存在的核心组件，他们一般不需要被回收掉，所以在新生代熬过默认15次垃圾回收之后就会进入老年代。

第二个，动态年龄判定规则，如果一次新生代gc过后，发现Survivor区域中的几个年龄的对象加起来超过了Survivor区域的50%，比如说年龄1+年龄2+年龄3的对象大小总和，超过了Survivor区域的50%，此时就会把年龄3以上的对象都放入老年代。

第三个，新生代垃圾回收过后，存活对象太多了，无法放入 Surviovr中，此时直接进入老年代。
````
ps:补一手动态年龄判断

Survivor区的对象年龄从小到大进行累加，当累加到 X 年龄时的总和大于50%（可以使用XX:TargetSurvivorRatio=? 来设置保留多少空闲空间，默认值是50），那么比X大的和X都会晋升入老年代 这是>=

老年代gc通常来说都很耗费时间，无论是CMS垃圾回收器还是G1垃圾回收器，因为比如说CMS就要经历初始标记、并发标记、重新标记、并发清理、碎片整理几个环节，过程非常的复杂，G1同样也是如此

主要还是在Survivor  太大,edne小 容易走第一个,太小 容易第二 第三

#### JVM性能优化到底在优化什么？

系统真正最大的问题，就是因为内存分配、参数设置不合理，导致你的对象频繁的进入老年代，然后频繁触发老年代gc，导致系统频繁的每隔几分钟就要卡死几秒钟

### 037、大厂面试题：解释一下什么是Young GC和Full GC？

#### Minor GC / Young GC
年轻代GC

#### Full GC？Old GC？傻傻分不清楚
 字面意思  一个全部,一个老年代 ,但事实上他们几乎是等价的 ,面试要精确的话 一定要问清楚
 
 Full GC
 > Full GC指的是针对新生代、老年代、永久代的全体内存空间的垃圾回收，所以称之为Full GC。

#### Major GC

Major GC跟Old GC等价起来，认为他就是针对老年代的GC，也有人把Major GC和Full GC等价起来，认为他是针对JVM全体内存区域的GC,这个是混淆的

#### Mixed GC
 G1 里面的概念,混合,等价于Full 吧
 
 
 ### 038、大厂面试题：Young GC和Full GC分别在什么情况下会发生？
 这章算总结,虽然前面也有
 
 #### Young GC的触发时机
 > Young GC其实一般就是在新生代的Eden区域满了之后就会触发，采用复制算法来回收新生代的垃圾
 
 > G1 还有个,停顿控制时间 检测
 
 #### Old GC和Full GC的触发时机  (CMS)
 
 + （1）发生Young GC之前进行检查，如果“老年代可用的连续内存空间” < “新生代历次Young GC后升入老年代的对象总和的平均大小”，说明本次Young GC后可能升入老年代的对象大小，可能超过了老年代当前可用内存空间此时必须先触发一次Old GC给老年代腾出更多的空间，然后再执行Young GC
 + （2）执行Young GC之后有一批对象需要放入老年代，此时老年代就是没有足够的内存空间存放这些对象了，此时必 须立即触发一次Old GC
 + （3）老年代内存使用率超过了92%，也要直接触发Old GC，当然这个比例是可以通过参数调整的
#### Mixed   (G1)
  + 老年代频繁达到占用堆内存45%的阈值，那么就会频繁触发mixed gc。参数可设置

老年代空间也不够了，没法放入更多对象了，这个时候务必执行OldGC对老年代进行垃圾回收 ,老年代会带着Young GC 一起

#### 永久代
假如存放类信息、常量池的永久代满了之后，就会触发一次Full GC ,但一般这个区能回收很少,还是没有 会内存不够的异常
 
### 039、案例实战：每秒10万并发的BI系统是如何频繁发生Young GC的？
>所谓BI，英文全称是“Business Intelligence”，也就是“商业智能”  就是把一些商家平时日常经营的数据收集起来进行分析，然后把各种数据报表展示给商家的一套系统。

#### 技术痛点：实时自动刷新报表 + 大数据量报表
> js 会频繁请求, 实时刷新数据

#### 没什么大影响的频繁Young GC

假如 每秒5M , eden 1G, 需要200s 大概3分钟,gc时间在十几ms 可以接受

#### 提升机器配置：运用大内存机器
越来越多的商家来使用，并发压力越来越大，甚至高峰期会有每秒10万的并发压力,每秒几千请求,eden 加到了16G

这时再去Young GC 就可能到达秒级别的停顿

#### 用G1来优化大内存机器的Young GC性能
 上面这种就用G1 G1设置一个预期的GC停顿时间，比如100ms，让G1保证每次Young GC的时候最多停顿100ms，避免影响终端用户的使用。
 
 ### 040、案例实战：每日百亿数据量的实时分析引擎，为啥频繁发生Full GC ？
 
 >这个数据计算系统会不停的通过SQL语句和其他方式，从各种数据存储中提取数据到内存中来进行计算，大致当时的生产负载是每分钟大概需要执行500次数据提取和计算的任务。
 
 >每次会提取大概1万条左右的数据到内存里来计算，平均每次计算大概需要耗费10秒左右的时间，然后每台机器是4核8G的配置，JVM内存给了4G，其中新生代和老年代分别是1.5G的内存空间
 
 #### 系统到底多块会塞满新生代？
 
 > 一万数据,一条算1k ,10M, Edne算1.2G 一两分钟 塞满
 
#### 触发Minor GC的时候会有多少对象进入老年代？
     
> 计算要10秒, 不停地话 会有20次计算存活,200M ,Survivor 默认150M 不够,进老年代

#### 系统运行多久，老年代大概就会填满？

> 一分钟一次Young ,200M, Minor检测, 前七次,old 内存都够,  但七次已经装满了

#### 这个系统运行多久，老年代会触发1次Full GC？
> 第八次,会Full GC  ,假设 old 都是计算的对象 ,不算其他的,就是七八分钟一次

#### 该案例应该如何进行JVM优化？
> 把Young 给大点,2g ,Survivor 200m 能装下每次存活的对象,几乎不会进入老年代  

#### 如果该系统的工作负载再次扩大10倍呢？
> 计算方式一样,两分钟大概三次左右

#### 使用大内存机器来优化上述场景
> 加机器 ,假如也是10倍, gc间隔时间还是差不多 ,一两分钟一次,但执行时间很长,16g的eden 算1秒

>要G1吗, 后台程序没必要

### 041、第6周作业：打开脑洞！如果你的线上系统压力增长100倍，会有频繁GC问题吗？

> 有时间补上

### 042、第6周答疑：本周问题答疑汇总！
````
  g1回收器。
    1.如果新生代未达60%，老年代未达45%，系统照常运行，不会触发回收 
    2.如果新生代达60%，此后如果如有新对象生成，跑到新生代，会触发ygc.
    （1）开启了空间担保机制，ygc先判断是否需要fgc,如果每次回收后对象少于老年代空闲大小，则不用fgc,否则要
    （2）不用触发，但ygc后的对象大于老年代空闲大小，无法直接进入老年代，触发fgc.
    （3）触发混合回收，先通过gcroot初始标记哪些不是垃圾对象(此过程会stw,不过很快)，然后并发标记(用户线程和标记线程并行)，接
    着最终标记(会stw，标记并发标记过程中可能新产生的垃圾对象)，最后混合回收(此过程采用复制算法，不会产生垃圾碎片，所以不用
    在回收完去整理内存碎片
    g1会按照我们给定的时间去stw并回收，争取回收性价比的对象，如果回收次数少于8次，则再次混合回收。不过，在回收中空闲
    region大小达到堆5%，会提前结束。)如果回收失败，则转换采用serialold回收器。 
    3.当老年代代达45%会触发上面那个混合过程。

````
### 043、动手实验：自己动手模拟出频繁Young GC的场景体验一下！

````
    -XX:NewSize=5242880 -XX:MaxNewSize=5242880 -XX:InitialHeapSize=10485760 -XX:MaxHeapSize=10485760 -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=10485760 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC
    上述参数都是基于JDK 1.8版本来设置的，不同的JDK版本对应的参数名称是不太一样的，但是基本意思是类似的。
    上面“-XX:InitialHeapSize”和“-XX:MaxHeapSize”就是初始堆大小和最大堆大小，“-XX:NewSize”和“-XX:MaxNewSize”是初始新生代大小和最大新生代大小，“-XX:PretenureSizeThreshold=10485760”指定了大对象阈值是10MB。
````

#### 如何打印出JVM GC日志？
````
    -XX:+PrintGCDetils：打印详细的gc日志
    
    -XX:+PrintGCTimeStamps：这个参数可以打印出来每次GC发生的时间
    
    -Xloggc:gc.log：这个参数可以设置将gc日志写入一个磁盘文件
````

-XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:gc.log


### 044、高级工程师的硬核技能：JVM的Young GC日志应该怎么看？

#### 程序运行采用的默认JVM参数如何查看？
> 打印gc日志的参数，就可以在这里看到他默认会给你的JVM进程分配多大的内存空间了

#### 一次GC的概要说明
````
  java代码
       byte[] arr = new byte[1024 * 1024];
        arr = new byte[1024 * 1024];
        arr = new byte[1024 * 1024];
        
        byte[] arr2 = new byte[2*1024 * 1024];
 gc 日志     
    0.268: [GC (Allocation Failure) 0.269: [ParNew: 4030K->512K(4608K), 0.0015734 secs] 4030K->574K(9728K), 0.0017518 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
````
GC (Allocation Failure) 分配失败 GC

耗时 0.268 

ParNew: 4030K->512K(4608K), 0.0015734 secs 年轻代ParNew ,4608KB 是可用,4030K 已用,512K gc之后 已用 ,耗时0.0015734

4030K->574K(9728K), 0.0017518 secs 是整个堆

#### GC过后的堆内存使用情况
````
    Heap
    
    par new generation   total 4608K, used 2601K [0x00000000ff600000, 0x00000000ffb00000, 0x00000000ffb00000)
    
     eden space 4096K,  51% used [0x00000000ff600000, 0x00000000ff80a558, 0x00000000ffa00000)
    
     from space 512K, 100% used [0x00000000ffa80000, 0x00000000ffb00000, 0x00000000ffb00000)
    
     to   space 512K,   0% used [0x00000000ffa00000, 0x00000000ffa00000, 0x00000000ffa80000)
    
    concurrent mark-sweep generation total 5120K, used 62K [0x00000000ffb00000, 0x0000000100000000, 0x0000000100000000)
    
    Metaspace       used 2782K, capacity 4486K, committed 4864K, reserved 1056768K
    
     class space    used 300K, capacity 386K, committed 512K, reserved 1048576K
````
JVM退出的时候打印出来的当前堆内存的使用情况，其实也很简单

````
par new generation   total 4608K, used 2601K [0x00000000ff600000, 0x00000000ffb00000, 0x00000000ffb00000)

 eden space 4096K,  51% used [0x00000000ff600000, 0x00000000ff80a558, 0x00000000ffa00000)

 from space 512K, 100% used [0x00000000ffa80000, 0x00000000ffb00000, 0x00000000ffb00000)

 to   space 512K,   0% used [0x00000000ffa00000, 0x00000000ffa00000, 0x00000000ffa80000)
 ````
 这是par new 负责的年轻代,eden,from ,to,使用情况  
 
 ````
 concurrent mark-sweep generation total 5120K, used 62K [0x00000000ffb00000, 0x0000000100000000, 0x0000000100000000)
 
 Metaspace       used 2782K, capacity 4486K, committed 4864K, reserved 1056768K
 
  class space    used 300K, capacity 386K, committed 512K, reserved 1048576K
  
 ````
CMS 负责的老年代 ,Metaspace元数据空间和Class空间 使用情况
 
### 045-46、动手实验：自己动手模拟出对象进入老年代的场景体验一下

#### 动态年龄判定规则
1. 躲过15次gc，达到15岁高龄之后进入老年代；
2. 动态年龄判定规则，如果Survivor区域内年龄1+年龄2+年龄3+年龄n的对象总和大于Survivor区的50%，此时年龄n以上的对象会进入
老年代，不一定要达到15岁
3. 如果一次Young GC后存活对象太多无法放入Survivor区，此时直接计入老年代
4. 大对象直接进入老年代

配置参数
>“-XX:NewSize=10485760 -XX:MaxNewSize=10485760 -XX:InitialHeapSize=20971520 -XX:MaxHeapSize=20971520 -
 XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=15 -XX:PretenureSizeThreshold=10485760 -XX:+UseParNewGC -
 XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:gc.log

新生代我们通过“-XX:NewSize”设置为10MB了         然后其中Eden区是8MB，每个Survivor区是1MB，Java堆总大小是20MB，老年代是10MB，大对象必须超过10MB才会直接进入老年代
 
 代码
````
        byte[] arr = new byte[2*1024 * 1024];
        arr = new byte[2*1024 * 1024];
        arr = new byte[2*1024 * 1024];
        arr = null;
        byte[] arr2 = new byte[128 * 1024];
        byte[] arr3 = new byte[2*1024 * 1024];
````

日志
````
 0.297: [GC (Allocation Failure) 0.297: [ParNew: 7260K->715K(9216K), 0.0012641 secs] 7260K->715K(19456K), 0.0015046
 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 Heap
 par new generation total 9216K, used 2845K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
  eden space 8192K, 26% used [0x00000000fec00000, 0x00000000fee14930, 0x00000000ff400000)
  from space 1024K, 69% used [0x00000000ff500000, 0x00000000ff5b2e10, 0x00000000ff600000)
  to space 1024K, 0% used [0x00000000ff400000, 0x00000000ff400000, 0x00000000ff500000)
 concurrent mark-sweep generation total 10240K, used 0K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
 Metaspace used 2782K, capacity 4486K, committed 4864K, reserved 1056768K
  class space used 300K, capacity 386K, committed 512K, reserved 1048576K
````

#### 部分代码的GC日志分析

连续申请3个 2M的对象,6m eden 8m ,后面又申请128k  ,在申请2m  肯定不够 gc

通过日志可以清晰看出，此时From Survivor区域被占据了69%的内存，大概就是700KB左右，这就是一次Young GC后存活下来的对象，他们都进入From Survivor区了。

同时Eden区域内被占据了26%的空间，大概就是2MB左右，这就是byte[] array3 = new byte[2 * 1024 * 1024];，这行代码在gc过后分配在Eden区域内的数组

此时from 内的对象 年龄应该是1 ,gc 后剩余715k 应该全部进入from, from 1m ,大于一半,所以应比他大的都在old


代码修改
````
        byte[] arr = new byte[2*1024 * 1024];
        arr = new byte[2*1024 * 1024];
        arr = new byte[2*1024 * 1024];
        arr = null;
        byte[] arr2 = new byte[128 * 1024];
        byte[] arr3 = new byte[2*1024 * 1024];

        arr3 = new byte[2*1024 * 1024];
        arr3 = new byte[2*1024 * 1024];
        arr3 = new byte[128 * 1024];
         arr3=null;
        byte[] arr4 = new byte[2*1024 * 1024];
````

#### 在分析下

 第一次gc 完毕, from 715k ,eden 2m
 
 然后申请 2个2m 1个 128K, null ,在申请2m ,不够 进入第二次par new  gc
 
 日志
 ````
    0.269: [GC (Allocation Failure) 0.269: [ParNew: 7260K->713K(9216K), 0.0013103 secs] 7260K->713K(19456K), 0.0015501
    secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
    0.271: [GC (Allocation Failure) 0.271: [ParNew: 7017K->0K(9216K), 0.0036521 secs] 7017K->700K(19456K), 0.0037342 secs]
    [Times: user=0.06 sys=0.00, real=0.00 secs]
    Heap
    par new generation total 9216K, used 2212K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
     eden space 8192K, 27% used [0x00000000fec00000, 0x00000000fee290e0, 0x00000000ff400000)
     from space 1024K, 0% used [0x00000000ff400000, 0x00000000ff400000, 0x00000000ff500000)
     to space 1024K, 0% used [0x00000000ff500000, 0x00000000ff500000, 0x00000000ff600000)
    concurrent mark-sweep generation total 10240K, used 700K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
    Metaspace used 2782K, capacity 4486K, committed 4864K, reserved 1056768K
     class space used 300K, capacity 386K, committed 512K, reserved 1048576K
````

第一次上面说了  第二次 7017K->0K  年轻代0k  eden 27% ,这个是刚申请的arr4 的2m,那其他对象呢

其实此时会发现Survivor区域中的对象都是存活的，而且总大小超过50%了，而且年龄都是1岁

此时根据动态年龄判定规则：年龄1+年龄2+年龄n的对象总大小超过了Survivor区域的50%，年龄n以上的对象进入老年代。

此时eden 是arr4  ,老年是arr2和其他对象


#### Survivor区域放不下，就直接进入老年代

参数和上面一样

代码
````
   byte[] arr = new byte[2*1024 * 1024];
   arr = new byte[2*1024 * 1024];
   arr = new byte[2*1024 * 1024];
   
    byte[] arr2 = new byte[128 * 1024];
    arr2 = null;
    byte[] arr3 = new byte[2*1024 * 1024];
````

日志

````

0.421: [GC (Allocation Failure) 0.421: [ParNew: 7260K->573K(9216K), 0.0024098 secs] 7260K->2623K(19456K), 0.0026802 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
Heap

par new generation   total 9216K, used 2703K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)

 eden space 8192K,  26% used [0x00000000fec00000, 0x00000000fee14930, 0x00000000ff400000)
 from space 1024K,  55% used [0x00000000ff500000, 0x00000000ff58f570, 0x00000000ff600000)
 to   space 1024K,   0% used [0x00000000ff400000, 0x00000000ff400000, 0x00000000ff500000)

concurrent mark-sweep generation total 10240K, used 2050K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
Metaspace       used 2782K, capacity 4486K, committed 4864K, reserved 1056768K

 class space    used 300K, capacity 386K, committed 512K, reserved 1048576K
````

逻辑和上面第一个一致 ,但他gc后 ,活下来一个2m 的对象, 和其他对象( 这部分应该是573k)  ,servivor 1m ,肯定是放不下的

而通过日志也能看到 2m那个,在old ,573的在survicor  

#### 结论

在这种场景下，有部分对象会留在Survivor中，有部分对象会进入老年代的。


### 047、高级工程师的硬核技能：JVM的Full GC日志应该怎么看？

参数
> “-XX:NewSize=10485760 -XX:MaxNewSize=10485760 -XX:InitialHeapSize=20971520 -XX:MaxHeapSize=20971520 -XX:SurvivorRatio=8  -XX:MaxTenuringThreshold=15 -XX:PretenureSizeThreshold=3145728 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:gc.log”

新生代我们通过“-XX:NewSize”设置为10MB了         然后其中Eden区是8MB，每个Survivor区是1MB，Java堆总大小是20MB，老年代是10MB

注意  PretenureSizeThreshold   大对象设为3m  

代码

````
      byte[] arr = new byte[4*1024 * 1024];
      arr=null;
      byte[] arr2 = new byte[2*1024 * 1024];
      byte[] arr3 = new byte[2*1024 * 1024];
      byte[] arr4 = new byte[2*1024 * 1024];
      byte[] arr5 = new byte[128* 1024];
         
      byte[] arr6 = new byte[2*1024 * 1024];
````

日志
````
    “0.308: [GC (Allocation Failure) 0.308: [ParNew (promotion failed): 7260K->7970K(9216K), 0.0048975 secs]0.314: [CMS: 8194K->6836K(10240K), 0.0049920 secs] 11356K->6836K(19456K), [Metaspace: 2776K->2776K(1056768K)], 0.0106074 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
    Heap
    
    par new generation   total 9216K, used 2130K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
     eden space 8192K,  26% used [0x00000000fec00000, 0x00000000fee14930, 0x00000000ff400000)
     from space 1024K,   0% used [0x00000000ff500000, 0x00000000ff500000, 0x00000000ff600000)
     to   space 1024K,   0% used [0x00000000ff400000, 0x00000000ff400000, 0x00000000ff500000)
    
    concurrent mark-sweep generation total 10240K, used 6836K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
    Metaspace       used 2782K, capacity 4486K, committed 4864K, reserved 1056768K
     class space    used 300K, capacity 386K, committed 512K, reserved 1048576K”
     
     [CMS: 8194K->6836K(10240K), 0.0049920 secs] 11356K->6836K(19456K), [Metaspace: 2776K->2776K(1056768K)], 0.0106074 secs]
````


#### 分析
>基于执行arr6 的时候 ,申请 2m

1 arr 是大对象,直接进old ,  3个2m 的对象+128  在申请会gc

2 但这几个对象都是强引用,收不掉, 放到survivor  放不下,

3 这时old 有4m ,放6m+128  一定放不下,担保,ParNew 强引用,还是存活,会触发FULL GC 清掉 4m 大对象

````
    [CMS: 8194K->6836K(10240K), 0.0049920 secs] 11356K->6836K(19456K), [Metaspace: 2776K->2776K(1056768K)], 0.0106074 secs]
````

4 CMS   8194K->6836K 从哪来的?   他会先放2个2m 不够gc,最后放后面   4+2+2-4+2+其他(包括那128) =6836K

5 eden 放入 arr6 ,eden 占用2m 

#### 总结
1 FULL 触发,忘了看下前面画的图
2 还有一个，就是老年代被使用率达到了92%的阈值，也会触发Full GC。

### 048、第7周作业：自己尝试着分析一把你们线上系统的JVM GC日志

> 2020/7/8  我专门试了下测试机,  表现打分不及格


### 049、第7周答疑：本周问题答疑汇总

1 补手动态年龄, 是>= 进入old  ,每次Minor GC过后就会触发动态年龄判定机制的


### 050、动手实验：使用 jstat 摸清线上系统的JVM运行状况
> 他可以轻易的让你看到当前运行中的系统，他的JVM内的Eden、Survivor、老年代的内存使用情况，还有Young GC和Full gC的执行次数以及耗时

常用 jstat -gc PID
````
  S0C：这是From Survivor区的大小
  
  
  S1C：这是To Survivor区的大小
  
  S0U：这是From Survivor区当前使用的内存大小
  
  S1U：这是To Survivor区当前使用的内存大小
  
  EC：这是Eden区的大小
  
  EU：这是Eden区当前使用的内存大小
  
  OC：这是老年代的大小
  
  OU：这是老年代当前使用的内存大小
  
  MC：这是方法区（永久代、元数据区）的大小
  
  MU：这是方法区（永久代、元数据区）的当前使用的内存大小
  
  YGC：这是系统运行迄今为止的Young GC次数
  
  YGCT：这是Young GC的耗时
  
  FGC：这是系统运行迄今为止的Full GC次数
  
  FGCT：这是Full GC的耗时
  
  GCT：这是所有GC的总耗时
````

其他的jstat命令
````
    除了上面的jstat -gc命令是最常用的以外，他还有一些命令可以看到更多详细的信息，如下所示：
    
    jstat -gccapacity PID：堆内存分析
    
    jstat -gcnew PID：年轻代GC分析，这里的TT和MTT可以看到对象在年轻代存活的年龄和存活的最大年龄
    
    jstat -gcnewcapacity PID：年轻代内存分析
    
    jstat -gcold PID：老年代GC分析
    
    jstat -gcoldcapacity PID：老年代内存分析
    
    jstat -gcmetacapacity PID：元数据区内存分析
````

jstat -gc PID 1000 10

 主要功能 (可以配合gc 日志)
>这行命令，他的意思就是每隔1秒钟更新出来最新的一行jstat统计信息，一共执行10次jstat统计

+ 能动态推测占用问题   ,新生代对象增长的速率
+ Young GC的触发频率和每次耗时
+ 每次Young GC后有多少对象是存活和进入老年代
+ Full GC的触发时机和耗时

### 051、动手实验：使用jmap和jhat摸清线上系统的对象分布

>使用jmap了解系统运行时的内存区域

#### map -heap PID
> 堆占用状态

#### jmap -histo PID

>jvm中的对象对内存占用的情况，只要直接用jmap -histo命令即可，非常好用

#### 使用jmap生成堆内存转储快照
>jmap -dump:live,format=b,file=dump.hprof PID

#### 使用jhat在浏览器中分析堆转出快照
> jhat dump.hprof -port 7000    去分析堆快照了，jhat内置了web服务器，他会支持你通过浏览器来以图形化的方式分析堆转储快照

### 052、从测试到上线：如何分析JVM运行状况及合理优化？
>优化思路其实简单来说就一句话：

>尽量让每次Young GC后的存活对象小于Survivor区域的50%，都留存在年轻代里。尽量别让对象进入老年代。尽量减少Full GC的频率，避免频繁Full GC对JVM性能的影响。

#### 对线上系统进行JVM监控
+ 第一种方法会“low”一些，其实就是每天在高峰期和低峰期都用jstat、jmap、jhat等工具去看看线上系统的JVM运行是否正常，有没有频繁Full GC的问题。

+ 第二种方法在中大型公司里会多一些，大家都知道，很多中大型公司都会部署专门的监控系统，比较常见的有Zabbix、OpenFalcon、Ganglia，等等。

### 053、案例实战：每秒10万并发的BI系统，如何定位和解决频繁Young GC问题？
> 还是服务于百万级商家的BI系统

> 可以用while(true)  模拟
####  技术痛点：实时自动刷新报表 + 大数据量报表
> 频繁请求数据,和大数据 都和一直生成大量对象

> 用jstat 分析运行轨迹和 gc 次数,时间


### 054、案例实战：每日百亿数据量的实时分析引擎，如何定位和解决频繁Full GC问题？

> 计算系统的特点 ,持续不断的 计算,数据提取和计算的任务。 都会生成行的对象

> 占用和运行逻辑 和上面一样,这儿主要写 优化处理

参数      (大对象20m)
````
    -XX:NewSize=104857600 -XX:MaxNewSize=104857600 -XX:InitialHeapSize=209715200 -XX:MaxHeapSize=209715200 -XX:SurvivorRatio=8  -XX:MaxTenuringThreshold=15 -XX:PretenureSizeThreshold=20971520 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:gc.log
````

模拟代码

````
    public static void main(String[] args) {
           Thread.sleep(30000);
           while (true) {
               loadData();
           }
       }
       private static void loadData() {
           byte[] data = null;
           for (int i = 0; i < 4; i++) {
               data = new byte[10 * 1024 * 1024];
           }
           data=null;
           byte[] data1 = new byte[10 * 1024 * 1024];
           byte[] data2 = new byte[10 * 1024 * 1024];
           byte[] data3 = new byte[10 * 1024 * 1024];
           data3 = new byte[10 * 1024 * 1024];
           Thread.sleep(1000);
       }
````

大概意思其实就是，每秒钟都会执行一次loadData()方法，他会分配4个10MB的数组，但是都立马成为垃圾，但是会有data1和data2两个10MB的数组是被变量引用必须存活的，此时Eden区已经占用了六七十MB空间了，接着是data3变量依次指向了两个10MB的数组，这是为了在1s内触发Young GC的。

#### 分析

老年代总共就100MB左右，gc 每次存活30M ,survivor 放不下,直接进入old ,加上其他对象,两三次Young 就需要一次FULL 

还有一点,每次Full GC都是由Young GC触发的，因为Young GC过后存活对象太多要放入老年代，老年代内存不够了触发Full GC，所以必须得等Full GC执行完毕了，Young GC才能把存活对象放入老年代，才算结束。这就导致Young GC也是速度非常慢。



#### 优化
-XX:NewSize=209715200 -XX:MaxNewSize=209715200 -XX:InitialHeapSize=314572800 -XX:MaxHeapSize=314572800 -XX:SurvivorRatio=2  -XX:MaxTenuringThreshold=15 -XX:PretenureSizeThreshold=20971520 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:gc.log

把堆大小调大为了300MB，年轻代给了200MB，同时“-XX:SurvivorRatio=2”表明，Eden:Survivor:Survivor的比例为2:1:1，所以Eden区是100MB，每个Survivor区是50MB，老年代也是100MB。


### 055、第8周作业
> 公司实战,恩........

### 056、第8周答疑：本周问题答疑汇总
> ...

### 057、案例实战：每秒十万QPS的社交APP 如何优化GC性能提升3倍？

>一个有高峰期每秒十万QPS的社交APP 这类APP，在晚上高峰期，流量最大的一个模块，其实就是个人主页模块 qps 很高


 高并发查询导致对象快速进入老年代  年轻代的Eden区会迅速的被填满，并且频繁的触发Young GC ,每次很多对象是需要存活下来的 ,经常会出现Young GC过后存活对象较多，在Survivor区中放不下的问题,会导致大量的对象快速的进入老年代中

老年代必然会触发频繁GC 会导致个人主页服务对应的JVM频繁的发生老年代的GC

#### 优化前的线上系统JVM参数
>最核心的优化点，主要应该是增加机器，尽量让每台机器承载更少的并发请求，减轻压力。

>同时，给年轻代的Survivor区域更大的内存空间，让每次Young GC后的存活对象务必停留在Survior中，别进入老年代

>-XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=5

在每5次 CMS后 执行一次老年代的内存整理

上述两个参数“-XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=5”是设置的5次Full GC之后才会进行一次压缩操作，解决内存碎片的问题，空出来大片的连续可用内存空间。

#### 优化方案
+  判断出来每次Young GC后存活对象有多少，然后就是增加Survivor区的内存，避免对象快速进入老年代。 减少FULL GC

+ 在降低了Full GC频率之后，务必设置如下参数“-XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0”，每次Full GC后都整理一下内存碎片。

> 如果有很多内存碎片，  也许第一次Full GC是一小时才有，第二次Full GC也许是40分钟之后，第三次Full GC可能就是20分钟之后，要是不解决CMS内存碎片问题，必然导致Full GC慢慢变得越来越频繁。

### 058、案例实战：垂直电商APP后台系统，如何对Full GC进行深度优化？

>其实现在除了淘宝、京东、天猫、唯品会这些超大型的电商平台之外，国内还是有很多中小型的垂直类电商公司的。 他们做的主要是一些细分领域的电商业务，比如说有的APP专门做消费分期类的电商业务，在他们的APP里你主要是进行购物，然后可以分期付费。

#### 垂直电商APP的JVM性能问题
> 默认的JVM参数是系统负载逐渐增高的时候一个最大的问题,因为 这种默认survivor,eden 给都不大, 会大量对象进old ,频繁FULL 

#### 公司级别的JVM参数模板
4核8G  还有其他进程
> -Xms4096M -Xmx4096M -Xmn3072M -Xss1M  -XX:PermSize=256M -XX:MaxPermSize=256M -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFaction=92 -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0


#### 优化每次Full GC的性能？

> -XX:+CMSParallelInitialMarkEnabled -XX:+CMSScavengeBeforeRemark

>一个参数是“-XX:+CMSParallelInitialMarkEnabled”，这个参数会在CMS垃圾回收器的“初始标记”阶段开启多线程并发执行。

大家应该还记得初始标记阶段，是会进行Stop the World的，会导致系统停顿，所以这个阶段开启多线程并发之后，可以尽可能优化这个阶段的性能，减少Stop the World的时间。



>另外一个参数是“-XX:+CMSScavengeBeforeRemark”，这个参数会在CMS的重新标记阶段之前，先尽量执行一次Young GC。

主要作用就是让Young GC 先执行,CMS 在CMS的重新标记阶段就可以少扫描一些对象，此时就可以提升CMS的重新标记阶段的性能，减少他的耗时。


### 059、案例实战：新手工程师不合理设置JVM参数，是如何导致频繁Full GC的？

####  问题的产生
>某天团队里一个新手工程师大概是心血来潮，觉得自己网上看到了某个JVM参数，以为学会了绝世武功秘籍，于是就在当天上线一个系统的时候，自作主张设置了一个JVM参数

#### 查看GC日志
在日志里，看到了一个“Metadata GC Threshold”的字样，类似于如下日志：
>【Full GC（Metadata GC Threshold）xxxxx, xxxxx】

#### 一个综合性的分析思路
这个很明显是系统在运行过程中，不停的有新的类产生被加载到Metaspace区域里去，然后不停的把Metaspace区域占满，接着触发一次Full GC回收掉Metaspace区域中的部分类。

然后这个过程反复的不断的循环，进而造成Metaspace区域反复被占满，然后反复导致Full GC的发生

#### 到底是什么类不停的被加载？
> “-XX:TraceClassLoading -XX:TraceClassUnloading”

开启类加载,卸载的追踪日志

堆日志，里面显示类似如下的内容：
>【Loaded sun.reflect.GeneratedSerializationConstructorAccessor from __JVM_Defined_Class】

#### JVM创建的奇怪类有什么玄机？
这种JVM自己创建的奇怪的类，他们的Class对象都是SoftReference，也就是软引用的。

那么SoftReference对象到底在GC的时候要不要回收是通过什么公式来判断的呢？
>是如下的一个公式：clock - timestamp <= freespace * SoftRefLRUPolicyMSPerMB。

这个公式的意思就是说，“clock - timestamp”代表了一个软引用对象他有多久没被访问过了，freespace代表JVM中的空闲内存空间，SoftRefLRUPolicyMSPerMB代表每一MB空闲内存空间可以允许SoftReference对象存活多久。

举个例子，假如说现在JVM创建了一大堆的奇怪的类出来，这些类本身的Class对象都是被SoftReference软引用的。然后现在JVM里的空间内存空间有3000MB，SoftRefLRUPolicyMSPerMB的默认值是1000毫秒，那么就意味着，此时那些奇怪的SoftReference软引用的Class对象，可以存活3000 * 1000 = 3000秒，就是50分钟左右。

当然上面都是举例而已，大家都知道，一般来说发生GC时，其实JVM内部或多或少总有一些空间内存的，所以基本上如果不是快要发生OOM内存溢出了，一般软引用也不会被回收。

#### 为什么JVM创建的奇怪的类会不停的变多？
> 因为文章开头那个新手工程师不知道从哪里扒出来了SoftRefLRUPolicyMSPerMB这个JVM启动参数，他直接把这个参数设置为0了。

实际上一旦这个参数设置为0之后，直接导致clock - timestamp <= freespace * SoftRefLRUPolicyMSPerMB这个公式的右半边是0，就导致所有的软引用对象，比如JVM生成的那些奇怪的Class对象，刚创建出来就可能被一次Young GC给带着立马回收掉一些。

也许下一次gc又会回收掉一些奇怪的类，但是马上JVM还会继续生成这种类，最终就会导致Metaspace区域被放满了，一旦Metaspace区域被占满了，就会触发Full GC，然后回收掉很多类，接着再次重复上述循环

#### 如何解决这个问题？
>XX:SoftRefLRUPolicyMSPerMB=0

这个参数设置大一些即可，千万别让一些新手同学设置为0，可以设置个1000，2000，3000，或者5000毫秒，都可以 

### 060、案例实战：一次线上系统每天数十次Full GC导致频繁卡死的优化实战！

#### 未优化前的JVM性能分析
> jvm 自带jstat

> Zabbix、Ganglia、Open-Falcon、Prometheus之类的可视化监控平台，其实接入都非常简单，如果把线上系统接入了这些平台，可以直接图形化看到JVM的表现。

````
机器配置：2核4G

JVM堆内存大小：2G

系统运行时间：6天

系统运行6天内发生的Full GC次数和耗时：250次，70多秒

系统运行6天内发生的Young GC次数和耗时：2.6万次，1400秒
````
综合分析一下，就可以知道，大致来说每天会发生40多次Full GC，平均每小时2次，每次Full GC在300毫秒左右；

#### 未优化前的线上JVM参数
> -Xms1536M -Xmx1536M -Xmn512M -Xss256K -XX:SurvivorRatio=5 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=68 -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC

其实基本上跟我们之前看到的参数没多大的不同，一个4G的机器上，给JVM的堆内存是设置了1.5G的大小，其中新生代是给了512M，老年代是1G。

比较关键的是“-XX:SurvivorRatio”设置为了5，也就是说，Eden:Survivor1:Survivor2的比例是5:1:1

所以此时Eden区域大致为365M，每个Survivor区域大致为70MB。

而且这里有一个非常关键的参数，那就是“-XX:CMSInitiatingOccupancyFraction”参数设置为了68

所以一旦老年代内存占用达到68%，也就是大概有680MB左右的对象时，就会触发一次Full GC

#### 根据线上系统的GC情况倒推运行内存模型

+ 每分钟会发生3次Young GC，说明系统运行20秒就会让Eden区满，也就是产生300多MB的对象，平均下来系统每秒钟会产生15~20MB的对象，

+ 每小时2次Full GC推断出，30分钟会触发一次Full GC, -XX:CMSInitiatingOccupancyFraction=68 ,系统运行30分钟就会导致老年代里有600多MB的对象      

结论:
+ 每隔20秒会让300多MB的Eden区满触发一次Young GC，一次Young GC耗时50毫秒左右。

+ 每隔30分钟会让老年代里600多MB空间占满，进而触发一次CMS的GC，一次Full GC耗时300毫秒左右。

#### 老年代里到底为什么会有那么多的对象？

一般来说，每次Young GC过后大概就存活几十MB而已，那么Survivor区域因为就70MB，所以经常会触发动态年龄判断规则，导致偶尔一次Young GC过后有几十MB对象进入老年代。

但这不足让600M 老年代,快速填满

这个时候我们通过jstat运行的时候就观察到一个现象，就是老年代里的内存占用在系统运行的时候，不知道为什么系统运行着运行着，就会突然有几百MB的对象占据在里面，大概有五六百MB的对象，一直占据在老年代中

#### 定位系统的大对象

dump内存快照。通过内存快照的分析，直接定位出来那个几百MB的大对象，就是几个Map之类的数据结构，这是什么东西？直接让负责写那个系统代码的几个同学分析了一下，明显是从数据库里查出来的！

#### 优化

+ 第一步，让开发同学解决代码中的bug，避免一些极端情况下SQL语句里不拼接where条件，务必要拼接上where条件，不允许查询表中全部数据。彻底解决那个时不时有几百MB对象进入老年代的问题。

+ 第二步，年轻代明显过小，Survivor区域空间不够，因为每次Young GC后存活对象在几十MB左右，如果Survivor就70MB很容易触发动态年龄判定，让对象进入老年代中。所以直接调整JVM参数如下：
>-Xms1536M -Xmx1536M -Xmn1024M -Xss256K -XX:SurvivorRatio=5 -XX:PermSize=256M -XX:MaxPermSize=256M  -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=92 -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC

直接把年轻代空间调整为700MB左右，每个Surivor是150MB左右，此时Young GC过后就几十MG存活对象，一般不会进入老年代。

反之老年代就留500MB左右就足够了，因为一般不会有对象进入老年代。

而且调整了参数“-XX:CMSInitiatingOccupancyFraction=92”，避免老年代仅仅占用68%就触发GC，现在必须要占用到92%才会触发GC。

最后，就是主动设置了永久代大小为256MB，因为如果不主动设置会导致默认永久代就在几十MB的样子，很容易导致万一系统运行时候采用了反射之类的机制，可能一旦动态加载的类过多，就会频繁触发Full GC。


### 061、案例实战：电商大促活动下，严重Full GC导致系统直接卡死的优化实战

>有一次一个新系统上线，平时都还算正常，结果有一次大促活动的时候，这个系统就直接卡死不动了

首先使用jstat去看一下系统运行情况，令人吃惊的事情是：JVM几乎每秒都执行一次Full GC，每次都耗时几百毫秒。

#### 问题
> System.gc()  显式调用了FULL GC

#### 解决
>-XX:+DisableExplicitGC。这个参数的意思就是禁止显式执行GC，不允许你来通过代码触发GC。

### 062、第9周作业
> 总结 

### 063、第9周答疑以及学员思考题总结汇总
> 


### 064、案例实战：一次线上大促营销活动导致的内存泄漏和Full GC优化
>一次我们线上推了一个大促销活动，大致就是类似于在某个特定节日里，突然给所有用户发短信、邮件、APP Push消息，说现在有个特别优惠的活动，如果参与的话肯定可以得到很大的实惠！


#### 初步排查CPU负载过高的原因
+ 第一个场景，是你自己在系统里创建了大量的线程，这些线程同时并发运行，而且工作负载都很重，过多的线程同时并发运行就会导致你的机器CPU负载过高。

+ 第二个场景，就是你的机器上运行的JVM在执行频繁的Full GC，Full GC是非常耗费CPU资源的，他是一个非常重负载的过程

#### 初步排查频繁Full GC的问题
一般可能性有三个：

+ 内存分配不合理，导致对象频繁进入老年代，进而引发频繁的Full GC；

+ 存在内存泄漏等问题，就是内存里驻留了大量的对象塞满了老年代，导致稍微有一些对象进入老年代就会引发Full GC；

+ 永久代里的类太多，触发了Full GC

还有一种显式调用  但少

#### 对线上系统导出一份内存快照
>jmap -dump:format=b,file=文件名 [服务进程ID]

#### 分析
> 用mat 分析内存泄露的情况

### 065、案例实战：百万级数据误处理导致的频繁Full GC问题优化
> 一次版本升级 所有用户全部看到的是一片空白和错误信息。线上系统所在机器的CPU负载非常高，持续走高，甚至直接导致机器都宕机了。

#### CPU负载高原因分析
> jstat Full GC 频繁,且耗时

#### Full GC频繁的原因分析
> 这个系统因为主要是用来进行大量数据处理然后提供数据给用户查看的，所以当时可是给JVM的堆分配了20G的内存，其中10G给了年轻代，10G给了老年代

#### 以前那套GC优化策略还能奏效吗？
> 难,新生对象多且大 ,survivor 加大,也会放不下

#### 分析
> 用mat 分析大对象, 查看调用链,看他的调用

### “String.split()”会造成内存泄漏？
 > 大量调用会 生成大量对象
 
 #### 代码如何进行优化？
 > 比较核心的思路，就是开启多线程并发处理大量的数据，尽量提升数据处理完毕的速度，这样到触发Young GC的时候避免过多的对象存活下来。
 
 ### 066、阶段性复习：JVM运行原理和GC原理你真的搞懂了吗？

#### JVM和GC的运行原理，你都能搞懂了吗？
>年轻代、老年代、Metaspace（也就是以前的永久代）。 

+ 我们写好的系统会不停的运行，运行的时候是不是就会不停的在年轻代的Eden区域中创建各种对象？
+ 一般创建对象都是在各种方法里执行的，一旦方法运行完毕，方法局部变量引用的那些对象就会成为Eden区里的垃圾对象，就是可以被回收的状态
+ 随着Eden区不断的创建对象，就会逐步的塞满，当然这个时候可能塞满Eden区的对象里大多数都是垃圾对象。一旦Eden区塞满之后，就会触发一次Young GC。然后把存活对象都放入第一个Survivor区域中
+ 垃圾回收器就会直接回收掉Eden区里剩余的全部垃圾对象，在整个这个垃圾回收的过程中全程会进入Stop the Wold状态，也就是暂停系统工作线程，系统代码全部停止运行，不允许创建新的对象只有这样，才能让垃圾回收器专心工作，找出来存活对象，回收掉垃圾对象
+ 一旦垃圾回收全部完毕之后，也就是存活对象都进入了Survivor区域，然后Eden区都清空了，那么Young GC执行完毕，此时系统恢复工作，继续在Eden区里创建对象

负责Young GC的垃圾回收器有很多种，但是常用的就是ParNew垃圾回收器

#### 对象什么时候进入老年代？
对象进入老年代区域中
1. 一个对象在年轻代里躲过15次垃圾回收，年龄太大了，寿终正寝，进入老年代
2. 对象太大了，超过了一定的阈值，直接进入老年代，不走年轻代
3. 一次Young GC过后存活对象太多了，导致Survivor区域放不下了，这批对象会进入老年代
4. 可能几次Young GC过后，Surviovr区域中的对象占用了超过50%的内存，此时会判断如果年龄1+年龄2+年龄N的对象总和超过了Survivor区域的50%，此时年龄N以及之上的对象都进入老年代，这是动态年龄判定规则

#### 老年代的GC是如何触发的？
1. 老年代自身可以设置一个阈值，有一个JVM参数可以控制，一旦老年代内存使用达到这个阈值，就会触发Full GC，一般建议调节大一些，比如92%
2. 在执行Young GC之前，如果判断发现老年代可用空间小于了历次Young GC后升入老年代的平均对象大小的话，那么就会在YoungGC之前触发Full GC，先回收掉老年代一批对象，然后再执行Young GC。
3. 如果Young GC过后的存活对象太多，Survivor区域放不下，就要放入老年代，要是此时老年代也放不下，就会触发Full GC，回收老年代一批对象，再把这些年轻代的存活对象放入老年代中

#### 正常情况下的系统
会有一定频率的Young GC，一般在几分钟一次Young GC，或者几十分钟一次Young GC，一次耗时在几毫秒到几十毫秒的样子，都是正常的。

### 067、阶段性复习：JVM性能优化到底该怎么做？
#### 一个新系统开发完毕之后如何设置JVM参数？
+ 首先大家应该估算一下自己负责的系统每个核心接口每秒多少次请求，每次请求会创建多少个对象，每个对象大概多大，每秒钟会使用多少内存空间？
+ 然后就可以估算出来多长时间会发生一次Young GC，而且可以估算一下发生Young GC的时候，会有多少对象存活下来，会有多少对象升入老年代里，老年代对象增长的速率大概是多少，多久之后会触发一次Full GC。

原则就是：尽可能让每次Young GC后存活对象远远小于Survivor区域，避免对象频繁进入老年代触发Full GC。

#### 在压测之后合理调整JVM参数
压测，此时在模拟线上压力的场景下，可以用jstat等工具去观察JVM的运行内存模型：
````
Eden区的对象增长速率多块？
Young GC频率多高？
一次Young GC多长耗时？
Young GC过后多少对象存活？
老年代的对象增长速率多高？
Full GC频率多高？
一次Full GC耗时？
````

#### 线上系统的监控和优化
>高大上的做法就是通过Zabbix、Open-Falcon之类的工具来监控机器和JVM的运行，频繁Full GC就要报警。

> 差一点的做法，就是在机器上运行jstat，让其把监控信息写入一个文件，每天定时检查一下看一看。

#### 线上频繁Full GC的几种表现

表现如下：
````
机器CPU负载过高；
频繁Full GC报警；
系统无法处理请求或者处理过慢
````
所以一旦发生上述几个情况，大家第一时间得想到是不是发生了频繁Full GC。

#### 频繁Full GC的几种常见原因
+ 系统承载高并发请求，或者处理数据量过大，导致Young GC很频繁，而且每次Young GC过后存活对象太多，内存分配不合理，Survivor区域过小，导致对象频繁进入老年代，频繁触发Full GC。
+ 系统一次性加载过多数据进内存，搞出来很多大对象，导致频繁有大对象进入老年代，必然频繁触发Full GC
+ 系统发生了内存泄漏，莫名其妙创建大量的对象，始终无法回收，一直占用在老年代里，必然频繁触发Full GC
+ Metaspace（永久代）因为加载类过多触发Full GC
+ 误调用System.gc()触发Full GC

其实常见的频繁Full GC原因无非就上述那几种，所以大家在线上处理Full GC的时候，就从这几个角度入手去分析即可，核心利器就是jstat。

#### 一个统一的JVM参数模板
> 通过机器大概配置,整理一个 参数模板

### 068、如何为你的面试准备自己负责的系统中的JVM优化案例？
#### 面试中回答JVM生产优化问题？
比较常见的做法，就是把之前学习过的知识，归纳总结出来一套通用的方法付论，然后面试的时候就聊这套通用方法论即可

#### 如果你的系统访问量和数据量暴增10倍或者100倍

应该把频繁Full GC问题和你自己的业务系统结合起来，自己深度思考，自己整理出来几个自己系统可能发生的JVM性能问题，然后整理出一套解决方案出来。

#### JVM的优化注意点
 少用一些奇怪的参数 ,很容易中招
 
### 070、第10周答疑汇总

####  新学到参数：
````
      -XX:+CMSParallelInitialMarkEnabled表示在初始标记的多线程执行，减少STW；
      -XX:+CMSScavengeBeforeRemark：在重新标记之前执行minorGC减少重新标记时间；
      -XX:+CMSParallelRemarkEnabled:在重新标记的时候多线程执行，降低STW；
      -XX：CMSInitiatingOccupancyFraction=92和-XX:+UseCMSInitiatingOccupancyOnly配套使用，如果不设置后者，jvm第一
      次会采用92%但是后续jvm会根据运行时采集的数据来进行GC周期，如果设置后者则jvm每次都会在92%的时候进行gc；
      -XX:+PrintHeapAtGC:在每次GC前都要GC堆的概况输出
````
#### fullgc的发生情况：
1. 老年代可用内存小于新生代全部对象的大小，又没有开启空间担保，就会直接触发fullgc。
2. 如果新生代存活大小大于老年代空间，并且老年代空间小于历次晋升的平均内存大小，也会执行fullgc。
3. 大对象或者动态年龄进入老年代，而老年代空间不足，也会执行fullgc。
4. 如果是cms回收器，那么老年代内存使用到92%之后，就会触发fullgc，因为并发清除阶段需要给用户线程预留内存空间。

#### 一个类从加载到使用，一般会经历哪些过程
答：加载->验证->准备->解析->初始化->使用->卸载

1. 加载:将编译好的".class"字节码文件加载到JVM中
2. 验证:根据JVM规范，校验加载进来的".class"字节码文件
3. 准备:给类和类变量分配一定的内存空间，且给类变量设置默认的初始值(0或者nul) 
4. 解析:把符号引用替换为直接引用的过程
5. 初始化:根据类初始化代码给类变量赋值 


### 071、Java程序员的梦魇：线上系统突然挂掉，可怕的OOM内存溢出！