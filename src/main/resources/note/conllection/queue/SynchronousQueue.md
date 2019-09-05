<blockquote>
<p>作者：彤哥</p>
<p>出处：<a href="https://www.cnblogs.com/tong-yuan/">https://www.cnblogs.com/tong-yuan/</a></p>
</blockquote>
<hr />
<p>笔记由博客+个人一些补充 构成
<h2><span id="i">问题</span></h2>
<p>（1）SynchronousQueue的实现方式？</p>
<p>（2）SynchronousQueue真的是无缓冲的吗？</p>
<p>（3）SynchronousQueue在高并发情景下会有什么问题？</p>
<h2><span id="i-2">简介</span></h2>
<p>SynchronousQueue是java并发包下无缓冲阻塞队列，它用来在两个线程之间移交元素，但是它有个很大的问题，你知道是什么吗？请看下面的分析。</p>
<h2><span id="i-3">源码分析</span></h2>
<h3><span id="i-4">主要属性</span></h3>
<pre><code class="java">// CPU的数量
static final int NCPUS = Runtime.getRuntime().availableProcessors();
// 有超时的情况自旋多少次，当CPU数量小于2的时候不自旋
static final int maxTimedSpins = (NCPUS &lt; 2) ? 0 : 32;
// 没有超时的情况自旋多少次
static final int maxUntimedSpins = maxTimedSpins * 16;
// 针对有超时的情况，自旋了多少次后，如果剩余时间大于1000纳秒就使用带时间的LockSupport.parkNanos()这个方法
static final long spinForTimeoutThreshold = 1000L;
// 传输器，即两个线程交换元素使用的东西
private transient volatile Transferer&lt;E&gt; transferer;
</code></pre>
<p>通过属性我们可以Get到两个点：</p>
<p>（1）这个阻塞队列里面是会自旋的；</p>
<p>（2）它使用了一个叫做transferer的东西来交换元素；</p>
<h3><span id="i-5">主要内部类</span></h3>
<pre><code class="java">// Transferer抽象类，主要定义了一个transfer方法用来传输元素
abstract static class Transferer&lt;E&gt; {
    abstract E transfer(E e, boolean timed, long nanos);
}
// 以栈方式实现的Transferer
static final class TransferStack&lt;E&gt; extends Transferer&lt;E&gt; {
    // 栈中节点的几种类型：
    // 1. 消费者（请求数据的）
    static final int REQUEST    = 0;
    // 2. 生产者（提供数据的）
    static final int DATA       = 1;
    // 3. 二者正在撮合中
    static final int FULFILLING = 2;
    // 栈中的节点
    static final class SNode {
        // 下一个节点
        volatile SNode next;        // next node in stack
        // 匹配者
        volatile SNode match;       // the node matched to this
        // 等待着的线程
        volatile Thread waiter;     // to control park/unpark
        // 元素
        Object item;                // data; or null for REQUESTs
        // 模式，也就是节点的类型，是消费者，是生产者，还是正在撮合中
        int mode;
    }
    // 栈的头节点
    volatile SNode head;
}
// 以队列方式实现的Transferer
static final class TransferQueue&lt;E&gt; extends Transferer&lt;E&gt; {
    // 队列中的节点
    static final class QNode {
        // 下一个节点
        volatile QNode next;          // next node in queue
        // 存储的元素
        volatile Object item;         // CAS'ed to or from null
        // 等待着的线程
        volatile Thread waiter;       // to control park/unpark
        // 是否是数据节点
        final boolean isData;
    }
    // 队列的头节点
    transient volatile QNode head;
    // 队列的尾节点
    transient volatile QNode tail;
}
</code></pre>
<p>（1）定义了一个抽象类Transferer，里面定义了一个传输元素的方法；</p>
<p>（2）有两种传输元素的方法，一种是栈，一种是队列；</p>
<p>（3）栈的特点是后进先出，队列的特点是先进行出；</p>
<p>（4）栈只需要保存一个头节点就可以了，因为存取元素都是操作头节点；</p>
<p>（5）队列需要保存一个头节点一个尾节点，因为存元素操作尾节点，取元素操作头节点；</p>
<p>（6）每个节点中保存着存储的元素、等待着的线程，以及下一个节点；</p>
<p>（7）栈和队列两种方式有什么不同呢？请看下面的分析。</p>
<h3><span id="i-6">主要构造方法</span></h3>
<pre><code class="java">public SynchronousQueue() {
    // 默认非公平模式
    this(false);
}
public SynchronousQueue(boolean fair) {
    // 如果是公平模式就使用队列，如果是非公平模式就使用栈
    transferer = fair ? new TransferQueue&lt;E&gt;() : new TransferStack&lt;E&gt;();
}
</code></pre>
<p>（1）默认使用非公平模式，也就是栈结构；</p>
<p>（2）公平模式使用队列，非公平模式使用栈；</p>
<h3><span id="i-7">入队</span></h3>
<p>我们这里主要介绍以栈方式实现的传输模式，以put(E e)方法为例。</p>
<pre><code class="java">public void put(E e) throws InterruptedException {
    // 元素不可为空
    if (e == null) throw new NullPointerException();
    // 直接调用传输器的transfer()方法
    // 三个参数分别是：传输的元素，是否需要超时，超时的时间
    if (transferer.transfer(e, false, 0) == null) {
        // 如果传输失败，直接让线程中断并抛出中断异常
        Thread.interrupted();
        throw new InterruptedException();
    }
}
</code></pre>
<p>调用transferer的transfer()方法，传入元素e，说明是生产者</p>
<h3><span id="i-8">出队</span></h3>
<p>我们这里主要介绍以栈方式实现的传输模式，以take()方法为例。</p>
<pre><code class="java">public E take() throws InterruptedException {
    // 直接调用传输器的transfer()方法
    // 三个参数分别是：null，是否需要超时，超时的时间
    // 第一个参数为null表示是消费者，要取元素
    E e = transferer.transfer(null, false, 0);
    // 如果取到了元素就返回
    if (e != null)
        return e;
    // 否则让线程中断并抛出中断异常
    Thread.interrupted();
    throw new InterruptedException();
}
</code></pre>
<p>调用transferer的transfer()方法，传入null，说明是消费者。</p>
<h3><span id="transfer">transfer()方法</span></h3>
<p>transfer()方法同时实现了取元素和放元素的功能，下面我再来看看这个transfer()方法里究竟干了什么。</p>
<pre><code class="java">// TransferStack.transfer()方法
E transfer(E e, boolean timed, long nanos) {
    SNode s = null; // constructed/reused as needed
    // 根据e是否为null决定是生产者还是消费者
    int mode = (e == null) ? REQUEST : DATA;
    // 自旋+CAS，熟悉的套路，熟悉的味道
    for (;;) {
        // 栈顶元素
        SNode h = head;
        // 栈顶没有元素，或者栈顶元素跟当前元素是一个模式的
        // 也就是都是生产者节点或者都是消费者节点
        if (h == null || h.mode == mode) {  // empty or same-mode
            // 如果有超时而且已到期
            if (timed &amp;&amp; nanos &lt;= 0) {      // can't wait
                // 如果头节点不为空且是取消状态
                if (h != null &amp;&amp; h.isCancelled())
                    // 就把头节点弹出，并进入下一次循环
                    casHead(h, h.next);     // pop cancelled node
                else
                    // 否则，直接返回null（超时返回null）
                    return null;
            } else if (casHead(h, s = snode(s, e, h, mode))) {
                // 入栈成功（因为是模式相同的，所以只能入栈）
                // 调用awaitFulfill()方法自旋+阻塞当前入栈的线程并等待被匹配到
                SNode m = awaitFulfill(s, timed, nanos);
                // 如果m等于s，说明取消了，那么就把它清除掉，并返回null
                if (m == s) {               // wait was cancelled
                    clean(s);
                    // 被取消了返回null
                    return null;
                }
                // 到这里说明匹配到元素了
                // 因为从awaitFulfill()里面出来要不被取消了要不就匹配到了
                // 如果头节点不为空，并且头节点的下一个节点是s
                // 就把头节点换成s的下一个节点
                // 也就是把h和s都弹出了
                // 也就是把栈顶两个元素都弹出了
                if ((h = head) != null &amp;&amp; h.next == s)
                    casHead(h, s.next);     // help s's fulfiller
                // 根据当前节点的模式判断返回m还是s中的值
                return (E) ((mode == REQUEST) ? m.item : s.item);
            }
        } else if (!isFulfilling(h.mode)) { // try to fulfill
            // 到这里说明头节点和当前节点模式不一样
            // 如果头节点不是正在撮合中
            // 如果头节点已经取消了，就把它弹出栈
            if (h.isCancelled())            // already cancelled
                casHead(h, h.next);         // pop and retry
            else if (casHead(h, s=snode(s, e, h, FULFILLING|mode))) {
                // 头节点没有在撮合中，就让当前节点先入队，再让他们尝试匹配
                // 且s成为了新的头节点，它的状态是正在撮合中
                for (;;) { // loop until matched or waiters disappear
                    SNode m = s.next;       // m is s's match
                    // 如果m为null，说明除了s节点外的节点都被其它线程先一步撮合掉了
                    // 就清空栈并跳出内部循环，到外部循环再重新入栈判断
                    if (m == null) {        // all waiters are gone
                        casHead(s, null);   // pop fulfill node
                        s = null;           // use new node next time
                        break;              // restart main loop
                    }
                    SNode mn = m.next;
                    // 如果m和s尝试撮合成功，就弹出栈顶的两个元素m和s
                    if (m.tryMatch(s)) {
                        casHead(s, mn);     // pop both s and m
                        // 返回撮合结果
                        return (E) ((mode == REQUEST) ? m.item : s.item);
                    } else                  // lost match
                        // 尝试撮合失败，说明m已经先一步被其它线程撮合了
                        // 就协助清除它
                        s.casNext(m, mn);   // help unlink
                }
            }
        } else {                            // help a fulfiller
            // 到这里说明当前节点和头节点模式不一样
            // 且头节点是正在撮合中
            SNode m = h.next;               // m is h's match
            if (m == null)                  // waiter is gone
                // 如果m为null，说明m已经被其它线程先一步撮合了
                casHead(h, null);           // pop fulfilling node
            else {
                SNode mn = m.next;
                // 协助匹配，如果m和s尝试撮合成功，就弹出栈顶的两个元素m和s
                if (m.tryMatch(h))          // help match
                    // 将栈顶的两个元素弹出后，再让s重新入栈
                    casHead(h, mn);         // pop both h and m
                else                        // lost match
                    // 尝试撮合失败，说明m已经先一步被其它线程撮合了
                    // 就协助清除它
                    h.casNext(m, mn);       // help unlink
            }
        }
    }
}
// 三个参数：需要等待的节点，是否需要超时，超时时间
SNode awaitFulfill(SNode s, boolean timed, long nanos) {
    // 到期时间
    final long deadline = timed ? System.nanoTime() + nanos : 0L;
    // 当前线程
    Thread w = Thread.currentThread();
    // 自旋次数
    int spins = (shouldSpin(s) ?
                 (timed ? maxTimedSpins : maxUntimedSpins) : 0);
    for (;;) {
        // 当前线程中断了，尝试清除s
        if (w.isInterrupted())
            s.tryCancel();
        // 检查s是否匹配到了元素m（有可能是其它线程的m匹配到当前线程的s）
        SNode m = s.match;
        // 如果匹配到了，直接返回m
        if (m != null)
            return m;
        // 如果需要超时
        if (timed) {
            // 检查超时时间如果小于0了，尝试清除s
            nanos = deadline - System.nanoTime();
            if (nanos &lt;= 0L) {
                s.tryCancel();
                continue;
            }
        }
        if (spins &gt; 0)
            // 如果还有自旋次数，自旋次数减一，并进入下一次自旋
            spins = shouldSpin(s) ? (spins-1) : 0;
        // 后面的elseif都是自旋次数没有了
        else if (s.waiter == null)
            // 如果s的waiter为null，把当前线程注入进去，并进入下一次自旋
            s.waiter = w; // establish waiter so can park next iter
        else if (!timed)
            // 如果不允许超时，直接阻塞，并等待被其它线程唤醒，唤醒后继续自旋并查看是否匹配到了元素
            LockSupport.park(this);
        else if (nanos &gt; spinForTimeoutThreshold)
            // 如果允许超时且还有剩余时间，就阻塞相应时间
            LockSupport.parkNanos(this, nanos);
    }
}
    // SNode里面的方向，调用者m是s的下一个节点
    // 这时候m节点的线程应该是阻塞状态的
    boolean tryMatch(SNode s) {
        // 如果m还没有匹配者，就把s作为它的匹配者
        if (match == null &amp;&amp;
            UNSAFE.compareAndSwapObject(this, matchOffset, null, s)) {
            Thread w = waiter;
            if (w != null) {    // waiters need at most one unpark
                waiter = null;
                // 唤醒m中的线程，两者匹配完毕
                LockSupport.unpark(w);
            }
            // 匹配到了返回true
            return true;
        }
        // 可能其它线程先一步匹配了m，返回其是否是s
        return match == s;
    }
</code></pre>
<p>整个逻辑比较复杂，这里为了简单起见，屏蔽掉多线程处理的细节，只描述正常业务场景下的逻辑：</p>
<p>（1）如果栈中没有元素，或者栈顶元素跟将要入栈的元素模式一样，就入栈；</p>
<p>（2）入栈后自旋等待一会看有没有其它线程匹配到它，自旋完了还没匹配到元素就阻塞等待；</p>
<p>（3）阻塞等待被唤醒了说明其它线程匹配到了当前的元素，就返回匹配到的元素；</p>
<p>（4）如果两者模式不一样，且头节点没有在匹配中，就拿当前节点跟它匹配，匹配成功了就返回匹配到的元素；</p>
<p>（5）如果两者模式不一样，且头节点正在匹配中，当前线程就协助去匹配，匹配完成了再让当前节点重新入栈重新匹配；</p>
<p>如果直接阅读这部分代码还是比较困难的，建议写个测试用例，打个断点一步一步跟踪调试。</p>
<p>下面是我的测试用例，可以参考下，在IDEA中可以让断点只阻塞线程:</p>
<pre><code class="java">public class TestSynchronousQueue {
    public static void main(String[] args) throws InterruptedException {
        SynchronousQueue&lt;Integer&gt; queue = new SynchronousQueue&lt;&gt;(false);
        new Thread(()-&gt;{
            try {
                queue.put(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(500);
        System.out.println(queue.take());
    }
}
</code></pre>
<p>修改断点只阻塞线程的方法，右击断点，选择Thread：</p>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/synchronous1.png" alt="thread" /></p>
<h2><span id="i-9">交给你了</span></h2>
<p>上面的源码分析都是基于Stack的方式来分析的，那么队列是怎么动作的呢？很简单哦，测试用例中的false改成true就可以了，这就交给你了。</p>
<h2><span id="i-10">总结</span></h2>
<p>（1）SynchronousQueue是java里的无缓冲队列，用于在两个线程之间直接移交元素；</p>
<p>（2）SynchronousQueue有两种实现方式，一种是公平（队列）方式，一种是非公平（栈）方式；</p>
<p>（3）栈方式中的节点有三种模式：生产者、消费者、正在匹配中；</p>
<p>（4）栈方式的大致思路是如果栈顶元素跟自己一样的模式就入栈并等待被匹配，否则就匹配，匹配到了就返回；</p>
<p>（5）队列方式的大致思路是……不告诉你^^（两者的逻辑差别还是挺大的）</p>
<h2><span id="i-11">彩蛋</span></h2>
<p>（1）SynchronousQueue真的是无缓冲的队列吗？</p>
<p>通过源码分析，我们可以发现其实SynchronousQueue内部或者使用栈或者使用队列来存储包含线程和元素值的节点，如果同一个模式的节点过多的话，它们都会存储进来，且都会阻塞着，所以，严格上来说，SynchronousQueue并不能算是一个无缓冲队列。</p>
<p>（2）SynchronousQueue有什么缺点呢？</p>
<p>试想一下，如果有多个生产者，但只有一个消费者，如果消费者处理不过来，是不是生产者都会阻塞起来？反之亦然。</p>
<p>这是一件很危险的事，所以，SynchronousQueue一般用于生产、消费的速度大致相当的情况，这样才不会导致系统中过多的线程处于阻塞状态。</p>
		</article>
		
<h2>补充同步队列 非公平即队列实现方式</h2>
<h3>SynchronousQueue 基本属性</h3>
<pre><code class="java">
/** CPU数量 */
static final int NCPUS = Runtime.getRuntime().availableProcessors();
/**
 * 自旋次数，如果transfer指定了timeout时间，则使用maxTimeSpins,如果CPU数量小于2则自旋次数为0，否则为32
 * 此值为经验值，不随CPU数量增加而变化，这里只是个常量。
 */
static final int maxTimedSpins = (NCPUS < 2) ? 0 : 32;
/**
 * 自旋次数，如果没有指定时间设置，则使用maxUntimedSpins。如果NCPUS数量大于等于2则设定为为32*16，否则为0；
 */
static final int maxUntimedSpins = maxTimedSpins * 16;
/**
 * The number of nanoseconds for which it is faster to spin
 * rather than to use timed park. A rough estimate suffices.
 */
static final long spinForTimeoutThreshold = 1000L;
</code></pre>
<h2 id="公平模式-transferqueue">公平模式-TransferQueue</h2>
<p>TransferQueue内部是如何进行工作的，这里先大致讲解下，队列采用了互补模式进行等待，QNode中有一个字段是isData，如果模式相同或空队列时进行等待操作，互补的情况下就进行消费操作。</p>
<p>入队操作相同模式<br />
<img src="https://img2018.cnblogs.com/blog/458325/201905/458325-20190511194700294-1968231289.png" /></p>
<p><img src="https://img2018.cnblogs.com/blog/458325/201905/458325-20190511194800943-86356229.png" /></p>
<p>不同模式时进行出队列操作：</p>
<p><img src="https://img2018.cnblogs.com/blog/458325/201905/458325-20190511194811557-2047294276.png" /></p>
<p>这时候来了一个isData=false的互补模式，队列就会变成如下状态：<br />
<img src="https://img2018.cnblogs.com/blog/458325/201905/458325-20190511194824692-1695599974.png" /></p>
<p>TransferQueue继承自Transferer抽象类，并且实现了transfer方法，它主要包含以下内容:</p>
<h3 id="qnode">QNode</h3>
<p>代表队列中的节点元素，它内部包含以下字段信息：</p>
<ol>
<li>字段信息描述</li>
</ol>
<table>
<thead>
<tr class="header">
<th>字段</th>
<th>描述</th>
<th>类型</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>next</td>
<td>下一个节点</td>
<td>QNode</td>
</tr>
<tr class="even">
<td>item</td>
<td>元素信息</td>
<td>Object</td>
</tr>
<tr class="odd">
<td>waiter</td>
<td>当前等待的线程</td>
<td>Thread</td>
</tr>
<tr class="even">
<td>isData</td>
<td>是否是数据</td>
<td>boolean</td>
</tr>
</tbody>
</table>
<ol>
<li>方法信息描述</li>
</ol>
<table>
<thead>
<tr class="header">
<th>方法</th>
<th>描述</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>casNext</td>
<td>替换当前节点的next节点</td>
</tr>
<tr class="even">
<td>casItem</td>
<td>替换当前节点的item数据</td>
</tr>
<tr class="odd">
<td>tryCancel</td>
<td>取消当前操作，将当前item赋值为this(当前QNode节点)</td>
</tr>
<tr class="even">
<td>isCancelled</td>
<td>如果item是this(当前QNode节点)的话就返回true，反之返回false</td>
</tr>
<tr class="odd">
<td>isOffList</td>
<td>如果已知此节点离队列，判断next节点是不是为this，则返回true，因为由于* advanceHead操作而忘记了其下一个指针。</td>
</tr>
</tbody>
</table>
<pre class="java"><code>E transfer(E e, boolean timed, long nanos) {
    /* Basic algorithm is to loop trying to take either of
     * two actions:
     *
     * 1. If queue apparently empty or holding same-mode nodes,
     *    try to add node to queue of waiters, wait to be
     *    fulfilled (or cancelled) and return matching item.
     *
     * 2. If queue apparently contains waiting items, and this
     *    call is of complementary mode, try to fulfill by CAS&#39;ing
     *    item field of waiting node and dequeuing it, and then
     *    returning matching item.
     *
     * In each case, along the way, check for and try to help
     * advance head and tail on behalf of other stalled/slow
     * threads.
     *
     * The loop starts off with a null check guarding against
     * seeing uninitialized head or tail values. This never
     * happens in current SynchronousQueue, but could if
     * callers held non-volatile/final ref to the
     * transferer. The check is here anyway because it places
     * null checks at top of loop, which is usually faster
     * than having them implicitly interspersed.
     */
    QNode s = null; // constructed/reused as needed
    // 分为两种状态1.有数据=true 2.无数据=false
    boolean isData = (e != null);
    // 循环内容
    for (;;) {
        // 尾部节点。
        QNode t = tail;
        // 头部节点。
        QNode h = head;
        // 判断头部和尾部如果有一个为null则自旋转。
        if (t == null || h == null)         // 还未进行初始化的值。
            continue;                       // 自旋
        // 头结点和尾节点相同或者尾节点的模式和当前节点模式相同。
        if (h == t || t.isData == isData) { // 空或同模式。
            // tn为尾节点的下一个节点信息。
            QNode tn = t.next;
            // 这里我认为是阅读不一致，原因是当前线程还没有阻塞的时候其他线程已经修改了尾节点tail会导致当前线程的tail节点不一致。
            if (t != tail)                  // inconsistent read
                continue;
            if (tn != null) {               // lagging tail
                advanceTail(t, tn);
                continue;
            }
            if (timed &amp;&amp; nanos &lt;= 0)        // 这里如果指定timed判断时间小于等于0直接返回。
                return null;
            // 判断新增节点是否为null,为null直接构建新节点。
            if (s == null)
                s = new QNode(e, isData);
            if (!t.casNext(null, s))        // 如果next节点不为null说明已经有其他线程进行tail操作
                continue;
            // 将t节点替换为s节点
            advanceTail(t, s);
            // 等待有消费者消费线程。
            Object x = awaitFulfill(s, e, timed, nanos);
            // 如果返回的x，指的是s.item,如果s.item指向自己的话清除操作。
            if (x == s) {
                clean(t, s);
                return null;
            }
            // 如果没有取消联系
            if (!s.isOffList()) {
                // 将当前节点替换头结点
                advanceHead(t, s);          // unlink if head
                if (x != null)              // 取消item值，这里是take方法时会进行item赋值为this
                    s.item = s;
                // 将等待线程设置为null
                s.waiter = null;
            }
            return (x != null) ? (E)x : e;
        } else {                            // complementary-mode
            // 获取头结点下一个节点
            QNode m = h.next;               // node to fulfill
            // 如果当前线程尾节点和全局尾节点不一致,重新开始
            // 头结点的next节点为空，代表无下一个节点，则重新开始，
            // 当前线程头结点和全局头结点不相等，则重新开始
            if (t != tail || m == null || h != head)
                continue;                   // inconsistent read
            Object x = m.item;
            if (isData == (x != null) ||    // m already fulfilled
                x == m ||                   // m cancelled
                !m.casItem(x, e)) {         // lost CAS
                advanceHead(h, m);          // dequeue and retry
                continue;
            }
            advanceHead(h, m);              // successfully fulfilled
            LockSupport.unpark(m.waiter);
            return (x != null) ? (E)x : e;
        }
    }
}</code></pre>
<p>我们来看一下awaitFulfill方法内容：</p>
<pre class="java"><code>Object awaitFulfill(QNode s, E e, boolean timed, long nanos) {
    // 如果指定了timed则为System.nanoTime() + nanos，反之为0。
    final long deadline = timed ? System.nanoTime() + nanos : 0L;
    // 获取当前线程。
    Thread w = Thread.currentThread();
    // 如果头节点下一个节点是当前s节点(以防止其他线程已经修改了head节点)
    // 则运算(timed ? maxTimedSpins : maxUntimedSpins)，否则直接返回。
    // 指定了timed则使用maxTimedSpins，反之使用maxUntimedSpins
    int spins = ((head.next == s) ?
                 (timed ? maxTimedSpins : maxUntimedSpins) : 0);
    // 自旋
    for (;;) {
        // 判断是否已经被中断。
        if (w.isInterrupted())
            //尝试取消，将当前节点的item修改为当前节点(this)。
            s.tryCancel(e);
        // 获取当前节点内容。
        Object x = s.item;
        // 判断当前值和节点值不相同是返回，因为弹出时会将item值赋值为null。
        if (x != e)
            return x;
        if (timed) {
            nanos = deadline - System.nanoTime();
            if (nanos &lt;= 0L) {
                s.tryCancel(e);
                continue;
            }
        }
        if (spins &gt; 0)![](https://img2018.cnblogs.com/blog/458325/201905/458325-20190511194850882-1013581623.png)
            --spins;
        else if (s.waiter == null)
            s.waiter = w;
        else if (!timed)
            LockSupport.park(this);
        else if (nanos &gt; spinForTimeoutThreshold)
            LockSupport.parkNanos(this, nanos);
    }
}</code></pre>
<ol>
<li>首先先判断有没有被中断，如果被中断则取消本次操作，将当前节点的item内容赋值为当前节点。</li>
<li>判断当前节点和节点值不相同是返回</li>
<li>将当前线程赋值给当前节点</li>
<li>自旋，如果指定了timed则使用<code>LockSupport.parkNanos(this, nanos);</code>，如果没有指定则使用<code>LockSupport.park(this);</code>。</li>
<li>中断相应是在下次才能被执行。</li>
</ol>
<p>通过上面源码分析我们这里做出简单的示例代码演示一下put操作和take操作是如何进行运作的，首先看一下示例代码，如下所示：</p>
<pre class="java"><code>/**
 * SynchronousQueue进行put和take操作。
 *
 * @author battleheart
 */
public class SynchronousQueueDemo {
    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        SynchronousQueue&lt;Integer&gt; queue = new SynchronousQueue&lt;&gt;(true);
        Thread thread1 = new Thread(() -&gt; {
            try {
                queue.put(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread1.start();
        Thread.sleep(2000);
        Thread thread2 = new Thread(() -&gt; {
            try {
                queue.put(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread2.start();
        Thread.sleep(10000);
        Thread thread3 = new Thread(() -&gt; {
            try {
                System.out.println(queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread3.start();
    }
}</code></pre>
<p>首先上来之后进行的是两次put操作，然后再take操作，默认队列上来会进行初始化，初始化的内容如下代码所示:</p>
<pre class="java"><code>TransferQueue() {
    QNode h = new QNode(null, false); // initialize to dummy node.
    head = h;
    tail = h;
}</code></pre>
<p>初始化后队列的状态如下图所示：</p>
<p><img src="https://img2018.cnblogs.com/blog/458325/201905/458325-20190511195104140-1116474850.png" /></p>
<p>当线程1执行put操作时，来分析下代码：</p>
<pre class="java"><code>QNode t = tail;
QNode h = head;
if (t == null || h == null)         // saw uninitialized value
    continue;</code></pre>
<p>首先执行局部变量t代表队尾指针，h代表队头指针，判断队头和队尾不为空则进行下面的操作，接下来是if…else语句这里是分水岭，当相同模式操作的时候执行if语句，当进行不同模式操作时执行的是else语句，程序是如何控制这样的操作的呢？接下来我们慢慢分析一下：</p>
<pre class="java"><code>if (h == t || t.isData == isData) { // 队列为空或者模式相同时进行if语句
    QNode tn = t.next;
    if (t != tail)                  // 判断t是否是队尾，不是则重新循环。
        continue;
    if (tn != null) {               // tn是队尾的下个节点，如果tn有内容则将队尾更换为tn，并且重新循环操作。
        advanceTail(t, tn);
        continue;
    }
    if (timed &amp;&amp; nanos &lt;= 0)        // 如果指定了timed并且延时时间用尽则直接返回空，这里操作主要是offer操作时，因为队列无存储空间的当offer时不允许插入。
        return null;
    if (s == null)                  // 这里是新节点生成。
        s = new QNode(e, isData);
    if (!t.casNext(null, s))        // 将尾节点的next节点修改为当前节点。
        continue;
    advanceTail(t, s);              // 队尾移动
    Object x = awaitFulfill(s, e, timed, nanos);    //自旋并且设置线程。
    if (x == s) {                   // wait was cancelled
        clean(t, s);
        return null;
    }
    if (!s.isOffList()) {           // not already unlinked
        advanceHead(t, s);          // unlink if head
        if (x != null)              // and forget fields
            s.item = s;
        s.waiter = null;
    }
    return (x != null) ? (E)x : e;
}</code></pre>
<p>上面代码是if语句中的内容，进入到if语句中的判断是如果头结点和尾节点相等代表队列为空，并没有元素所有要进行插入队列的操作，或者是队尾的节点的isData标志和当前操作的节点的类型一样时，会进行入队操作，isData标识当前元素是否是数据，如果为true代表是数据，如果为false则代表不是数据，换句话说只有模式相同的时候才会往队列中存放，如果不是模式相同的时候则代表互补模式，就不走if语句了，而是走了else语句，上面代码中做有注释讲解，下面看一下这里：</p>
<pre class="java"><code>if (s == null)                  // 这里是新节点生成。
    s = new QNode(e, isData);
if (!t.casNext(null, s))        // 将尾节点的next节点修改为当前节点。
    continue</code></pre>
<p>当执行上面代码后，队列的情况如下图所示：(这里视为<code>插入第一个元素</code>图，方便下面的引用)</p>
<p><img src="https://img2018.cnblogs.com/blog/458325/201905/458325-20190511195115791-1877855953.png" /></p>
<p>接下来执行这段代码：</p>
<pre class="java"><code> advanceTail(t, s);              // 队尾移动</code></pre>
<p><img src="https://img2018.cnblogs.com/blog/458325/201905/458325-20190511195122314-69757535.png" /></p>
<p>修改了tail节点后，这时候就需要进行自旋操作，并且设置QNode的waiter等待线程，并且将线程等待，等到唤醒线程进行唤醒操作</p>
<pre class="java"><code> Object x = awaitFulfill(s, e, timed, nanos);   //自旋并且设置线程。</code></pre>
<p>方法内部分析局部内容，上面已经全部内容的分析：</p>
<pre class="java"><code>if (spins &gt; 0)
    --spins;
else if (s.waiter == null)
    s.waiter = w;
else if (!timed)
    LockSupport.park(this);
else if (nanos &gt; spinForTimeoutThreshold)
    LockSupport.parkNanos(this, nanos);</code></pre>
<p>如果自旋时间spins还有则进行循环递减操作，接下来判断如果当前节点的waiter是空则价格当前线程赋值给waiter，上图中显然是为空的所以会把当前线程进行赋值给我waiter，接下来就是等待操作了。</p>
<p><img src="https://img2018.cnblogs.com/blog/458325/201905/458325-20190511195128586-1381252368.png" /></p>
<p>上面线程则处于等待状态，接下来是线程二进行操作，这里不进行重复进行，插入第二个元素队列的状况，此时线程二也处于等待状态。</p>
<p><img src="https://img2018.cnblogs.com/blog/458325/201905/458325-20190511195133984-1507606809.png" /></p>
<p>上面的主要是put了两次操作后队列的情况，接下来分析一下take操作时又是如何进行操作的，当take操作时，isData为false，而队尾的isData为true两个不相等，所以不会进入到if语句，而是进入到了else语句</p>
<pre class="java"><code>} else {                            // 互补模式
    QNode m = h.next;               // 获取头结点的下一个节点，进行互补操作。
    if (t != tail || m == null || h != head)
        continue;                   // 这里就是为了防止阅读不一致的问题
    Object x = m.item;
    if (isData == (x != null) ||    // 如果x=null说明已经被读取了。
        x == m ||                   // x节点和m节点相等说明被中断操作，被取消操作了。
        !m.casItem(x, e)) {         // 这里是将item值设置为null
        advanceHead(h, m);          // 移动头结点到头结点的下一个节点
        continue;
    }
    advanceHead(h, m);              // successfully fulfilled
    LockSupport.unpark(m.waiter);
    return (x != null) ? (E)x : e;
}</code></pre>
<p>首先获取头结点的下一个节点用于互补操作，也就是take操作，接下来进行阅读不一致的判断，防止其他线程进行了阅读操作，接下来获取需要弹出内容x=1，首先进行判断节点内容是不是已经被消费了，节点内容为null时则代表被消费了，接下来判断节点的item值是不是和本身相等如果相等话说明节点被取消了或者被中断了，然后移动头结点到下一个节点上，然后将<code>refenrence-715</code>的item值修改为null，<code>至于为什么修改为null这里留下一个悬念，这里还是比较重要的，大家看到这里的时候需要注意下</code>，显然这些都不会成立，所以if语句中内容不会被执行，接下来的队列的状态是是这个样子的：</p>
<p><img src="https://img2018.cnblogs.com/blog/458325/201905/458325-20190511195139214-877626357.png" /></p>
<p>OK，接下来就开始移动队头head了，将head移动到m节点上，执行代码如下所示：</p>
<pre class="java"><code>advanceHead(h, m);</code></pre>
<p>此时队列的状态是这个样子的：</p>
<p><img src="https://img2018.cnblogs.com/blog/458325/201905/458325-20190511195144711-804621885.png" /></p>
<pre class="java"><code>LockSupport.unpark(m.waiter);
return (x != null) ? (E)x : e;</code></pre>
<p>接下来将执行唤醒被等待的线程，也就是thread-0，然后返回获取item值1，take方法结束，但是这里并没有结束，因为唤醒了put的线程，此时会切换到put方法中，这时候线程唤醒后会执行<code>awaitFulfill</code>方法，此时循环时，有与item值修改为null则直接返回内容。</p>
<pre class="java"><code>Object x = s.item;
if (x != e)
    return x;</code></pre>
<p>这里的代码我们可以对照<code>插入第一个元素</code>图，s节点也就是当前m节点，获取值得时候已经修改为null，但是当时插入的值时1，所以两个不想等了，则直接返回null值。</p>
<pre class="java"><code>Object x = awaitFulfill(s, e, timed, nanos);
if (x == s) {                   // wait was cancelled
    clean(t, s);
    return null;
}
if (!s.isOffList()) {           // not already unlinked
    advanceHead(t, s);          // unlink if head
    if (x != null)              // and forget fields
        s.item = s;
    s.waiter = null;
}
return (x != null) ? (E)x : e;</code></pre>
<p>又返回到了transfer方法的if语句中，此时x和s并不相等所以不用进行clean操作，首先判断s节点是否已经离队了，显然并没有进行离队操作，<code>advanceHead(t, s);</code>操作不会被执行因为上面已近将头节点修改了，但是第一次插入的时候头结点还是<code>reference-716</code>，此时已经是<code>reference-715</code>,而t节点的引用地址是<code>reference-716</code>，所以不会操作，接下来就是将waiter设置为null，也就是忘记掉等待的线程。</p>
<p><img src="https://img2018.cnblogs.com/blog/458325/201905/458325-20190511195151368-1904763115.png" /></p>
<p>分析了正常的take和put操作，接下来分析下中断操作，由于中断相应后，会被执行<code>if(w.isInterrupted())</code>这段代码，它会执行<code>s.tryCancel(e)</code>方法，这个方法的作用的是将QNode节点的item节点赋值为当前QNode，这时候x和e值就不相等了（<code>if (x != e)</code>），x的值是s.item，则为当前QNode，而e的值是用户指定的值，这时候返回x(s.item)。返回到函数调用地方<code>transfer</code>中，这时候要执行下面语句：</p>
<pre class="java"><code>if (x == s) {
    clean(t, s);
    return null;
}</code></pre>
<p>进入到clean方法执行清理当前节点，clean 会清除已消费的 值</p>
<h3>非同步的实现步骤</h3>
<h4>put  当put操作时，isData为true</h4>
<p>(1) 队列为空或者模式相同时进行if语句</p>
<p>(2) 判断t是否是队尾，不是则重新循环。</p>
<p>(3) tn是队尾的下个节点，如果tn有内容则将队尾更换为tn，并且重新循环操作。</p>
<p>(4) 如果指定了timed并且延时时间用尽则直接返回空，这里操作主要是offer操作时，因为队列无存储空间的当offer时不允许插入。</p>
<p>(5) 将尾节点的next节点修改为当前节点。</p>
<p>(6) 队尾移动</p>
<p>(7) 自旋并且设置线程</p>
<h4>take  当take操作时，isData为false</h4>
<p>(1) 进入互补模式</p>
<p>(2) 获取头结点的下一个节点，进行互补操作。</p>
<p>(3) 判断节点是否被读取 ，x=null （x是item的值）说明已经被读取了。</p>
<p>(4) 已经被 读取 x节点和m节点相等说明被中断操作，被取消操作了。</p>
<p>(5) 这里是将item值设置为null 移动头结点到头结点的下一个节 进行下一次读取</p>
<p>(6)  没有被读取  移动头结点到头结点的下一个节</p>