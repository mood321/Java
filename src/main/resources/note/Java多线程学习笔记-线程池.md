### Java 多线程-线程池和JUC笔记 重写版本
#### 线程池
   + 什么是线程池？为什么要用线程池？ 
        ```` 
     1 降低资源的消耗。降低线程创建和销毁的资源消耗；
     2 提高响应速度：线程的创建时间为T1，执行时间T2,销毁时间T3，免去T1和T3的时间
     3 提高线程的可管理性。
     ````
   + 合理配置线程池
     ````
     根据任务的性质来：计算密集型（CPU），IO密集型，混合型
     计算密集型：加密，大数分解，正则…….， 线程数适当小一点，最大推荐：机器的Cpu核心数+1，为什么+1，防止页缺失，(机器的Cpu核心=Runtime.getRuntime().availableProcessors();)
     IO密集型：读取文件，数据库连接，网络通讯, 线程数适当大一点，机器的Cpu核心数*2,
     混合型：尽量拆分，IO密集型>>计算密集型，拆分意义不大，IO密集型~计算密集型
     队列的选择上，应该使用有界，无界队列可能会导致内存溢出，OOM
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
   + CompletionService
        ````
         future1.get() 会阻塞其他已经执行完毕的线程 等待线程任务全部完毕 在去拿结果
         CompletionService的主要功能就是一边生成任务，一边获取任务的返回值。
         让两件事分开执行，任务之间不会互相阻塞。
         
         CompletionService在提交任务之后，会根据任务完成顺序来获取返回值，
         也就是谁先完成就返回谁的返回值

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
           - ArrayBlockingQueue：一个由数组结构组成的有界阻塞队列。（公平、非公平）
                >按照先进先出原则，要求设定初始大小
           - LinkedBlockingQueue：一个由链表结构组成的有界阻塞队列。（两个独立锁提高并发）
                >按照先进先出原则，可以不设定初始大小，Integer.Max_Value
          - ArrayBlockingQueue和LinkedBlockingQueue不同：
               > 锁上面：ArrayBlockingQueue只有一个锁，LinkedBlockingQueue用了两个锁，
                 实现上：ArrayBlockingQueue直接插入元素，LinkedBlockingQueue需要转换。
          - PriorityBlockingQueue：一个支持优先级排序的无界阻塞队列。（compareTo 排序实现优先）
               > 默认情况下，按照自然顺序，要么实现compareTo()方法，指定构造参数Comparator
          - DelayQueue：一个使用优先级队列实现的无界阻塞队列。（缓存失效、定时任务 ）
               > 支持延时获取的元素的阻塞队列，元素必须要实现Delayed接口。适用场景：实现自己的缓存系统，订单到期，限时支付等等。
          - SynchronousQueue：一个不存储元素的阻塞队列。（不存储数据、可用于传递数据）
               > 每一个put操作都要等待一个take操作
          - LinkedTransferQueue：一个由链表结构组成的无界阻塞队列。
               > transfer()，必须要消费者消费了以后方法才会返回，tryTransfer()无论消费者是否接收，方法都立即返回。
          - LinkedBlockingDeque：一个由链表结构组成的双向阻塞队列。
               > 可以从队列的头和尾都可以插入和移除元素，实现工作密取，方法名带了First对头部操作，带了last从尾部操作，另外：add=addLast;	remove=removeFirst;	take=takeFirst
       + CyclicBarrier 、CountDownLatch 、Semaphore 的 的用法
            - CountDownLatch（线程计数器 ）
                > CountDownLatch类位于java.util.concurrent 包下，利用它可以实现类似计数器的功能。比如有
                  一个任务 A，它要等待其他 4 个任务执行完毕之后才能执行，此时就可以利用 CountDownLatch
                  来实现这种功能了
            - CyclicBarrier（回环栅栏-等待至 barrier 状态再全部同时执行）
                 ````
                 字面意思回环栅栏，通过它可以实现让一组线程等待至某个状态之后再全部同时执行。叫做回环
                  是因为当所有等待线程都被释放以后，CyclicBarrier 可以被重用。我们暂且把这个状态就叫做
                  barrier，当调用 await()方法之后，线程就处于 barrier 了。
                  CyclicBarrier 中最重要的方法就是 await 方法，它有 2 个重载版本：
                  1.  public int await()：用来挂起当前线程，直至所有线程都到达 barrier 状态再同时执行后续任
                  务；
                  2.  public int await(long timeout, TimeUnit unit)：让这些线程等待至一定的时间，如果还有
                  线程没有到达 barrier 状态就直接让到达 barrier 的线程执行后续任务
                 ````
            - CountDownLatch和CyclicBarrier辨析
                ````
                 1、countdownlatch放行由第三者控制，CyclicBarrier放行由一组线程本身控制
                 2、countdownlatch放行条件》=线程数，CyclicBarrier放行条件=线程数
                 3、countdownlatch不可重用，CyclicBarrier可重用
            - Semaphore（信号量-控制同时访问的线程个数）
                ````
                Semaphore 翻译成字面意思为 信号量，Semaphore 可以控制同时访问的线程个数，通过
                acquire() 获取一个许可，如果没有就等待，而 release() 释放一个许可。
                ````
               - Semaphore 类中比较重要的几个方法：
                    1.  public void acquire(): 用来获取一个许可，若无许可能够获得，则会一直等待，直到获得许
                    可。
                    2.  public void acquire(int permits):获取 permits 个许可
                    3.  public void release() { } :释放许可。注意，在释放许可之前，必须先获获得许可。
                    4.  public void release(int permits) { }:释放 permits 个许可
               - 上面 4 个方法都会被阻塞，如果想立即得到执行结果，可以使用下面几个方法
                    1.  public boolean tryAcquire():尝试获取一个许可，若获取成功，则立即返回 true，若获取失
                    败，则立即返回 false
                    2.  public boolean tryAcquire(long timeout, TimeUnit unit):尝试获取一个许可，若在指定的
                    时间内获取成功，则立即返回 true，否则则立即返回 false
                    3.  public boolean tryAcquire(int permits):尝试获取 permits 个许可，若获取成功，则立即返
                    回 true，若获取失败，则立即返回 false
                    4.  public boolean tryAcquire(int permits, long timeout, TimeUnit unit): 尝试获取 permits
                    个许可，若在指定的时间内获取成功，则立即返回 true，否则则立即返回 false
                    5.  还可以通过 availablePermits()方法得到可用的许可数目。
                    例子：若一个工厂有5 台机器，但是有8个工人，一台机器同时只能被一个工人使用，只有使用完
                    了，其他工人才能继续使用。那么我们就可以通过 Semaphore 来实现
            - volatile 关键字的作用 关键字的作用（变量可见性、禁止重排序）
                
                + 变量可见性
                    ````
                    其一是保证该变量对所有线程可见，这里的可见性指的是当一个线程修改了变量的值，那么新的
                    值对于其他线程是可以立即获取的
                    ````
                + 禁止重排序
                    ````
                  volatile 禁止了指令重排。
                  比 sychronized 更轻量级的同步锁
                  在访问 volatile 变量时不会执行加锁操作，因此也就不会使执行线程阻塞，因此 volatile 变量是一
                  种比 sychronized 关键字更轻量级的同步机制。volatile 适合这种场景：一个变量被多个线程共
                  享，线程直接给这个变量赋值。
                  当对非 volatile 变量进行读写的时候，每个线程先从内存拷贝变量到 CPU 缓存中。如果计算机有
                  多个 CPU，每个线程可能在不同的 CPU 上被处理，这意味着每个线程可以拷贝到不同的 CPU
                  cache 中。而声明变量是 volatile 的，JVM 保证了每次读变量都从内存中读，跳过 CPU cache
                  这一步。
                    ````
                + 适用场景
                    ````
                  值得说明的是对 volatile 变量的单次读/写操作可以保证原子性的，如 long 和 double 类型变量，
                  但是并不能保证 i++这种操作的原子性，因为本质上 i++是读、写两次操作。在某些场景下可以
                  代替 Synchronized。但是,volatile 的不能完全取代 Synchronized 的位置，只有在一些特殊的场
                  景下，才能适用 volatile。总的来说，必须同时满足下面两个条件才能保证在并发环境的线程安
                  全：
                  （1）对变量的写操作不依赖于当前值（比如 i++），或者说是单纯的变量赋值（boolean
                  flag = true）。
                  （2）该变量没有包含在具有其他变量的不变式中，也就是说，不同的 volatile 变量之间，不
                  能互相依赖。只有在状态真正独立于程序内其他内容时才能使用 volatile
                  ps : concorrenthashmap 也有这个问题  读写两次操作 不保持原子性
                    ````
       + 如何在两个线程之间共享数据
            ```` 
            Java 里面进行多线程通信的主要方式就是共享内存的方式，共享内存主要的关注点有两个：可见
            性和有序性原子性。Java 内存模型（JMM）解决了可见性和有序性的问题，而锁解决了原子性的
            问题，理想情况下我们希望做到“同步”和“互斥”。有以下常规实现方法：
            将数据抽象成一个类 ，并将数据的操作作为这个类的方法
            
            ````
            1.  将数据抽象成一个类，并将对这个数据的操作作为这个类的方法，这么设计可以和容易做到
                        同步，只要在方法上加 synchronized
            2.  将 Runnable 对象作为一个类的内部类，共享数据作为这个类的成员变量，每个线程对共享数
             据的操作方法也封装在外部类，以便实现对数据的各个操作的同步和互斥，作为内部类的各
             个 Runnable 对象调用外部类的这些方法
       + ThreadLocal 作用（ 线程本地存储 ）
            ````
            ThreadLocal，很多地方叫做线程本地变量，也有些地方叫做线程本地存储，ThreadLocal 的作用
            是提供线程内的局部变量，这种变量在线程的生命周期内起作用，减少同一个线程内多个函数或
            者组件之间一些公共变量的传递的复杂度
            ````
            - ThreadLocalMap （线程的一个属性）
                1.  每个线程中都有一个自己的 ThreadLocalMap 类对象，可以将线程自己的对象保持到其中，
                各管各的，线程可以正确的访问到自己的对象。
                2.  将一个共用的 ThreadLocal 静态实例作为 key，将不同对象的引用保存到不同线程的
                ThreadLocalMap 中，然后在线程执行的各处通过这个静态 ThreadLocal 实例的 get()方法取
                得自己线程保存的那个对象，避免了将这个对象作为参数传递的麻烦。
                3.  ThreadLocalMap 其实就是线程里面的一个属性，它在 Thread 类中定义
                ThreadLocal.ThreadLocalMap threadLocals = null;
                + 使用场景
                    > 最常见的 ThreadLocal 使用场景为 用来解决 数据库连接、Session 管理等
              
       + ConcurrentHashMap （详细我准备 写在合集）
            - 减小锁粒度
                ````
                减小锁粒度是指缩小锁定对象的范围，从而减小锁冲突的可能性，从而提高系统的并发能力。减
                小锁粒度是一种削弱多线程锁竞争的有效手段，这种技术典型的应用是 ConcurrentHashMap(高
                性能的 HashMap)类的实现。对于 HashMap 而言，最重要的两个方法是 get 与 set 方法，如果我
                们对整个 HashMap 加锁，可以得到线程安全的对象，但是加锁粒度太大。Segment 的大小也被
                称为 ConcurrentHashMap 的并发度
            - ConcurrentHashMap 分段锁
                ````
                ConcurrentHashMap，它内部细分了若干个小的 HashMap，称之为段(Segment)。默认情况下
                一个 ConcurrentHashMap 被进一步细分为 16 个段，既就是锁的并发度。
                如果需要在 ConcurrentHashMap 中添加一个新的表项，并不是将整个 HashMap 加锁，而是首
                先根据hashcode得到该表项应该存放在哪个段中，然后对该段加锁，并完成put操作。在多线程
                环境中，如果多个线程同时进行put操作，只要被加入的表项不存放在同一个段中，则线程间可以
                做到真正的并行。
               ConcurrentHashMap 是由 Segment 数组结构和 HashEntry 数组结构组成
                ConcurrentHashMap 是由 Segment 数组结构和 HashEntry 数组结构组成。Segment 是一种可
                重入锁 ReentrantLock，在 ConcurrentHashMap 里扮演锁的角色，HashEntry 则用于存储键值
                对数据。一个 ConcurrentHashMap 里包含一个 Segment 数组，Segment 的结构和 HashMap
                类似，是一种数组和链表结构， 一个 Segment 里包含一个 HashEntry 数组，每个 HashEntry 是
                一个链表结构的元素， 每个 Segment 守护一个 HashEntry 数组里的元素,当对 HashEntry 数组的
                数据进行修改时，必须首先获得它对应的 Segment 锁  