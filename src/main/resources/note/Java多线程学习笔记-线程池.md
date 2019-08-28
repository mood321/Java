### Java 多线程-线程池和JUC笔记 重写版本
#### 线程池
   + 什么是线程池？为什么要用线程池？ 
        ```` 
     1 降低资源的消耗。降低线程创建和销毁的资源消耗；
     2 提高响应速度：线程的创建时间为T1，执行时间T2,销毁时间T3，免去T1和T3的时间
     3 提高线程的可管理性。
     ````
   + 线程池原理
       > 线程池做的工作主要是控制运行的线程的数量，处理过程中将任务放入队列，然后在线程创建后
        启动这些任务，如果线程数量超过了最大数量超出数量的线程排队等候，等其它线程执行完毕，
        再从队列中取出任务来执行。他的主要特点为：线程复用；控制最大并发数；管理线程
   + 线程复用
       > 每一个 Thread 的类都有一个 start 方法。 当调用 start 启动线程时 Java 虚拟机会调用该类的 run
         方法。 那么该类的 run() 方法中就是调用了 Runnable 对象的 run() 方法。 我们可以继承重写
         Thread 类，在其 start 方法中添加不断循环调用传递过来的 Runnable 对象。 这就是线程池的实
         现原理。循环方法中不断获取 Runnable 是用 Queue 实现的，在获取下一个 Runnable 之前可以
         是阻塞的
   + 线程池的组成
        ````
        一般的线程池主要分为以下 4 个组成部分：
        1.  线程池管理器：用于创建并管理线程池
        2.  工作线程：线程池中的线程
        3.  任务接口：每个任务必须实现的接口，用于工作线程调度其运行
        4.  任务队列：用于存放待处理的任务，提供一种缓冲机制
        Java 中的线程池是通过 Executor 框架实现的，该框架中用到了 Executor，Executors，
        ExecutorService，ThreadPoolExecutor ，Callable 和 Future、FutureTask 这几个类。
        ````
        ![](https://s2.ax1x.com/2019/08/29/mHOHc6.jpg)
         ThreadPoolExecutor 的构造方法如下
        ````
        public ThreadPoolExecutor(int corePoolSize,int maximumPoolSize, long keepAliveTime,
        TimeUnit unit, BlockingQueue<Runnable> workQueue) {
             this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             Executors.defaultThreadFactory(), defaultHandler);
        }
        ````
        1.  corePoolSize：指定了线程池中的线程数量。
        2.  maximumPoolSize：指定了线程池中的最大线程数量。
        3.  keepAliveTime：当前线程池数量超过 corePoolSize 时，多余的空闲线程的存活时间，即多
        次时间内会被销毁。
        4.  unit：keepAliveTime 的单位。
        5.  workQueue：任务队列，被提交但尚未被执行的任务。
        6.  threadFactory：线程工厂，用于创建线程，一般用默认的即可。
        7.  handler：拒绝策略，当任务太多来不及处理，如何拒绝任务。
   
   + 拒绝策略
       ````
       线程池中的线程已经用完了，无法继续为新任务服务，同时，等待队列也已经排满了，再也
       塞不下新任务了。这时候我们就需要拒绝策略机制合理的处理这个问题。
       ````
       JDK 内置的拒绝策略如下：
       1. AbortPolicy ： 直接抛出异常，阻止系统正常运行。
       2. CallerRunsPolicy ： 只要线程池未关闭，该策略直接在调用者线程中，运行当前被丢弃的
       任务。显然这样做不会真的丢弃任务，但是，任务提交线程的性能极有可能会急剧下降。
       3. DiscardOldestPolicy ： 丢弃最老的一个请求，也就是即将被执行的一个任务，并尝试再
       次提交当前任务。
       4. DiscardPolicy ： 该策略默默地丢弃无法处理的任务，不予任何处理。如果允许任务丢
       失，这是最好的一种方案。
       以上内置拒绝策略均实现了 RejectedExecutionHandler 接口，若以上策略仍无法满足实际
       需要，完全可以自己扩展 RejectedExecutionHandler 接口。   
   + Java 线程池工作过程
        1.  线程池刚创建时，里面没有一个线程。任务队列是作为参数传进来的。不过，就算队列里面
        有任务，线程池也不会马上执行它们。
        2.  当调用 execute() 方法添加一个任务时，线程池会做如下判断：
        a)  如果正在运行的线程数量小于 corePoolSize，那么马上创建线程运行这个任务；
        b) 如果正在运行的线程数量大于或等于 corePoolSize，那么将这个任务放入队列；
        c)  如果这时候队列满了，而且正在运行的线程数量小于 maximumPoolSize，那么还是要
        创建非核心线程立刻运行这个任务；
        d) 如果队列满了，而且正在运行的线程数量大于或等于 maximumPoolSize，那么线程池
        会抛出异常 RejectExecutionException。
        3.  当一个线程完成任务时，它会从队列中取下一个任务来执行。
        4.  当一个线程无事可做，超过一定的时间（keepAliveTime）时，线程池会判断，如果当前运
        行的线程数大于 corePoolSize，那么这个线程就被停掉。所以线程池的所有任务完成后，它
        最终会收缩到 corePoolSize 的大小。
        ![](https://s2.ax1x.com/2019/08/29/mHXsVe.jpg)
   
   + 预定义的线程池
       - FixedThreadPool
       > 创建固定线程数量的，适用于负载较重的服务器，使用了无界队列
       - SingleThreadExecutor
       >创建单个线程，需要顺序保证执行任务，不会有多个线程活动，使用了无界队列
      - CachedThreadPool
       > 会根据需要来创建新线程的，执行很多短期异步任务的程序，使用了SynchronousQueue
      - WorkStealingPool（JDK7以后） 
      > 基于ForkJoinPool实现
      - ScheduledThreadPoolExecutor 
      ````
       需要定期执行周期任务，Timer不建议使用了。
       newSingleThreadScheduledExecutor：只包含一个线程，只需要单个线程执行周期任务，保证顺序的执行各个任务
       newScheduledThreadPool 可以包含多个线程的，线程执行周期任务，适度控制后台线程数量的时候
       方法说明：
       schedule：只执行一次，任务还可以延时执行
       scheduleAtFixedRate：提交固定时间间隔的任务
       scheduleWithFixedDelay：提交固定延时间隔执行的任务
       ````
       ps : 定时任务异常自己处理 会停止整个定时任务

#### JUC 组件
   + JAVA  阻塞队列
        
       + 概念
           ````
            1)当队列满的时候，插入元素的线程被阻塞，直达队列不满。
            2)队列为空的时候，获取元素的线程被阻塞，直到队列不空。
            ````
       + 生产者和消费者模式
          > 生产者就是生产数据的线程，消费者就是消费数据的线程。在多线程开发中，如果生产者处理速度很快，而消费者处理速度很慢，
            那么生产者就必须等待消费者处理完，才能继续生产数据。同样的道理，如果消费者的处理能力大于生产者，那么消费者就必须等待生产者。
            为了解决这种生产消费能力不均衡的问题，便有了生产者和消费者模式。生产者和消费者模式是通过一个容器来解决生产者和消费者的强耦合问题。
            生产者和消费者彼此之间不直接通信，而是通过阻塞队列来进行通信，所以生产者生产完数据之后不用等待消费者处理，直接扔给阻塞队列，
            消费者不找生产者要数据，而是直接从阻塞队列里取，阻塞队列就相当于一个缓冲区，平衡了生产者和消费者的处理能力
        
       + 常用方法 
       
            | 方法| 抛出异常| 返回值|一直阻塞| 超时退出|
             | ---   | ---:   | :---: |:---: |:---:|
            |插入方法|add|offer|put|Offer(time)|
            |移除方法|remove|poll|take|Poll(time)|
            |检查方法|element|peek|N/A|N/A|
            ````
             抛出异常：当队列满时，如果再往队列里插入元素，会抛出IllegalStateException（"Queuefull"）异常。当队列空时，从队列里获取元素会抛出NoSuchElementException异常。
             返回特殊值：当往队列插入元素时，会返回元素是否插入成功，成功返回true。如果是移除方法，则是从队列里取出一个元素，如果没有则返回null。
             一直阻塞：当阻塞队列满时，如果生产者线程往队列里put元素，队列会一直阻塞生产者线程，直到队列可用或者响应中断退出。当队列空时，如果消费者线程从队列里take元素，队列会阻塞住消费者线程，直到队列不为空。
             超时退出：当阻塞队列满时，如果生产者线程往队列里插入元素，队列会阻塞生产者线程一段时间，如果超过了指定的时间，生产者线程就会退出。
            ````
       + 常用阻塞队列 
           - ArrayBlockingQueue：一个由数组结构组成的有界阻塞队列。
           >按照先进先出原则，要求设定初始大小
           - LinkedBlockingQueue：一个由链表结构组成的有界阻塞队列。
            >按照先进先出原则，可以不设定初始大小，Integer.Max_Value
          - ArrayBlockingQueue和LinkedBlockingQueue不同：
           >锁上面：ArrayBlockingQueue只有一个锁，LinkedBlockingQueue用了两个锁，
           实现上：ArrayBlockingQueue直接插入元素，LinkedBlockingQueue需要转换。
          - PriorityBlockingQueue：一个支持优先级排序的无界阻塞队列。
           >默认情况下，按照自然顺序，要么实现compareTo()方法，指定构造参数Comparator
          - DelayQueue：一个使用优先级队列实现的无界阻塞队列。
           > 支持延时获取的元素的阻塞队列，元素必须要实现Delayed接口。适用场景：实现自己的缓存系统，订单到期，限时支付等等。
          - SynchronousQueue：一个不存储元素的阻塞队列。
           >每一个put操作都要等待一个take操作
          - LinkedTransferQueue：一个由链表结构组成的无界阻塞队列。
           > transfer()，必须要消费者消费了以后方法才会返回，tryTransfer()无论消费者是否接收，方法都立即返回。
          - LinkedBlockingDeque：一个由链表结构组成的双向阻塞队列。
           >可以从队列的头和尾都可以插入和移除元素，实现工作密取，方法名带了First对头部操作，带了last从尾部操作，另外：add=addLast;	remove=removeFirst;	take=takeFirst
       + 
        