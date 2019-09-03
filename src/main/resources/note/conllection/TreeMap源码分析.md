<blockquote>
<p>作者：彤哥</p>
<p>出处：<a href="https://www.cnblogs.com/tong-yuan/">https://www.cnblogs.com/tong-yuan/</a></p>
</blockquote>
<hr />
<h2><span id="i">简介</span></h2>
<p>TreeMap使用红黑树存储元素，可以保证元素按key值的大小进行遍历。</p>
<h2><span id="i-2">继承体系</span></h2>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/TreeMap.png" alt="TreeMap" /></p>
<p>TreeMap实现了Map、SortedMap、NavigableMap、Cloneable、Serializable等接口。</p>
<p>SortedMap规定了元素可以按key的大小来遍历，它定义了一些返回部分map的方法。</p>
<pre><code class="java">public interface SortedMap&lt;K,V&gt; extends Map&lt;K,V&gt; {
    // key的比较器
    Comparator&lt;? super K&gt; comparator();
    // 返回fromKey（包含）到toKey（不包含）之间的元素组成的子map
    SortedMap&lt;K,V&gt; subMap(K fromKey, K toKey);
    // 返回小于toKey（不包含）的子map
    SortedMap&lt;K,V&gt; headMap(K toKey);
    // 返回大于等于fromKey（包含）的子map
    SortedMap&lt;K,V&gt; tailMap(K fromKey);
    // 返回最小的key
    K firstKey();
    // 返回最大的key
    K lastKey();
    // 返回key集合
    Set&lt;K&gt; keySet();
    // 返回value集合
    Collection&lt;V&gt; values();
    // 返回节点集合
    Set&lt;Map.Entry&lt;K, V&gt;&gt; entrySet();
}
</code></pre>
<p>NavigableMap是对SortedMap的增强，定义了一些返回离目标key最近的元素的方法。</p>
<pre><code class="java">public interface NavigableMap&lt;K,V&gt; extends SortedMap&lt;K,V&gt; {
    // 小于给定key的最大节点
    Map.Entry&lt;K,V&gt; lowerEntry(K key);
    // 小于给定key的最大key
    K lowerKey(K key);
    // 小于等于给定key的最大节点
    Map.Entry&lt;K,V&gt; floorEntry(K key);
    // 小于等于给定key的最大key
    K floorKey(K key);
    // 大于等于给定key的最小节点
    Map.Entry&lt;K,V&gt; ceilingEntry(K key);
    // 大于等于给定key的最小key
    K ceilingKey(K key);
    // 大于给定key的最小节点
    Map.Entry&lt;K,V&gt; higherEntry(K key);
    // 大于给定key的最小key
    K higherKey(K key);
    // 最小的节点
    Map.Entry&lt;K,V&gt; firstEntry();
    // 最大的节点
    Map.Entry&lt;K,V&gt; lastEntry();
    // 弹出最小的节点
    Map.Entry&lt;K,V&gt; pollFirstEntry();
    // 弹出最大的节点
    Map.Entry&lt;K,V&gt; pollLastEntry();
    // 返回倒序的map
    NavigableMap&lt;K,V&gt; descendingMap();
    // 返回有序的key集合
    NavigableSet&lt;K&gt; navigableKeySet();
    // 返回倒序的key集合
    NavigableSet&lt;K&gt; descendingKeySet();
    // 返回从fromKey到toKey的子map，是否包含起止元素可以自己决定
    NavigableMap&lt;K,V&gt; subMap(K fromKey, boolean fromInclusive,
                             K toKey,   boolean toInclusive);
    // 返回小于toKey的子map，是否包含toKey自己决定
    NavigableMap&lt;K,V&gt; headMap(K toKey, boolean inclusive);
    // 返回大于fromKey的子map，是否包含fromKey自己决定
    NavigableMap&lt;K,V&gt; tailMap(K fromKey, boolean inclusive);
    // 等价于subMap(fromKey, true, toKey, false)
    SortedMap&lt;K,V&gt; subMap(K fromKey, K toKey);
    // 等价于headMap(toKey, false)
    SortedMap&lt;K,V&gt; headMap(K toKey);
    // 等价于tailMap(fromKey, true)
    SortedMap&lt;K,V&gt; tailMap(K fromKey);
}
</code></pre>
<h2><span id="i-3">存储结构</span></h2>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/TreeMap-structure.png" alt="TreeMap-structure" /></p>
<p>TreeMap只使用到了红黑树，所以它的时间复杂度为O(log n)，我们再来回顾一下红黑树的特性。</p>
<p>（1）每个节点或者是黑色，或者是红色。</p>
<p>（2）根节点是黑色。</p>
<p>（3）每个叶子节点（NIL）是黑色。（注意：这里叶子节点，是指为空(NIL或NULL)的叶子节点！）</p>
<p>（4）如果一个节点是红色的，则它的子节点必须是黑色的。</p>
<p>（5）从一个节点到该节点的子孙节点的所有路径上包含相同数目的黑节点。</p>
<h2><span id="i-4">源码解析</span></h2>
<h3><span id="i-5">属性</span></h3>
<pre><code class="java">/**
 * 比较器，如果没传则key要实现Comparable接口
 */
private final Comparator&lt;? super K&gt; comparator;
/**
 * 根节点
 */
private transient Entry&lt;K,V&gt; root;
/**
 * 元素个数
 */
private transient int size = 0;
/**
 * 修改次数
 */
private transient int modCount = 0;
</code></pre>
<p>（1）comparator</p>
<p>按key的大小排序有两种方式，一种是key实现Comparable接口，一种方式通过构造方法传入比较器。</p>
<p>（2）root</p>
<p>根节点，TreeMap没有桶的概念，所有的元素都存储在一颗树中。</p>
<h3><span id="Entry">Entry内部类</span></h3>
<p>存储节点，典型的红黑树结构。</p>
<pre><code class="java">static final class Entry&lt;K,V&gt; implements Map.Entry&lt;K,V&gt; {
    K key;
    V value;
    Entry&lt;K,V&gt; left;
    Entry&lt;K,V&gt; right;
    Entry&lt;K,V&gt; parent;
    boolean color = BLACK;
}
</code></pre>
<h3><span id="i-6">构造方法</span></h3>
<pre><code class="java">/**
 * 默认构造方法，key必须实现Comparable接口
 */
public TreeMap() {
    comparator = null;
}
/**
 * 使用传入的comparator比较两个key的大小
 */
public TreeMap(Comparator&lt;? super K&gt; comparator) {
    this.comparator = comparator;
}
/**
 * key必须实现Comparable接口，把传入map中的所有元素保存到新的TreeMap中
 */
public TreeMap(Map&lt;? extends K, ? extends V&gt; m) {
    comparator = null;
    putAll(m);
}
/**
 * 使用传入map的比较器，并把传入map中的所有元素保存到新的TreeMap中
 */
public TreeMap(SortedMap&lt;K, ? extends V&gt; m) {
    comparator = m.comparator();
    try {
        buildFromSorted(m.size(), m.entrySet().iterator(), null, null);
    } catch (java.io.IOException cannotHappen) {
    } catch (ClassNotFoundException cannotHappen) {
    }
}
</code></pre>
<p>构造方法主要分成两类，一类是使用comparator比较器，一类是key必须实现Comparable接口。</p>
<p>其实，笔者认为这两种比较方式可以合并成一种，当没有传comparator的时候，可以用以下方式来给comparator赋值，这样后续所有的比较操作都可以使用一样的逻辑处理了，而不用每次都检查comparator为空的时候又用Comparable来实现一遍逻辑。</p>
<pre><code class="java">// 如果comparator为空，则key必须实现Comparable接口，所以这里肯定可以强转
// 这样在构造方法中统一替换掉，后续的逻辑就都一致了
comparator = (k1, k2) -&gt; ((Comparable&lt;? super K&gt;)k1).compareTo(k2);
</code></pre>
<h3><span id="getObject_key">get(Object key)方法</span></h3>
<p>获取元素，典型的二叉查找树的查找方法。</p>
<pre><code class="java">public V get(Object key) {
    // 根据key查找元素
    Entry&lt;K,V&gt; p = getEntry(key);
    // 找到了返回value值，没找到返回null
    return (p==null ? null : p.value);
}
final Entry&lt;K,V&gt; getEntry(Object key) {
    // 如果comparator不为空，使用comparator的版本获取元素
    if (comparator != null)
        return getEntryUsingComparator(key);
    // 如果key为空返回空指针异常
    if (key == null)
        throw new NullPointerException();
    // 将key强转为Comparable
    @SuppressWarnings("unchecked")
    Comparable&lt;? super K&gt; k = (Comparable&lt;? super K&gt;) key;
    // 从根元素开始遍历
    Entry&lt;K,V&gt; p = root;
    while (p != null) {
        int cmp = k.compareTo(p.key);
        if (cmp &lt; 0)
            // 如果小于0从左子树查找
            p = p.left;
        else if (cmp &gt; 0)
            // 如果大于0从右子树查找
            p = p.right;
        else
            // 如果相等说明找到了直接返回
            return p;
    }
    // 没找到返回null
    return null;
}
final Entry&lt;K,V&gt; getEntryUsingComparator(Object key) {
    @SuppressWarnings("unchecked")
    K k = (K) key;
    Comparator&lt;? super K&gt; cpr = comparator;
    if (cpr != null) {
        // 从根元素开始遍历
        Entry&lt;K,V&gt; p = root;
        while (p != null) {
            int cmp = cpr.compare(k, p.key);
            if (cmp &lt; 0)
                // 如果小于0从左子树查找
                p = p.left;
            else if (cmp &gt; 0)
                // 如果大于0从右子树查找
                p = p.right;
            else
                // 如果相等说明找到了直接返回
                return p;
        }
    }
    // 没找到返回null
    return null;
}
</code></pre>
<p>（1）从root遍历整个树；</p>
<p>（2）如果待查找的key比当前遍历的key小，则在其左子树中查找；</p>
<p>（3）如果待查找的key比当前遍历的key大，则在其右子树中查找；</p>
<p>（4）如果待查找的key与当前遍历的key相等，则找到了该元素，直接返回；</p>
<p>（5）从这里可以看出是否有comparator分化成了两个方法，但是内部逻辑一模一样，因此可见笔者<code>comparator = (k1, k2) -&gt; ((Comparable&lt;? super K&gt;)k1).compareTo(k2);</code>这种改造的必要性。</p>
<hr />
<p>我是一条美丽的分割线，前方高能，请做好准备。</p>
<hr />
<h3><span id="i-7">特性再回顾</span></h3>
<p>（1）每个节点或者是黑色，或者是红色。</p>
<p>（2）根节点是黑色。</p>
<p>（3）每个叶子节点（NIL）是黑色。（注意：这里叶子节点，是指为空(NIL或NULL)的叶子节点！）</p>
<p>（4）如果一个节点是红色的，则它的子节点必须是黑色的。</p>
<p>（5）从一个节点到该节点的子孙节点的所有路径上包含相同数目的黑节点。</p>
<h3><span id="i-8">左旋</span></h3>
<p>左旋，就是以某个节点为支点向左旋转。</p>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/left-rotation.jpg" alt="left-rotation" /></p>
<p>整个左旋过程如下：</p>
<p>（1）将 y的左节点 设为 x的右节点，即将 β 设为 x的右节点；</p>
<p>（2）将 x 设为 y的左节点的父节点，即将 β的父节点 设为 x；</p>
<p>（3）将 x的父节点 设为 y的父节点；</p>
<p>（4）如果 x的父节点 为空节点，则将y设置为根节点；如果x是它父节点的左（右）节点，则将y设置为x父节点的左（右）节点；</p>
<p>（5）将 x 设为 y的左节点；</p>
<p>（6）将 x的父节点 设为 y；</p>
<p>让我们来看看TreeMap中的实现：</p>
<pre><code class="java">/**
 * 以p为支点进行左旋
 * 假设p为图中的x
 */
private void rotateLeft(Entry&lt;K,V&gt; p) {
    if (p != null) {
        // p的右节点，即y
        Entry&lt;K,V&gt; r = p.right;
        // （1）将 y的左节点 设为 x的右节点
        p.right = r.left;
        // （2）将 x 设为 y的左节点的父节点（如果y的左节点存在的话）
        if (r.left != null)
            r.left.parent = p;
        // （3）将 x的父节点 设为 y的父节点
        r.parent = p.parent;
        // （4）...
        if (p.parent == null)
            // 如果 x的父节点 为空，则将y设置为根节点
            root = r;
        else if (p.parent.left == p)
            // 如果x是它父节点的左节点，则将y设置为x父节点的左节点
            p.parent.left = r;
        else
            // 如果x是它父节点的右节点，则将y设置为x父节点的右节点
            p.parent.right = r;
        // （5）将 x 设为 y的左节点
        r.left = p;
        // （6）将 x的父节点 设为 y
        p.parent = r;
    }
}
</code></pre>
<h3><span id="i-9">右旋</span></h3>
<p>右旋，就是以某个节点为支点向右旋转。</p>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/right-rotation.jpg" alt="right-rotation" /></p>
<p>整个右旋过程如下：</p>
<p>（1）将 x的右节点 设为 y的左节点，即 将 β 设为 y的左节点；</p>
<p>（2）将 y 设为 x的右节点的父节点，即 将 β的父节点 设为 y；</p>
<p>（3）将 y的父节点 设为 x的父节点；</p>
<p>（4）如果 y的父节点 是 空节点，则将x设为根节点；如果y是它父节点的左（右）节点，则将x设为y的父节点的左（右）节点；</p>
<p>（5）将 y 设为 x的右节点；</p>
<p>（6）将 y的父节点 设为 x；</p>
<p>让我们来看看TreeMap中的实现：</p>
<pre><code class="java">/**
 * 以p为支点进行右旋
 * 假设p为图中的y
 */
private void rotateRight(Entry&lt;K,V&gt; p) {
    if (p != null) {
        // p的左节点，即x
        Entry&lt;K,V&gt; l = p.left;
        // （1）将 x的右节点 设为 y的左节点
        p.left = l.right;
        // （2）将 y 设为 x的右节点的父节点（如果x有右节点的话）
        if (l.right != null) l.right.parent = p;
        // （3）将 y的父节点 设为 x的父节点
        l.parent = p.parent;
        // （4）...
        if (p.parent == null)
            // 如果 y的父节点 是 空节点，则将x设为根节点
            root = l;
        else if (p.parent.right == p)
            // 如果y是它父节点的右节点，则将x设为y的父节点的右节点
            p.parent.right = l;
        else
            // 如果y是它父节点的左节点，则将x设为y的父节点的左节点
            p.parent.left = l;
        // （5）将 y 设为 x的右节点
        l.right = p;
        // （6）将 y的父节点 设为 x
        p.parent = l;
    }
}
</code></pre>
<p>未完待续，下一节我们一起探讨红黑树插入元素的操作。</p>
		</article>

<h3><span id="i">插入元素</span></h3>
<p>插入元素，如果元素在树中存在，则替换value；如果元素不存在，则插入到对应的位置，再平衡树。</p>
<pre><code class="java">public V put(K key, V value) {
    Entry&lt;K,V&gt; t = root;
    if (t == null) {
        // 如果没有根节点，直接插入到根节点
        compare(key, key); // type (and possibly null) check
        root = new Entry&lt;&gt;(key, value, null);
        size = 1;
        modCount++;
        return null;
    }
    // key比较的结果
    int cmp;
    // 用来寻找待插入节点的父节点
    Entry&lt;K,V&gt; parent;
    // 根据是否有comparator使用不同的分支
    Comparator&lt;? super K&gt; cpr = comparator;
    if (cpr != null) {
        // 如果使用的是comparator方式，key值可以为null，只要在comparator.compare()中允许即可
        // 从根节点开始遍历寻找
        do {
            parent = t;
            cmp = cpr.compare(key, t.key);
            if (cmp &lt; 0)
                // 如果小于0从左子树寻找
                t = t.left;
            else if (cmp &gt; 0)
                // 如果大于0从右子树寻找
                t = t.right;
            else
                // 如果等于0，说明插入的节点已经存在了，直接更换其value值并返回旧值
                return t.setValue(value);
        } while (t != null);
    }
    else {
        // 如果使用的是Comparable方式，key不能为null
        if (key == null)
            throw new NullPointerException();
        @SuppressWarnings("unchecked")
        Comparable&lt;? super K&gt; k = (Comparable&lt;? super K&gt;) key;
        // 从根节点开始遍历寻找
        do {
            parent = t;
            cmp = k.compareTo(t.key);
            if (cmp &lt; 0)
                // 如果小于0从左子树寻找
                t = t.left;
            else if (cmp &gt; 0)
                // 如果大于0从右子树寻找
                t = t.right;
            else
                // 如果等于0，说明插入的节点已经存在了，直接更换其value值并返回旧值
                return t.setValue(value);
        } while (t != null);
    }
    // 如果没找到，那么新建一个节点，并插入到树中
    Entry&lt;K,V&gt; e = new Entry&lt;&gt;(key, value, parent);
    if (cmp &lt; 0)
        // 如果小于0插入到左子节点
        parent.left = e;
    else
        // 如果大于0插入到右子节点
        parent.right = e;
    // 插入之后的平衡
    fixAfterInsertion(e);
    // 元素个数加1（不需要扩容）
    size++;
    // 修改次数加1
    modCount++;
    // 如果插入了新节点返回空
    return null;
}
</code></pre>
<h3><span id="i-2">插入再平衡</span></h3>
<p>插入的元素默认都是红色，因为插入红色元素只违背了第4条特性，那么我们只要根据这个特性来平衡就容易多了。</p>
<p>根据不同的情况有以下几种处理方式：</p>
<ol>
<li>
<p>插入的元素如果是根节点，则直接涂成黑色即可，不用平衡；</p>
</li>
<li>
<p>插入的元素的父节点如果为黑色，不需要平衡；</p>
</li>
<li>
<p>插入的元素的父节点如果为红色，则违背了特性4，需要平衡，平衡时又分成下面三种情况：</p>
</li>
</ol>
<p><strong>（如果父节点是祖父节点的左节点）</strong></p>
<table>
<thead>
<tr>
<th>情况</th>
<th>策略</th>
</tr>
</thead>
<tbody>
<tr>
<td>1）父节点为红色，叔叔节点也为红色</td>
<td>（1）将父节点设为黑色；<br />（2）将叔叔节点设为黑色；<br />（3）将祖父节点设为红色；<br />（4）将祖父节点设为新的当前节点，进入下一次循环判断；</td>
</tr>
<tr>
<td>2）父节点为红色，叔叔节点为黑色，且当前节点是其父节点的右节点</td>
<td>（1）将父节点作为新的当前节点；<br />（2）以新当节点为支点进行左旋，进入情况3）；</td>
</tr>
<tr>
<td>3）父节点为红色，叔叔节点为黑色，且当前节点是其父节点的左节点</td>
<td>（1）将父节点设为黑色；<br />（2）将祖父节点设为红色；<br />（3）以祖父节点为支点进行右旋，进入下一次循环判断；</td>
</tr>
</tbody>
</table>
<p><strong>（如果父节点是祖父节点的右节点，则正好与上面反过来）</strong></p>
<table>
<thead>
<tr>
<th>情况</th>
<th>策略</th>
</tr>
</thead>
<tbody>
<tr>
<td>1）父节点为红色，叔叔节点也为红色</td>
<td>（1）将父节点设为黑色；<br />（2）将叔叔节点设为黑色；<br />（3）将祖父节点设为红色；<br />（4）将祖父节点设为新的当前节点，进入下一次循环判断；</td>
</tr>
<tr>
<td>2）父节点为红色，叔叔节点为黑色，且当前节点是其父节点的左节点</td>
<td>（1）将父节点作为新的当前节点；<br />（2）以新当节点为支点进行右旋；</td>
</tr>
<tr>
<td>3）父节点为红色，叔叔节点为黑色，且当前节点是其父节点的右节点</td>
<td>（1）将父节点设为黑色；<br />（2）将祖父节点设为红色；<br />（3）以祖父节点为支点进行左旋，进入下一次循环判断；</td>
</tr>
</tbody>
</table>
<p>让我们来看看TreeMap中的实现：</p>
<pre><code class="java">/**
 * 插入再平衡
 *（1）每个节点或者是黑色，或者是红色。
 *（2）根节点是黑色。
 *（3）每个叶子节点（NIL）是黑色。（注意：这里叶子节点，是指为空(NIL或NULL)的叶子节点！）
 *（4）如果一个节点是红色的，则它的子节点必须是黑色的。
 *（5）从一个节点到该节点的子孙节点的所有路径上包含相同数目的黑节点。
 */
private void fixAfterInsertion(Entry&lt;K,V&gt; x) {
    // 插入的节点为红节点，x为当前节点
    x.color = RED;
    // 只有当插入节点不是根节点且其父节点为红色时才需要平衡（违背了特性4）
    while (x != null &amp;&amp; x != root &amp;&amp; x.parent.color == RED) {
        if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
            // a）如果父节点是祖父节点的左节点
            // y为叔叔节点
            Entry&lt;K,V&gt; y = rightOf(parentOf(parentOf(x)));
            if (colorOf(y) == RED) {
                // 情况1）如果叔叔节点为红色
                // （1）将父节点设为黑色
                setColor(parentOf(x), BLACK);
                // （2）将叔叔节点设为黑色
                setColor(y, BLACK);
                // （3）将祖父节点设为红色
                setColor(parentOf(parentOf(x)), RED);
                // （4）将祖父节点设为新的当前节点
                x = parentOf(parentOf(x));
            } else {
                // 如果叔叔节点为黑色
                // 情况2）如果当前节点为其父节点的右节点
                if (x == rightOf(parentOf(x))) {
                    // （1）将父节点设为当前节点
                    x = parentOf(x);
                    // （2）以新当前节点左旋
                    rotateLeft(x);
                }
                // 情况3）如果当前节点为其父节点的左节点（如果是情况2）则左旋之后新当前节点正好为其父节点的左节点了）
                // （1）将父节点设为黑色
                setColor(parentOf(x), BLACK);
                // （2）将祖父节点设为红色
                setColor(parentOf(parentOf(x)), RED);
                // （3）以祖父节点为支点进行右旋
                rotateRight(parentOf(parentOf(x)));
            }
        } else {
            // b）如果父节点是祖父节点的右节点
            // y是叔叔节点
            Entry&lt;K,V&gt; y = leftOf(parentOf(parentOf(x)));
            if (colorOf(y) == RED) {
                // 情况1）如果叔叔节点为红色
                // （1）将父节点设为黑色
                setColor(parentOf(x), BLACK);
                // （2）将叔叔节点设为黑色
                setColor(y, BLACK);
                // （3）将祖父节点设为红色
                setColor(parentOf(parentOf(x)), RED);
                // （4）将祖父节点设为新的当前节点
                x = parentOf(parentOf(x));
            } else {
                // 如果叔叔节点为黑色
                // 情况2）如果当前节点为其父节点的左节点
                if (x == leftOf(parentOf(x))) {
                    // （1）将父节点设为当前节点
                    x = parentOf(x);
                    // （2）以新当前节点右旋
                    rotateRight(x);
                }
                // 情况3）如果当前节点为其父节点的右节点（如果是情况2）则右旋之后新当前节点正好为其父节点的右节点了）
                // （1）将父节点设为黑色
                setColor(parentOf(x), BLACK);
                // （2）将祖父节点设为红色
                setColor(parentOf(parentOf(x)), RED);
                // （3）以祖父节点为支点进行左旋
                rotateLeft(parentOf(parentOf(x)));
            }
        }
    }
    // 平衡完成后将根节点设为黑色
    root.color = BLACK;
}
</code></pre>
<h3><span id="i-3">插入元素举例</span></h3>
<p>我们依次向红黑树中插入 4、2、3 三个元素，来一起看看整个红黑树平衡的过程。</p>
<p>三个元素都插入完成后，符合父节点是祖父节点的左节点，叔叔节点为黑色，且当前节点是其父节点的右节点，即情况2）。</p>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/treemap1.png" alt="1" /></p>
<p>情况2）需要做以下两步处理：</p>
<p>（1）将父节点作为新的当前节点；</p>
<p>（2）以新当节点为支点进行左旋，进入情况3）；</p>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/treemap2.png" alt="2" /></p>
<p>情况3）需要做以下三步处理：</p>
<p>（1）将父节点设为黑色；</p>
<p>（2）将祖父节点设为红色；</p>
<p>（3）以祖父节点为支点进行右旋，进入下一次循环判断；</p>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/treemap3.png" alt="3" /></p>
<p>下一次循环不符合父节点为红色了，退出循环，插入再平衡完成。</p>
<hr />
<p>未完待续，下一节我们一起探讨红黑树删除元素的操作。</p>
		</article>
		
		
<h3><span id="i">二叉树的遍历</span></h3>
<p>我们知道二叉查找树的遍历有前序遍历、中序遍历、后序遍历。</p>
<p>（1）前序遍历，先遍历我，再遍历我的左子节点，最后遍历我的右子节点；</p>
<p>（2）中序遍历，先遍历我的左子节点，再遍历我，最后遍历我的右子节点；</p>
<p>（3）后序遍历，先遍历我的左子节点，再遍历我的右子节点，最后遍历我；</p>
<p>这里的前中后都是以“我”的顺序为准的，我在前就是前序遍历，我在中就是中序遍历，我在后就是后序遍历。</p>
<p>下面让我们看看经典的中序遍历是怎么实现的：</p>
<pre><code class="java">public class TreeMapTest {
    public static void main(String[] args) {
        // 构建一颗10个元素的树
        TreeNode&lt;Integer&gt; node = new TreeNode&lt;&gt;(1, null).insert(2)
                .insert(6).insert(3).insert(5).insert(9)
                .insert(7).insert(8).insert(4).insert(10);
        // 中序遍历，打印结果为1到10的顺序
        node.root().inOrderTraverse();
    }
}
/**
 * 树节点，假设不存在重复元素
 * @param &lt;T&gt;
 */
class TreeNode&lt;T extends Comparable&lt;T&gt;&gt; {
    T value;
    TreeNode&lt;T&gt; parent;
    TreeNode&lt;T&gt; left, right;
    public TreeNode(T value, TreeNode&lt;T&gt; parent) {
        this.value = value;
        this.parent = parent;
    }
    /**
     * 获取根节点
     */
    TreeNode&lt;T&gt; root() {
        TreeNode&lt;T&gt; cur = this;
        while (cur.parent != null) {
            cur = cur.parent;
        }
        return cur;
    }
    /**
     * 中序遍历
     */
    void inOrderTraverse() {
        if(this.left != null) this.left.inOrderTraverse();
        System.out.println(this.value);
        if(this.right != null) this.right.inOrderTraverse();
    }
    /**
     * 经典的二叉树插入元素的方法
     */
    TreeNode&lt;T&gt; insert(T value) {
        // 先找根元素
        TreeNode&lt;T&gt; cur = root();
        TreeNode&lt;T&gt; p;
        int dir;
        // 寻找元素应该插入的位置
        do {
            p = cur;
            if ((dir=value.compareTo(p.value)) &lt; 0) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        } while (cur != null);
        // 把元素放到找到的位置
        if (dir &lt; 0) {
            p.left = new TreeNode&lt;&gt;(value, p);
            return p.left;
        } else {
            p.right = new TreeNode&lt;&gt;(value, p);
            return p.right;
        }
    }
}
</code></pre>
<h3><span id="TreeMap">TreeMap的遍历</span></h3>
<p>从上面二叉树的遍历我们很明显地看到，它是通过递归的方式实现的，但是递归会占用额外的空间，直接到线程栈整个释放掉才会把方法中申请的变量销毁掉，所以当元素特别多的时候是一件很危险的事。</p>
<p>（上面的例子中，没有申请额外的空间，如果有声明变量，则可以理解为直到方法完成才会销毁变量）</p>
<p>那么，有没有什么方法不用递归呢？</p>
<p>让我们来看看java中的实现：</p>
<pre><code class="java">@Override
public void forEach(BiConsumer&lt;? super K, ? super V&gt; action) {
    Objects.requireNonNull(action);
    // 遍历前的修改次数
    int expectedModCount = modCount;
    // 执行遍历，先获取第一个元素的位置，再循环遍历后继节点
    for (Entry&lt;K, V&gt; e = getFirstEntry(); e != null; e = successor(e)) {
        // 执行动作
        action.accept(e.key, e.value);
        // 如果发现修改次数变了，则抛出异常
        if (expectedModCount != modCount) {
            throw new ConcurrentModificationException();
        }
    }
}
</code></pre>
<p>是不是很简单？！</p>
<p>（1）寻找第一个节点；</p>
<p>从根节点开始找最左边的节点，即最小的元素。</p>
<pre><code class="java">    final Entry&lt;K,V&gt; getFirstEntry() {
        Entry&lt;K,V&gt; p = root;
        // 从根节点开始找最左边的节点，即最小的元素
        if (p != null)
            while (p.left != null)
                p = p.left;
        return p;
    }
</code></pre>
<p>（2）循环遍历后继节点；</p>
<p>寻找后继节点这个方法我们在删除元素的时候也用到过，当时的场景是有右子树，则从其右子树中寻找最小的节点。</p>
<pre><code class="java">static &lt;K,V&gt; TreeMap.Entry&lt;K,V&gt; successor(Entry&lt;K,V&gt; t) {
    if (t == null)
        // 如果当前节点为空，返回空
        return null;
    else if (t.right != null) {
        // 如果当前节点有右子树，取右子树中最小的节点
        Entry&lt;K,V&gt; p = t.right;
        while (p.left != null)
            p = p.left;
        return p;
    } else {
        // 如果当前节点没有右子树
        // 如果当前节点是父节点的左子节点，直接返回父节点
        // 如果当前节点是父节点的右子节点，一直往上找，直到找到一个祖先节点是其父节点的左子节点为止，返回这个祖先节点的父节点
        Entry&lt;K,V&gt; p = t.parent;
        Entry&lt;K,V&gt; ch = t;
        while (p != null &amp;&amp; ch == p.right) {
            ch = p;
            p = p.parent;
        }
        return p;
    }
}
</code></pre>
<p>让我们一起来分析下这种方式的时间复杂度吧。</p>
<p>首先，寻找第一个元素，因为红黑树是接近平衡的二叉树，所以找最小的节点，相当于是从顶到底了，时间复杂度为O(log n)；</p>
<p>其次，寻找后继节点，因为红黑树插入元素的时候会自动平衡，最坏的情况就是寻找右子树中最小的节点，时间复杂度为O(log k)，k为右子树元素个数；</p>
<p>最后，需要遍历所有元素，时间复杂度为O(n)；</p>
<p>所以，总的时间复杂度为 O(log n) + O(n * log k) ≈ O(n)。</p>
<p>虽然遍历红黑树的时间复杂度是O(n)，但是它实际是要比跳表要慢一点的，啥？跳表是啥？安心，后面会讲到跳表的。</p>
<h2><span id="i-2">总结</span></h2>
<p>到这里红黑树就整个讲完了，让我们再回顾下红黑树的特性：</p>
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
<p>（7）欢迎补充&#8230;</p>
<h2><span id="i-3">带详细注释的源码地址</span></h2>
<p><a href="https://github.com/alan-tang-tt/yuan/blob/master/%E6%AD%BB%E7%A3%95%20java%E9%9B%86%E5%90%88%E7%B3%BB%E5%88%97/code/TreeMap.java">微信用户请“阅读原文”，进入仓库查看，其它渠道直接点击此链接即可跳转。</a></p>
<h2><span id="i-4">彩蛋</span></h2>
<p>上面我们说到的删除元素的时候，如果当前节点有右子树，则从右子树中寻找最小元素所在的位置，把这个位置的元素放到当前位置，再把删除的位置移到那个位置，再看有没有替代元素，balabala。</p>
<p>那么，除了这种方式，还有没有其它方式呢？</p>
<p>答案当然是肯定的。</p>
<p>上面我们说的红黑树的插入元素、删除元素的过程都是标准的红黑树是那么干的，其实也不一定要完全那么做。</p>
<p>比如说，删除元素，如果当前节点有左子树，那么，我们可以找左子树中最大元素的位置，然后把这个位置的元素放到当前节点，再把删除的位置移到那个位置，再看有没有替代元素，balabala。</p>
<p>举例说明，比如下面这颗红黑树：</p>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/treemap-other1.png" alt="treemap-other1" /></p>
<p>我们删除10这个元素，从左子树中找最大的，找到了9这个元素，那么把9放到10的位置，然后把删除的位置移到原来9的位置，发现不需要作平衡（红+黑节点），直接把这个位置删除就可以了。</p>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/treemap-other2.png" alt="treemap-other2" /></p>
<p>同样是满足红黑树的特性的。</p>
<p>所以，死读书不如无书，学习的过程也是一个不断重塑知识的过程。</p>
		</article>