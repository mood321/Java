<blockquote>
<p>作者：彤哥</p>
<p>出处：<a href="https://www.cnblogs.com/tong-yuan/">https://www.cnblogs.com/tong-yuan/</a></p>
</blockquote>
<hr />
<p>笔记由原博客+个人补充部分组成</p>
<h2><span id="i">简介</span></h2>
<p>HashMap采用key/value存储结构，每个key对应唯一的value，查询和修改的速度都很快，能达到O(1)的平均时间复杂度。它是非线程安全的，且不保证元素存储的顺序；</p>
<h2><span id="i-2">继承体系</span></h2>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/HashMap.png" alt="HashMap" /></p>
<p>HashMap实现了Cloneable，可以被克隆。</p>
<p>HashMap实现了Serializable，可以被序列化。</p>
<p>HashMap继承自AbstractMap，实现了Map接口，具有Map的所有功能。</p>
<h2><span id="i-3">存储结构</span></h2>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/HashMap-structure.png" alt="HashMap-structure" /></p>
<p>在Java中，HashMap的实现采用了（数组 + 链表 + 红黑树）的复杂结构，数组的一个元素又称作桶。</p>
<p>在添加元素时，会根据hash值算出元素在数组中的位置，如果该位置没有元素，则直接把元素放置在此处，如果该位置有元素了，则把元素以链表的形式放置在链表的尾部。</p>
<p>当一个链表的元素个数达到一定的数量（且数组的长度达到一定的长度）后，则把链表转化为红黑树，从而提高效率。</p>
<p>数组的查询效率为O(1)，链表的查询效率是O(k)，红黑树的查询效率是O(log k)，k为桶中的元素个数，所以当元素数量非常多的时候，转化为红黑树能极大地提高效率。</p>
<h2><span id="i-4">源码解析</span></h2>
<h3><span id="i-5">属性</span></h3>
<pre><code class="java"><br />/**
 * 默认的初始容量为16
 */
static final int DEFAULT_INITIAL_CAPACITY = 1 &lt;&lt; 4;
/**
 * 最大的容量为2的30次方
 */
static final int MAXIMUM_CAPACITY = 1 &lt;&lt; 30;
/**
 * 默认的装载因子
 */
static final float DEFAULT_LOAD_FACTOR = 0.75f;
/**
 * 当一个桶中的元素个数大于等于8时进行树化
 */
static final int TREEIFY_THRESHOLD = 8;
/**
 * 当一个桶中的元素个数小于等于6时把树转化为链表
 */
static final int UNTREEIFY_THRESHOLD = 6;
/**
 * 当桶的个数达到64的时候才进行树化
 */
static final int MIN_TREEIFY_CAPACITY = 64;
/**
 * 数组，又叫作桶（bucket）
 */
transient Node&lt;K,V&gt;[] table;
/**
 * 作为entrySet()的缓存
 */
transient Set&lt;Map.Entry&lt;K,V&gt;&gt; entrySet;
/**
 * 元素的数量
 */
transient int size;
/**
 * 修改次数，用于在迭代的时候执行快速失败策略
 */
transient int modCount;
/**
 * 当桶的使用数量达到多少时进行扩容，threshold = capacity * loadFactor
 */
int threshold;
/**
 * 装载因子
 */
final float loadFactor;
</code></pre>
<p>（1）容量</p>
<p>容量为数组的长度，亦即桶的个数，默认为16，最大为2的30次方，当容量达到64时才可以树化。</p>
<p>（2）装载因子</p>
<p>装载因子用来计算容量达到多少时才进行扩容，默认装载因子为0.75。</p>
<p>（3）树化</p>
<p>树化，当容量达到64且链表的长度达到8时进行树化，当链表的长度小于6时反树化。</p>
<h3><span id="Node">Node内部类</span></h3>
<p>Node是一个典型的单链表节点，其中，hash用来存储key计算得来的hash值。</p>
<pre><code class="java">static class Node&lt;K,V&gt; implements Map.Entry&lt;K,V&gt; {
    final int hash;
    final K key;
    V value;
    Node&lt;K,V&gt; next;
}
</code></pre>
<h3><span id="TreeNode">TreeNode内部类</span></h3>
<p>这是一个神奇的类，它继承自LinkedHashMap中的Entry类，关于LInkedHashMap.Entry这个类我们后面再讲。</p>
<p>TreeNode是一个典型的树型节点，其中，prev是链表中的节点，用于在删除元素的时候可以快速找到它的前置节点。</p>
<pre><code class="java">// 位于HashMap中
static final class TreeNode&lt;K,V&gt; extends LinkedHashMap.Entry&lt;K,V&gt; {
    TreeNode&lt;K,V&gt; parent;  // red-black tree links
    TreeNode&lt;K,V&gt; left;
    TreeNode&lt;K,V&gt; right;
    TreeNode&lt;K,V&gt; prev;    // needed to unlink next upon deletion
    boolean red;
}
// 位于LinkedHashMap中，典型的双向链表节点
static class Entry&lt;K,V&gt; extends HashMap.Node&lt;K,V&gt; {
    Entry&lt;K,V&gt; before, after;
    Entry(int hash, K key, V value, Node&lt;K,V&gt; next) {
        super(hash, key, value, next);
    }
}
</code></pre>
<h3><span id="HashMap">HashMap()构造方法</span></h3>
<p>空参构造方法，全部使用默认值。</p>
<pre><code class="java">public HashMap() {
    this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
}
</code></pre>
<h3><span id="HashMapint_initialCapacity">HashMap(int initialCapacity)构造方法</span></h3>
<p>调用HashMap(int initialCapacity, float loadFactor)构造方法，传入默认装载因子。</p>
<pre><code class="java">public HashMap(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
}
</code></pre>
<h3><span id="HashMapint_initialCapacity-2">HashMap(int initialCapacity)构造方法</span></h3>
<p>判断传入的初始容量和装载因子是否合法，并计算扩容门槛，扩容门槛为传入的初始容量往上取最近的2的n次方。</p>
<pre><code class="java">public HashMap(int initialCapacity, float loadFactor) {
    // 检查传入的初始容量是否合法
    if (initialCapacity &lt; 0)
        throw new IllegalArgumentException("Illegal initial capacity: " +
                                           initialCapacity);
    if (initialCapacity &gt; MAXIMUM_CAPACITY)
        initialCapacity = MAXIMUM_CAPACITY;
    // 检查装载因子是否合法
    if (loadFactor &lt;= 0 || Float.isNaN(loadFactor))
        throw new IllegalArgumentException("Illegal load factor: " +
                                           loadFactor);
    this.loadFactor = loadFactor;
    // 计算扩容门槛
    this.threshold = tableSizeFor(initialCapacity);
}
static final int tableSizeFor(int cap) {
    // 扩容门槛为传入的初始容量往上取最近的2的n次方
    int n = cap - 1;
    n |= n &gt;&gt;&gt; 1;
    n |= n &gt;&gt;&gt; 2;
    n |= n &gt;&gt;&gt; 4;
    n |= n &gt;&gt;&gt; 8;
    n |= n &gt;&gt;&gt; 16;
    return (n &lt; 0) ? 1 : (n &gt;= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
}
</code></pre>
<h3><span id="putK_key_V_value">put(K key, V value)方法</span></h3>
<p>添加元素的入口。</p>
<pre><code class="java">public V put(K key, V value) {
    // 调用hash(key)计算出key的hash值
    return putVal(hash(key), key, value, false, true);
}
static final int hash(Object key) {
    int h;
    // 如果key为null，则hash值为0，否则调用key的hashCode()方法
    // 并让高16位与整个hash异或，这样做是为了使计算出的hash更分散
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h &gt;&gt;&gt; 16);
}
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
               boolean evict) {
    Node&lt;K, V&gt;[] tab;
    Node&lt;K, V&gt; p;
    int n, i;
    // 如果桶的数量为0，则初始化
    if ((tab = table) == null || (n = tab.length) == 0)
        // 调用resize()初始化
        n = (tab = resize()).length;
    // (n - 1) &amp; hash 计算元素在哪个桶中
    // 如果这个桶中还没有元素，则把这个元素放在桶中的第一个位置
    if ((p = tab[i = (n - 1) &amp; hash]) == null)
        // 新建一个节点放在桶中
        tab[i] = newNode(hash, key, value, null);
    else {
        // 如果桶中已经有元素存在了
        Node&lt;K, V&gt; e;
        K k;
        // 如果桶中第一个元素的key与待插入元素的key相同，保存到e中用于后续修改value值
        if (p.hash == hash &amp;&amp;
                ((k = p.key) == key || (key != null &amp;&amp; key.equals(k))))
            e = p;
        else if (p instanceof TreeNode)
            // 如果第一个元素是树节点，则调用树节点的putTreeVal插入元素
            e = ((TreeNode&lt;K, V&gt;) p).putTreeVal(this, tab, hash, key, value);
        else {
            // 遍历这个桶对应的链表，binCount用于存储链表中元素的个数
            for (int binCount = 0; ; ++binCount) {
                // 如果链表遍历完了都没有找到相同key的元素，说明该key对应的元素不存在，则在链表最后插入一个新节点
                if ((e = p.next) == null) {
                    p.next = newNode(hash, key, value, null);
                    // 如果插入新节点后链表长度大于8，则判断是否需要树化，因为第一个元素没有加到binCount中，所以这里-1
                    if (binCount &gt;= TREEIFY_THRESHOLD - 1) // -1 for 1st
                        treeifyBin(tab, hash);
                    break;
                }
                // 如果待插入的key在链表中找到了，则退出循环
                if (e.hash == hash &amp;&amp;
                        ((k = e.key) == key || (key != null &amp;&amp; key.equals(k))))
                    break;
                p = e;
            }
        }
        // 如果找到了对应key的元素
        if (e != null) { // existing mapping for key
            // 记录下旧值
            V oldValue = e.value;
            // 判断是否需要替换旧值
            if (!onlyIfAbsent || oldValue == null)
                // 替换旧值为新值
                e.value = value;
            // 在节点被访问后做点什么事，在LinkedHashMap中用到
            afterNodeAccess(e);
            // 返回旧值
            return oldValue;
        }
    }
    // 到这里了说明没有找到元素
    // 修改次数加1
    ++modCount;
    // 元素数量加1，判断是否需要扩容
    if (++size &gt; threshold)
        // 扩容
        resize();
    // 在节点插入后做点什么事，在LinkedHashMap中用到
    afterNodeInsertion(evict);
    // 没找到元素返回null
    return null;
}
</code></pre>
<p>（1）计算key的hash值；</p>
<p>（2）如果桶（数组）数量为0，则初始化桶；</p>
<p>（3）如果key所在的桶没有元素，则直接插入；</p>
<p>（4）如果key所在的桶中的第一个元素的key与待插入的key相同，说明找到了元素，转后续流程（9）处理；</p>
<p>（5）如果第一个元素是树节点，则调用树节点的putTreeVal()寻找元素或插入树节点；</p>
<p>（6）如果不是以上三种情况，则遍历桶对应的链表查找key是否存在于链表中；</p>
<p>（7）如果找到了对应key的元素，则转后续流程（9）处理；</p>
<p>（8）如果没找到对应key的元素，则在链表最后插入一个新节点并判断是否需要树化；</p>
<p>（9）如果找到了对应key的元素，则判断是否需要替换旧值，并直接返回旧值；</p>
<p>（10）如果插入了元素，则数量加1并判断是否需要扩容；</p>
<h3><span id="resize">resize()方法</span></h3>
<p>扩容方法。</p>
<pre><code class="java">final Node&lt;K, V&gt;[] resize() {
    // 旧数组
    Node&lt;K, V&gt;[] oldTab = table;
    // 旧容量
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    // 旧扩容门槛
    int oldThr = threshold;
    int newCap, newThr = 0;
    if (oldCap &gt; 0) {
        if (oldCap &gt;= MAXIMUM_CAPACITY) {
            // 如果旧容量达到了最大容量，则不再进行扩容
            threshold = Integer.MAX_VALUE;
            return oldTab;
        } else if ((newCap = oldCap &lt;&lt; 1) &lt; MAXIMUM_CAPACITY &amp;&amp;
                oldCap &gt;= DEFAULT_INITIAL_CAPACITY)
            // 如果旧容量的两倍小于最大容量并且旧容量大于默认初始容量（16），则容量扩大为两部，扩容门槛也扩大为两倍
            newThr = oldThr &lt;&lt; 1; // double threshold
    } else if (oldThr &gt; 0) // initial capacity was placed in threshold
        // 使用非默认构造方法创建的map，第一次插入元素会走到这里
        // 如果旧容量为0且旧扩容门槛大于0，则把新容量赋值为旧门槛
        newCap = oldThr;
    else {               // zero initial threshold signifies using defaults
        // 调用默认构造方法创建的map，第一次插入元素会走到这里
        // 如果旧容量旧扩容门槛都是0，说明还未初始化过，则初始化容量为默认容量，扩容门槛为默认容量*默认装载因子
        newCap = DEFAULT_INITIAL_CAPACITY;
        newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }
    if (newThr == 0) {
        // 如果新扩容门槛为0，则计算为容量*装载因子，但不能超过最大容量
        float ft = (float) newCap * loadFactor;
        newThr = (newCap &lt; MAXIMUM_CAPACITY &amp;&amp; ft &lt; (float) MAXIMUM_CAPACITY ?
                (int) ft : Integer.MAX_VALUE);
    }
    // 赋值扩容门槛为新门槛
    threshold = newThr;
    // 新建一个新容量的数组
    @SuppressWarnings({"rawtypes", "unchecked"})
    Node&lt;K, V&gt;[] newTab = (Node&lt;K, V&gt;[]) new Node[newCap];
    // 把桶赋值为新数组
    table = newTab;
    // 如果旧数组不为空，则搬移元素
    if (oldTab != null) {
        // 遍历旧数组
        for (int j = 0; j &lt; oldCap; ++j) {
            Node&lt;K, V&gt; e;
            // 如果桶中第一个元素不为空，赋值给e
            if ((e = oldTab[j]) != null) {
                // 清空旧桶，便于GC回收  
                oldTab[j] = null;
                // 如果这个桶中只有一个元素，则计算它在新桶中的位置并把它搬移到新桶中
                // 因为每次都扩容两倍，所以这里的第一个元素搬移到新桶的时候新桶肯定还没有元素
                if (e.next == null)
                    newTab[e.hash &amp; (newCap - 1)] = e;
                else if (e instanceof TreeNode)
                    // 如果第一个元素是树节点，则把这颗树打散成两颗树插入到新桶中去
                    ((TreeNode&lt;K, V&gt;) e).split(this, newTab, j, oldCap);
                else { // preserve order
                    // 如果这个链表不止一个元素且不是一颗树
                    // 则分化成两个链表插入到新的桶中去
                    // 比如，假如原来容量为4，3、7、11、15这四个元素都在三号桶中
                    // 现在扩容到8，则3和11还是在三号桶，7和15要搬移到七号桶中去
                    // 也就是分化成了两个链表
                    Node&lt;K, V&gt; loHead = null, loTail = null;
                    Node&lt;K, V&gt; hiHead = null, hiTail = null;
                    Node&lt;K, V&gt; next;
                    do {
                        next = e.next;
                        // (e.hash &amp; oldCap) == 0的元素放在低位链表中
                        // 比如，3 &amp; 4 == 0
                        if ((e.hash &amp; oldCap) == 0) {
                            if (loTail == null)
                                loHead = e;
                            else
                                loTail.next = e;
                            loTail = e;
                        } else {
                            // (e.hash &amp; oldCap) != 0的元素放在高位链表中
                            // 比如，7 &amp; 4 != 0
                            if (hiTail == null)
                                hiHead = e;
                            else
                                hiTail.next = e;
                            hiTail = e;
                        }
                    } while ((e = next) != null);
                    // 遍历完成分化成两个链表了
                    // 低位链表在新桶中的位置与旧桶一样（即3和11还在三号桶中）
                    if (loTail != null) {
                        loTail.next = null;
                        newTab[j] = loHead;
                    }
                    // 高位链表在新桶中的位置正好是原来的位置加上旧容量（即7和15搬移到七号桶了）
                    if (hiTail != null) {
                        hiTail.next = null;
                        newTab[j + oldCap] = hiHead;
                    }
                }
            }
        }
    }
    return newTab;
}
</code></pre>
<p>（1）如果使用是默认构造方法，则第一次插入元素时初始化为默认值，容量为16，扩容门槛为12；</p>
<p>（2）如果使用的是非默认构造方法，则第一次插入元素时初始化容量等于扩容门槛，扩容门槛在构造方法里等于传入容量向上最近的2的n次方；</p>
<p>（3）如果旧容量大于0，则新容量等于旧容量的2倍，但不超过最大容量2的30次方，新扩容门槛为旧扩容门槛的2倍；</p>
<p>（4）创建一个新容量的桶；</p>
<p>（5）搬移元素，原链表分化成两个链表，低位链表存储在原来桶的位置，高位链表搬移到原来桶的位置加旧容量的位置；</p>
<h3><span id="TreeNodeputTreeVal8230">TreeNode.putTreeVal(&#8230;)方法</span></h3>
<p>插入元素到红黑树中的方法。</p>
<pre><code class="java">final TreeNode&lt;K, V&gt; putTreeVal(HashMap&lt;K, V&gt; map, Node&lt;K, V&gt;[] tab,
                                int h, K k, V v) {
    Class&lt;?&gt; kc = null;
    // 标记是否找到这个key的节点
    boolean searched = false;
    // 找到树的根节点
    TreeNode&lt;K, V&gt; root = (parent != null) ? root() : this;
    // 从树的根节点开始遍历
    for (TreeNode&lt;K, V&gt; p = root; ; ) {
        // dir=direction，标记是在左边还是右边
        // ph=p.hash，当前节点的hash值
        int dir, ph;
        // pk=p.key，当前节点的key值
        K pk;
        if ((ph = p.hash) &gt; h) {
            // 当前hash比目标hash大，说明在左边
            dir = -1;
        }
        else if (ph &lt; h)
            // 当前hash比目标hash小，说明在右边
            dir = 1;
        else if ((pk = p.key) == k || (k != null &amp;&amp; k.equals(pk)))
            // 两者hash相同且key相等，说明找到了节点，直接返回该节点
            // 回到putVal()中判断是否需要修改其value值
            return p;
        else if ((kc == null &amp;&amp;
                // 如果k是Comparable的子类则返回其真实的类，否则返回null
                (kc = comparableClassFor(k)) == null) ||
                // 如果k和pk不是同样的类型则返回0，否则返回两者比较的结果
                (dir = compareComparables(kc, k, pk)) == 0) {
            // 这个条件表示两者hash相同但是其中一个不是Comparable类型或者两者类型不同
            // 比如key是Object类型，这时可以传String也可以传Integer，两者hash值可能相同
            // 在红黑树中把同样hash值的元素存储在同一颗子树，这里相当于找到了这颗子树的顶点
            // 从这个顶点分别遍历其左右子树去寻找有没有跟待插入的key相同的元素
            if (!searched) {
                TreeNode&lt;K, V&gt; q, ch;
                searched = true;
                // 遍历左右子树找到了直接返回
                if (((ch = p.left) != null &amp;&amp;
                        (q = ch.find(h, k, kc)) != null) ||
                        ((ch = p.right) != null &amp;&amp;
                                (q = ch.find(h, k, kc)) != null))
                    return q;
            }
            // 如果两者类型相同，再根据它们的内存地址计算hash值进行比较
            dir = tieBreakOrder(k, pk);
        }
        TreeNode&lt;K, V&gt; xp = p;
        if ((p = (dir &lt;= 0) ? p.left : p.right) == null) {
            // 如果最后确实没找到对应key的元素，则新建一个节点
            Node&lt;K, V&gt; xpn = xp.next;
            TreeNode&lt;K, V&gt; x = map.newTreeNode(h, k, v, xpn);
            if (dir &lt;= 0)
                xp.left = x;
            else
                xp.right = x;
            xp.next = x;
            x.parent = x.prev = xp;
            if (xpn != null)
                ((TreeNode&lt;K, V&gt;) xpn).prev = x;
            // 插入树节点后平衡
            // 把root节点移动到链表的第一个节点
            moveRootToFront(tab, balanceInsertion(root, x));
            return null;
        }
    }
}
</code></pre>
<p>（1）寻找根节点；</p>
<p>（2）从根节点开始查找；</p>
<p>（3）比较hash值及key值，如果都相同，直接返回，在putVal()方法中决定是否要替换value值；</p>
<p>（4）根据hash值及key值确定在树的左子树还是右子树查找，找到了直接返回；</p>
<p>（5）如果最后没有找到则在树的相应位置插入元素，并做平衡；</p>
<h3><span id="treeifyBin">treeifyBin()方法</span></h3>
<p>如果插入元素后链表的长度大于等于8则判断是否需要树化。</p>
<pre><code class="java">final void treeifyBin(Node&lt;K, V&gt;[] tab, int hash) {
    int n, index;
    Node&lt;K, V&gt; e;
    if (tab == null || (n = tab.length) &lt; MIN_TREEIFY_CAPACITY)
        // 如果桶数量小于64，直接扩容而不用树化
        // 因为扩容之后，链表会分化成两个链表，达到减少元素的作用
        // 当然也不一定，比如容量为4，里面存的全是除以4余数等于3的元素
        // 这样即使扩容也无法减少链表的长度
        resize();
    else if ((e = tab[index = (n - 1) &amp; hash]) != null) {
        TreeNode&lt;K, V&gt; hd = null, tl = null;
        // 把所有节点换成树节点
        do {
            TreeNode&lt;K, V&gt; p = replacementTreeNode(e, null);
            if (tl == null)
                hd = p;
            else {
                p.prev = tl;
                tl.next = p;
            }
            tl = p;
        } while ((e = e.next) != null);
        // 如果进入过上面的循环，则从头节点开始树化
        if ((tab[index] = hd) != null)
            hd.treeify(tab);
    }
}
</code></pre>
<h3><span id="TreeNodetreeify">TreeNode.treeify()方法</span></h3>
<p>真正树化的方法。</p>
<pre><code class="java">final void treeify(Node&lt;K, V&gt;[] tab) {
    TreeNode&lt;K, V&gt; root = null;
    for (TreeNode&lt;K, V&gt; x = this, next; x != null; x = next) {
        next = (TreeNode&lt;K, V&gt;) x.next;
        x.left = x.right = null;
        // 第一个元素作为根节点且为黑节点，其它元素依次插入到树中再做平衡
        if (root == null) {
            x.parent = null;
            x.red = false;
            root = x;
        } else {
            K k = x.key;
            int h = x.hash;
            Class&lt;?&gt; kc = null;
            // 从根节点查找元素插入的位置
            for (TreeNode&lt;K, V&gt; p = root; ; ) {
                int dir, ph;
                K pk = p.key;
                if ((ph = p.hash) &gt; h)
                    dir = -1;
                else if (ph &lt; h)
                    dir = 1;
                else if ((kc == null &amp;&amp;
                        (kc = comparableClassFor(k)) == null) ||
                        (dir = compareComparables(kc, k, pk)) == 0)
                    dir = tieBreakOrder(k, pk);
                // 如果最后没找到元素，则插入
                TreeNode&lt;K, V&gt; xp = p;
                if ((p = (dir &lt;= 0) ? p.left : p.right) == null) {
                    x.parent = xp;
                    if (dir &lt;= 0)
                        xp.left = x;
                    else
                        xp.right = x;
                    // 插入后平衡，默认插入的是红节点，在balanceInsertion()方法里
                    root = balanceInsertion(root, x);
                    break;
                }
            }
        }
    }
    // 把根节点移动到链表的头节点，因为经过平衡之后原来的第一个元素不一定是根节点了
    moveRootToFront(tab, root);
}
</code></pre>
<p>（1）从链表的第一个元素开始遍历；</p>
<p>（2）将第一个元素作为根节点；</p>
<p>（3）其它元素依次插入到红黑树中，再做平衡；</p>
<p>（4）将根节点移到链表第一元素的位置（因为平衡的时候根节点会改变）；</p>
<h3><span id="getObject_key">get(Object key)方法</span></h3>
<pre><code class="java">public V get(Object key) {
    Node&lt;K, V&gt; e;
    return (e = getNode(hash(key), key)) == null ? null : e.value;
}
final Node&lt;K, V&gt; getNode(int hash, Object key) {
    Node&lt;K, V&gt;[] tab;
    Node&lt;K, V&gt; first, e;
    int n;
    K k;
    // 如果桶的数量大于0并且待查找的key所在的桶的第一个元素不为空
    if ((tab = table) != null &amp;&amp; (n = tab.length) &gt; 0 &amp;&amp;
            (first = tab[(n - 1) &amp; hash]) != null) {
        // 检查第一个元素是不是要查的元素，如果是直接返回
        if (first.hash == hash &amp;&amp; // always check first node
                ((k = first.key) == key || (key != null &amp;&amp; key.equals(k))))
            return first;
        if ((e = first.next) != null) {
            // 如果第一个元素是树节点，则按树的方式查找
            if (first instanceof TreeNode)
                return ((TreeNode&lt;K, V&gt;) first).getTreeNode(hash, key);
            // 否则就遍历整个链表查找该元素
            do {
                if (e.hash == hash &amp;&amp;
                        ((k = e.key) == key || (key != null &amp;&amp; key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    return null;
}
</code></pre>
<p>（1）计算key的hash值；</p>
<p>（2）找到key所在的桶及其第一个元素；</p>
<p>（3）如果第一个元素的key等于待查找的key，直接返回；</p>
<p>（4）如果第一个元素是树节点就按树的方式来查找，否则按链表方式查找；</p>
<h3><span id="TreeNodegetTreeNodeint_h_Object_k">TreeNode.getTreeNode(int h, Object k)方法</span></h3>
<pre><code class="java">final TreeNode&lt;K, V&gt; getTreeNode(int h, Object k) {
    // 从树的根节点开始查找
    return ((parent != null) ? root() : this).find(h, k, null);
}
final TreeNode&lt;K, V&gt; find(int h, Object k, Class&lt;?&gt; kc) {
    TreeNode&lt;K, V&gt; p = this;
    do {
        int ph, dir;
        K pk;
        TreeNode&lt;K, V&gt; pl = p.left, pr = p.right, q;
        if ((ph = p.hash) &gt; h)
            // 左子树
            p = pl;
        else if (ph &lt; h)
            // 右子树
            p = pr;
        else if ((pk = p.key) == k || (k != null &amp;&amp; k.equals(pk)))
            // 找到了直接返回
            return p;
        else if (pl == null)
            // hash相同但key不同，左子树为空查右子树
            p = pr;
        else if (pr == null)
            // 右子树为空查左子树
            p = pl;
        else if ((kc != null ||
                (kc = comparableClassFor(k)) != null) &amp;&amp;
                (dir = compareComparables(kc, k, pk)) != 0)
            // 通过compare方法比较key值的大小决定使用左子树还是右子树
            p = (dir &lt; 0) ? pl : pr;
        else if ((q = pr.find(h, k, kc)) != null)
            // 如果以上条件都不通过，则尝试在右子树查找
            return q;
        else
            // 都没找到就在左子树查找
            p = pl;
    } while (p != null);
    return null;
}
</code></pre>
<p>经典二叉查找树的查找过程，先根据hash值比较，再根据key值比较决定是查左子树还是右子树。</p>
<h3><span id="removeObject_key">remove(Object key)方法</span></h3>
<pre><code class="java">public V remove(Object key) {
    Node&lt;K, V&gt; e;
    return (e = removeNode(hash(key), key, null, false, true)) == null ?
            null : e.value;
}
final Node&lt;K, V&gt; removeNode(int hash, Object key, Object value,
                            boolean matchValue, boolean movable) {
    Node&lt;K, V&gt;[] tab;
    Node&lt;K, V&gt; p;
    int n, index;
    // 如果桶的数量大于0且待删除的元素所在的桶的第一个元素不为空
    if ((tab = table) != null &amp;&amp; (n = tab.length) &gt; 0 &amp;&amp;
            (p = tab[index = (n - 1) &amp; hash]) != null) {
        Node&lt;K, V&gt; node = null, e;
        K k;
        V v;
        if (p.hash == hash &amp;&amp;
                ((k = p.key) == key || (key != null &amp;&amp; key.equals(k))))
            // 如果第一个元素正好就是要找的元素，赋值给node变量后续删除使用
            node = p;
        else if ((e = p.next) != null) {
            if (p instanceof TreeNode)
                // 如果第一个元素是树节点，则以树的方式查找节点
                node = ((TreeNode&lt;K, V&gt;) p).getTreeNode(hash, key);
            else {
                // 否则遍历整个链表查找元素
                do {
                    if (e.hash == hash &amp;&amp;
                            ((k = e.key) == key ||
                                    (key != null &amp;&amp; key.equals(k)))) {
                        node = e;
                        break;
                    }
                    p = e;
                } while ((e = e.next) != null);
            }
        }
        // 如果找到了元素，则看参数是否需要匹配value值，如果不需要匹配直接删除，如果需要匹配则看value值是否与传入的value相等
        if (node != null &amp;&amp; (!matchValue || (v = node.value) == value ||
                (value != null &amp;&amp; value.equals(v)))) {
            if (node instanceof TreeNode)
                // 如果是树节点，调用树的删除方法（以node调用的，是删除自己）
                ((TreeNode&lt;K, V&gt;) node).removeTreeNode(this, tab, movable);
            else if (node == p)
                // 如果待删除的元素是第一个元素，则把第二个元素移到第一的位置
                tab[index] = node.next;
            else
                // 否则删除node节点
                p.next = node.next;
            ++modCount;
            --size;
            // 删除节点后置处理
            afterNodeRemoval(node);
            return node;
        }
    }
    return null;
}
</code></pre>
<p>（1）先查找元素所在的节点；</p>
<p>（2）如果找到的节点是树节点，则按树的移除节点处理；</p>
<p>（3）如果找到的节点是桶中的第一个节点，则把第二个节点移到第一的位置；</p>
<p>（4）否则按链表删除节点处理；</p>
<p>（5）修改size，调用移除节点后置处理等；</p>
<h3><span id="TreeNoderemoveTreeNode8230">TreeNode.removeTreeNode(&#8230;)方法</span></h3>
<pre><code class="java">final void removeTreeNode(HashMap&lt;K, V&gt; map, Node&lt;K, V&gt;[] tab,
                          boolean movable) {
    int n;
    // 如果桶的数量为0直接返回
    if (tab == null || (n = tab.length) == 0)
        return;
    // 节点在桶中的索引
    int index = (n - 1) &amp; hash;
    // 第一个节点，根节点，根左子节点
    TreeNode&lt;K, V&gt; first = (TreeNode&lt;K, V&gt;) tab[index], root = first, rl;
    // 后继节点，前置节点
    TreeNode&lt;K, V&gt; succ = (TreeNode&lt;K, V&gt;) next, pred = prev;
    if (pred == null)
        // 如果前置节点为空，说明当前节点是根节点，则把后继节点赋值到第一个节点的位置，相当于删除了当前节点
        tab[index] = first = succ;
    else
        // 否则把前置节点的下个节点设置为当前节点的后继节点，相当于删除了当前节点
        pred.next = succ;
    // 如果后继节点不为空，则让后继节点的前置节点指向当前节点的前置节点，相当于删除了当前节点
    if (succ != null)
        succ.prev = pred;
    // 如果第一个节点为空，说明没有后继节点了，直接返回
    if (first == null)
        return;
    // 如果根节点的父节点不为空，则重新查找父节点
    if (root.parent != null)
        root = root.root();
    // 如果根节点为空，则需要反树化（将树转化为链表）
    // 如果需要移动节点且树的高度比较小，则需要反树化
    if (root == null
            || (movable
            &amp;&amp; (root.right == null
            || (rl = root.left) == null
            || rl.left == null))) {
        tab[index] = first.untreeify(map);  // too small
        return;
    }
    // 分割线，以上都是删除链表中的节点，下面才是直接删除红黑树的节点（因为TreeNode本身即是链表节点又是树节点）
    // 删除红黑树节点的大致过程是寻找右子树中最小的节点放到删除节点的位置，然后做平衡，此处不过多注释
    TreeNode&lt;K, V&gt; p = this, pl = left, pr = right, replacement;
    if (pl != null &amp;&amp; pr != null) {
        TreeNode&lt;K, V&gt; s = pr, sl;
        while ((sl = s.left) != null) // find successor
            s = sl;
        boolean c = s.red;
        s.red = p.red;
        p.red = c; // swap colors
        TreeNode&lt;K, V&gt; sr = s.right;
        TreeNode&lt;K, V&gt; pp = p.parent;
        if (s == pr) { // p was s's direct parent
            p.parent = s;
            s.right = p;
        } else {
            TreeNode&lt;K, V&gt; sp = s.parent;
            if ((p.parent = sp) != null) {
                if (s == sp.left)
                    sp.left = p;
                else
                    sp.right = p;
            }
            if ((s.right = pr) != null)
                pr.parent = s;
        }
        p.left = null;
        if ((p.right = sr) != null)
            sr.parent = p;
        if ((s.left = pl) != null)
            pl.parent = s;
        if ((s.parent = pp) == null)
            root = s;
        else if (p == pp.left)
            pp.left = s;
        else
            pp.right = s;
        if (sr != null)
            replacement = sr;
        else
            replacement = p;
    } else if (pl != null)
        replacement = pl;
    else if (pr != null)
        replacement = pr;
    else
        replacement = p;
    if (replacement != p) {
        TreeNode&lt;K, V&gt; pp = replacement.parent = p.parent;
        if (pp == null)
            root = replacement;
        else if (p == pp.left)
            pp.left = replacement;
        else
            pp.right = replacement;
        p.left = p.right = p.parent = null;
    }
    TreeNode&lt;K, V&gt; r = p.red ? root : balanceDeletion(root, replacement);
    if (replacement == p) {  // detach
        TreeNode&lt;K, V&gt; pp = p.parent;
        p.parent = null;
        if (pp != null) {
            if (p == pp.left)
                pp.left = null;
            else if (p == pp.right)
                pp.right = null;
        }
    }
    if (movable)
        moveRootToFront(tab, r);
}
</code></pre>
<p>（1）TreeNode本身既是链表节点也是红黑树节点；</p>
<p>（2）先删除链表节点；</p>
<p>（3）再删除红黑树节点并做平衡；</p>
<h2><span id="i-6">总结</span></h2>
<p>（1）HashMap是一种散列表，采用（数组 + 链表 + 红黑树）的存储结构；</p>
<p>（2）HashMap的默认初始容量为16（1&lt;&lt;4），默认装载因子为0.75f，容量总是2的n次方；</p>
<p>（3）HashMap扩容时每次容量变为原来的两倍；</p>
<p>（4）当桶的数量小于64时不会进行树化，只会扩容；</p>
<p>（5）当桶的数量大于64且单个桶中元素的数量大于8时，进行树化；</p>
<p>（6）当单个桶中元素数量小于6时，进行反树化；</p>
<p>（7）HashMap是非线程安全的容器；</p>
<p>（8）HashMap查找添加元素的时间复杂度都为O(1)；</p>
<h2><span id="i-7">带详细注释的源码地址</span></h2>
<p><a href="https://gitee.com/alan-tang-tt/yuan/blob/master/死磕%20java集合系列/code/HashMap.java">带详细注释的源码地址</a></p>
<h2><span id="i-8">彩蛋</span></h2>
<p><em>红黑树知多少？</em></p>
<p>红黑树具有以下5种性质：</p>
<p>（1）节点是红色或黑色。</p>
<p>（2）根节点是黑色。</p>
<p>（3）每个叶节点（NIL节点，空节点）是黑色的。</p>
<p>（4）每个红色节点的两个子节点都是黑色。(从每个叶子到根的所有路径上不能有两个连续的红色节点)</p>
<p>（5）从任一节点到其每个叶子的所有路径都包含相同数目的黑色节点。</p>
<p>红黑树的时间复杂度为O(log n)，与树的高度成正比。</p>
<p>红黑树每次的插入、删除操作都需要做平衡，平衡时有可能会改变根节点的位置，颜色转换，左旋，右旋等。</p>

<h3>HashMap的多线程安全问题</h3>
    <li> 1.7</li>
     <p>迁移的源代码</p>
     <pre class="java"><code>
     void transfer(Entry[] newTable) {
         Entry[] src = table;
         int newCapacity = newTable.length;
         //下面这段代码的意思是：
         //  从OldTable里摘一个元素出来，然后放到NewTable中
         for (int j = 0; j < src.length; j++) {
             Entry<K,V> e = src[j];
             if (e != null) {
                 src[j] = null;
                 do {
                     Entry<K,V> next = e.next;
                     int i = indexFor(e.hash, newCapacity);
                     e.next = newTable[i];
                     newTable[i] = e;
                     e = next;
                 } while (e != null);
             }
         }
     }
     </code></pre>
     <p>该方法实现的机制就是将每个链表转化到新链表，并且链表中的位置发生反转，而这在多线程情况下是很容易造成链表回路，从而发生 get() 死循环。所以只要保证建新链时还是按照原来的顺序的话就不会产生循环（JDK 8 的改进）
    <li> JDK 8 的改进</li>
     <p>JDK 8 中采用的是位桶 + 链表/红黑树的方式，当某个位桶的链表的长度超过 8 的时候，这个链表就将转换成红黑树</p>
     <p>HashMap 不会因为多线程 put 导致死循环（JDK 8 用 head 和 tail 来保证链表的顺序和之前一样；JDK 7 rehash 会倒置链表元素），但是还会有数据丢失等弊端（并发本身的问题）。因此多线程情况下还是建议使用 ConcurrentHashMap</p>
     <li>为什么线程不安全</li>
     <p>HashMap 在并发时可能出现的问题主要是两方面：<p>
     <ul>如果多个线程同时使用 put 方法添加元素，而且假设正好存在两个 put 的 key 发生了碰撞（根据 hash 值计算的 bucket 一样），那么根据 HashMap 的实现，这两个 key 会添加到数组的同一个位置，这样最终就会发生其中一个线程 put 的数据被覆盖</ul>
     <ul>如果多个线程同时检测到元素个数超过数组大小 * loadFactor，这样就会发生多个线程同时对 Node 数组进行扩容，都在重新计算元素位置以及复制数据，但是最终只有一个线程扩容后的数组会赋给 table，也就是说其他线程的都会丢失，并且各自线程 put 的数据也丢失</ul>
     