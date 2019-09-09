<blockquote>
<p>作者：彤哥</p>
<p>出处：<a href="https://www.cnblogs.com/tong-yuan/">https://www.cnblogs.com/tong-yuan/</a></p>
</blockquote>
<hr />
<h2><span id="i">问题</span></h2>
<p>（1）PriorityBlockingQueue的实现方式？</p>
<p>（2）PriorityBlockingQueue是否需要扩容？</p>
<p>（3）PriorityBlockingQueue是怎么控制并发安全的？</p>
<h2><span id="i-2">简介</span></h2>
<p>PriorityBlockingQueue是java并发包下的优先级阻塞队列，它是线程安全的，如果让你来实现你会怎么实现它呢？</p>
<p>还记得我们前面介绍过的PriorityQueue吗？点击链接直达【<a href="https://mp.weixin.qq.com/s/kGKS7WXWbf-ME1_Hr3Fpgw">死磕 java集合之PriorityQueue源码分析</a>】</p>
<p>还记得优先级队列一般使用什么来实现吗？点击链接直达【<a href="https://mp.weixin.qq.com/s/AF2tMHfofG8b51yIyaIReg">拜托，面试别再问我堆（排序）了！</a>】</p>
<h2><span id="i-3">源码分析</span></h2>
<h3><span id="i-4">主要属性</span></h3>
<pre><code class="java">// 默认容量为11
private static final int DEFAULT_INITIAL_CAPACITY = 11;
// 最大数组大小
private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
// 存储元素的地方
private transient Object[] queue;
// 元素个数
private transient int size;
// 比较器
private transient Comparator&lt;? super E&gt; comparator;
// 重入锁
private final ReentrantLock lock;
// 非空条件
private final Condition notEmpty;
// 扩容的时候使用的控制变量，CAS更新这个值，谁更新成功了谁扩容，其它线程让出CPU
private transient volatile int allocationSpinLock;
// 不阻塞的优先级队列，非存储元素的地方，仅用于序列化/反序列化时
private PriorityQueue&lt;E&gt; q;
</code></pre>
<p>（1）依然是使用一个数组来使用元素；</p>
<p>（2）使用一个锁加一个notEmpty条件来保证并发安全；</p>
<p>（3）使用一个变量的CAS操作来控制扩容；</p>
<p>为啥没有notFull条件呢？</p>
<h3><span id="i-5">主要构造方法</span></h3>
<pre><code class="java">// 默认容量为11
public PriorityBlockingQueue() {
    this(DEFAULT_INITIAL_CAPACITY, null);
}
// 传入初始容量
public PriorityBlockingQueue(int initialCapacity) {
    this(initialCapacity, null);
}
// 传入初始容量和比较器
// 初始化各变量
public PriorityBlockingQueue(int initialCapacity,
                             Comparator&lt;? super E&gt; comparator) {
    if (initialCapacity &lt; 1)
        throw new IllegalArgumentException();
    this.lock = new ReentrantLock();
    this.notEmpty = lock.newCondition();
    this.comparator = comparator;
    this.queue = new Object[initialCapacity];
}
</code></pre>
<h3><span id="i-6">入队</span></h3>
<p>每个阻塞队列都有四个方法，我们这里只分析一个offer(E e)方法：</p>
<pre><code class="java"><br />public boolean offer(E e) {
    // 元素不能为空
    if (e == null)
        throw new NullPointerException();
    final ReentrantLock lock = this.lock;
    // 加锁
    lock.lock();
    int n, cap;
    Object[] array;
    // 判断是否需要扩容，即元素个数达到了数组容量
    while ((n = size) &gt;= (cap = (array = queue).length))
        tryGrow(array, cap);
    try {
        Comparator&lt;? super E&gt; cmp = comparator;
        // 根据是否有比较器选择不同的方法
        if (cmp == null)
            siftUpComparable(n, e, array);
        else
            siftUpUsingComparator(n, e, array, cmp);
        // 插入元素完毕，元素个数加1            
        size = n + 1;
        // 唤醒notEmpty条件
        notEmpty.signal();
    } finally {
        // 解锁
        lock.unlock();
    }
    return true;
}
private static &lt;T&gt; void siftUpComparable(int k, T x, Object[] array) {
    Comparable&lt;? super T&gt; key = (Comparable&lt;? super T&gt;) x;
    while (k &gt; 0) {
        // 取父节点
        int parent = (k - 1) &gt;&gt;&gt; 1;
        // 父节点的元素值
        Object e = array[parent];
        // 如果key大于父节点，堆化结束
        if (key.compareTo((T) e) &gt;= 0)
            break;
        // 否则，交换二者的位置，继续下一轮比较
        array[k] = e;
        k = parent;
    }
    // 找到了应该放的位置，放入元素
    array[k] = key;
}
</code></pre>
<p>入队的整个操作跟PriorityQueue几乎一致：</p>
<p>（1）加锁；</p>
<p>（2）判断是否需要扩容；</p>
<p>（3）添加元素并做自下而上的堆化；</p>
<p>（4）元素个数加1并唤醒notEmpty条件，唤醒取元素的线程；</p>
<p>（5）解锁；</p>
<h3><span id="i-7">扩容</span></h3>
<pre><code class="java">private void tryGrow(Object[] array, int oldCap) {
    // 先释放锁，因为是从offer()方法的锁内部过来的
    // 这里先释放锁，使用allocationSpinLock变量控制扩容的过程
    // 防止阻塞的线程过多
    lock.unlock(); // must release and then re-acquire main lock
    Object[] newArray = null;
    // CAS更新allocationSpinLock变量为1的线程获得扩容资格
    if (allocationSpinLock == 0 &amp;&amp;
        UNSAFE.compareAndSwapInt(this, allocationSpinLockOffset,
                                 0, 1)) {
        try {
            // 旧容量小于64则翻倍，旧容量大于64则增加一半
            int newCap = oldCap + ((oldCap &lt; 64) ?
                                   (oldCap + 2) : // grow faster if small
                                   (oldCap &gt;&gt; 1));
            // 判断新容量是否溢出
            if (newCap - MAX_ARRAY_SIZE &gt; 0) {    // possible overflow
                int minCap = oldCap + 1;
                if (minCap &lt; 0 || minCap &gt; MAX_ARRAY_SIZE)
                    throw new OutOfMemoryError();
                newCap = MAX_ARRAY_SIZE;
            }
            // 创建新数组
            if (newCap &gt; oldCap &amp;&amp; queue == array)
                newArray = new Object[newCap];
        } finally {
            // 相当于解锁
            allocationSpinLock = 0;
        }
    }
    // 只有进入了上面条件的才会满足这个条件
    // 意思是让其它线程让出CPU
    if (newArray == null) // back off if another thread is allocating
        Thread.yield();
    // 再次加锁
    lock.lock();
    // 判断新数组创建成功并且旧数组没有被替换过
    if (newArray != null &amp;&amp; queue == array) {
        // 队列赋值为新数组
        queue = newArray;
        // 并拷贝旧数组元素到新数组中
        System.arraycopy(array, 0, newArray, 0, oldCap);
    }
}
</code></pre>
<p>（1）解锁，解除offer()方法中加的锁；</p>
<p>（2）使用allocationSpinLock变量的CAS操作来控制扩容的过程；</p>
<p>（3）旧容量小于64则翻倍，旧容量大于64则增加一半；</p>
<p>（4）创建新数组；</p>
<p>（5）修改allocationSpinLock为0，相当于解锁；</p>
<p>（6）其它线程在扩容的过程中要让出CPU；</p>
<p>（7）再次加锁；</p>
<p>（8）新数组创建成功，把旧数组元素拷贝过来，并返回到offer()方法中继续添加元素操作；</p>
<h3><span id="i-8">出队</span></h3>
<p>阻塞队列的出队方法也有四个，我们这里只分析一个take()方法：</p>
<pre><code class="java">public E take() throws InterruptedException {
    final ReentrantLock lock = this.lock;
    // 加锁
    lock.lockInterruptibly();
    E result;
    try {
        // 队列没有元素，就阻塞在notEmpty条件上
        // 出队成功，就跳出这个循环
        while ( (result = dequeue()) == null)
            notEmpty.await();
    } finally {
        // 解锁
        lock.unlock();
    }
    // 返回出队的元素
    return result;
}
private E dequeue() {
    // 元素个数减1
    int n = size - 1;
    if (n &lt; 0)
        // 数组元素不足，返回null
        return null;
    else {
        Object[] array = queue;
        // 弹出堆顶元素
        E result = (E) array[0];
        // 把堆尾元素拿到堆顶
        E x = (E) array[n];
        array[n] = null;
        Comparator&lt;? super E&gt; cmp = comparator;
        // 并做自上而下的堆化
        if (cmp == null)
            siftDownComparable(0, x, array, n);
        else
            siftDownUsingComparator(0, x, array, n, cmp);
        // 修改size
        size = n;
        // 返回出队的元素
        return result;
    }
}
private static &lt;T&gt; void siftDownComparable(int k, T x, Object[] array,
                                           int n) {
    if (n &gt; 0) {
        Comparable&lt;? super T&gt; key = (Comparable&lt;? super T&gt;)x;
        int half = n &gt;&gt;&gt; 1;           // loop while a non-leaf
        // 只需要遍历到叶子节点就够了
        while (k &lt; half) {
            // 左子节点
            int child = (k &lt;&lt; 1) + 1; // assume left child is least
            // 左子节点的值
            Object c = array[child];
            // 右子节点
            int right = child + 1;
            // 取左右子节点中最小的值
            if (right &lt; n &amp;&amp;
                ((Comparable&lt;? super T&gt;) c).compareTo((T) array[right]) &gt; 0)
                c = array[child = right];
            // key如果比左右子节点都小，则堆化结束
            if (key.compareTo((T) c) &lt;= 0)
                break;
            // 否则，交换key与左右子节点中最小的节点的位置
            array[k] = c;
            k = child;
        }
        // 找到了放元素的位置，放置元素
        array[k] = key;
    }
}
</code></pre>
<p>出队的过程与PriorityQueue基本类似：</p>
<p>（1）加锁；</p>
<p>（2）判断是否出队成功，未成功就阻塞在notEmpty条件上；</p>
<p>（3）出队时弹出堆顶元素，并把堆尾元素拿到堆顶；</p>
<p>（4）再做自上而下的堆化；</p>
<p>（5）解锁；</p>
<h2><span id="i-9">总结</span></h2>
<p>（1）PriorityBlockingQueue整个入队出队的过程与PriorityQueue基本是保持一致的；</p>
<p>（2）PriorityBlockingQueue使用一个锁+一个notEmpty条件控制并发安全；</p>
<p>（3）PriorityBlockingQueue扩容时使用一个单独变量的CAS操作来控制只有一个线程进行扩容；</p>
<p>（4）入队使用自下而上的堆化；</p>
<p>（5）出队使用自上而下的堆化；</p>
<h2><span id="i-10">彩蛋</span></h2>
<p>为什么PriorityBlockingQueue不需要notFull条件？</p>
<p>因为PriorityBlockingQueue在入队的时候如果没有空间了是会自动扩容的，也就不存在队列满了的状态，也就是不需要等待通知队列不满了可以放元素了，所以也就不需要notFull条件了。</p>
		</article>