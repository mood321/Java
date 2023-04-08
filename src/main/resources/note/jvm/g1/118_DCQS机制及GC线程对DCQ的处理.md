- 回顾：

  1、谁来消费DCQ？

  2、消费线程的数量是怎么确定的？

  3、说了这么多DCQ，DCQ到底是怎么写进去的？

  思考：DCQS到底啥时候算满？多少个refine线程去处理DCQS才合适？

   

  本节内容：

  5、DCQS又是个什么东西？

  6、DCQS是怎么更新的？

  7、如果DCQ没有处理完毕，就进入GC会怎么办？

   

   

  1、DCQS是什么？

  上节课其实已经简单说了什么叫做DCQS了，其实就是G1设计了二级缓存来解决并发冲突的，解决DCQ写入引用关系变更数据并发冲突的这么二级缓存。

  第一层缓存是在线程这一层，也就是说，每一个工作线程都会关联一个DCQ，每个线程在执行了引用更新操作的时候，都会往自己持有的那个DCQ里面写入变更信息。DCQ的长度默认是256，如果写满了，就重新申请一个新的DCQ，并把这个DCQ提交到第二级缓存，也就是一个DCQ Set里面去，我们叫这个二级缓存为DCQS。

  所以，所谓的DCQS其实不过就是一个存放DCQ的地方，当然DCQ本身也有长度，它的长度具体是怎么推算的，我们在下面的内容会有讲解。

   

  2、DCQS的白绿黄红四个挡位

  我们知道DCQS肯定是有上限的，当达到一定的阈值不能再提交的时候，工作线程就得自己去处理了。这个时候说明系统负载已经很重很重了，系统的运行速度可能会比较慢，因为工作线程要去处理DCQ更新RSet的引用关系去了。

  负载很重的时候，我们就要考虑应该启动多少个线程？

  （1）避免出现这种负担很重的情况

  （2）避免大量的refine线程导致的CPU资源占用问题

  那么我们应该怎么设计Refine线程的个数？因为摆在面前很现实的问题就是，如果说启用refine线程过多，会导致程序整体效率，一直都不高。如果说启用refine线程过少，可能会导致运行一段时间，DCQS就满了，工作线程要做一些额外的更新RSet的操作，这个时候整体性能也不高。

  ![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/24001700_1641210272.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

  那么到底refine线程的数量怎么设置？什么时候应该用多少个线程？针对这个问题G1对DCQS的数量做了四个区域。

  有三个参数来划分这四个区域：G1ConcRefinementGreenZone、G1ConcRefinementYellowZone、G1ConcRefinementRedZone，三个值默认都是0，如果说没有设置，G1会自动推断这个三个值。

  白区：[0,green)  如果说在这个区域，refine线程也不处理，也就是不启动refine线程来处理DCQ，只会有refine线程去新生代做抽样

  绿区：[green,yellow)  refine线程开始启动，并且个数DCQS的大小来计算启动refine线程的个数

  黄：[yellow,red)  所有refine线程都参与到DCQS的处理

  红：[red,正无穷)  所有refine线程以及系统工作线程都参与DCQ的处理

   

  Refine线程到底应该是几个呢？我们上面说了可以通过参数来设置个数，如果没有设置，就会和ParallelGCThreads相等。如果ParallelGCThreads没有设置，则会根据CPU核数来自动推断有多少个。推断公式为：如果核数小于或者等于8，则ParallelGCThreads = cpu核数，如果核数大于8，则ParallelGCThreads = 8 + （核数 - 8）* 5 / 8，此时如果没有设置refine个数，refine个数 = ParallelGCThreads。

  3+1  1个线程在做抽样，另外根据DCQS的size来启动线程。

   

  当DCQS处于绿区的时候，refine线程启动也有一定的讲究。我们说它会根据DCQS的大小来启动不同的refine个数。其实可以理解为，我们让每个refine线程处理多少个DCQ？

  我们DCQS长度是有限的对吧。不同的区又启动不同的refine数量。

  也就是，处理DCQ的步长。假如说，DCQS大小为9，步长设置为3（可通过参数设置，如果不设置，自动推断为refine个数，注意这里呢是参与处理DCQ的refine线程的个数），那么就会启动3个refine线程去处理。

   

  对于DCQS的几个阈值，设置的时候和ParallelGCThreads也有关系。Green = ParallelGCThreads，Yellow = 3 * green，red = 6*green。

   

  那么假如说，我们有总计4个refine线程处理DCQS。那么绿黄红按照步长（步长和refine线程数量相等）来计算的话是[4, 12, 24]。

  那么DCQS会在少于4个DCQ的时候，不启动refine线程。

  ![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/25439100_1641210272.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

  在大于等于4个DCQ，小于9个的时候启动1个线程。

  在 大于等于9个DCQ的时候，启动第二个线程。

  在 大于等于11个DCQ的时候，启动第三个线程。

  还有一个线程在处理新生代分区的抽样。

   

  在达到24个DCQ的时候工作线程也在处理DCQ。

  ![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/28650200_1641210272.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

  3、如果说DCQS直到最终GC的时候还是没有处理完，会怎么办？

  上面我们一直在强调会启动多个线程处理，并且，如果DCQ数量在小于绿区的阈值的时候，是没有refine线程在处理的。其实这个时候，是不需要refine线程处理的，G1为了保证运行过程中的效率，会在GC的时候，由GC线程来处理这些DCQ，因为GC线程其实也是有多个的，在STW的时候处理，速度也不会很慢。

  启动了refine线程，然而DCQ在GC的时候还是没有处理完，这个时候，就会由GC线程，参与处理没有完成处理的DCQ。直到全部处理完成，然后才会进行下一步的GC操作。

  ![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/26606500_1641210272.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

   