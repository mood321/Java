<blockquote>
<p>作者：彤哥</p>
<p>出处：<a href="https://www.cnblogs.com/tong-yuan/">https://www.cnblogs.com/tong-yuan/</a></p>
</blockquote>
<hr />
<h2><span id="i">问题</span></h2>
<p>（1）ConcurrentSkipListSet的底层是ConcurrentSkipListMap吗？</p>
<p>（2）ConcurrentSkipListSet是线程安全的吗？</p>
<p>（3）ConcurrentSkipListSet是有序的吗？</p>
<p>（4）ConcurrentSkipListSet和之前讲的Set有何不同？</p>
<h2><span id="i-2">简介</span></h2>
<p>ConcurrentSkipListSet底层是通过ConcurrentNavigableMap来实现的，它是一个有序的线程安全的集合。</p>
<h2><span id="i-3">源码分析</span></h2>
<p>它的源码比较简单，跟通过Map实现的Set基本是一致，只是多了一些取最近的元素的方法。</p>
<p>为了保持专栏的完整性，我还是贴一下源码，最后会对Set的整个家族作一个对比，有兴趣的可以直接拉到最下面。</p>
<pre><code class="java">// 实现了NavigableSet接口，并没有所谓的ConcurrentNavigableSet接口
public class ConcurrentSkipListSet&lt;E&gt;
    extends AbstractSet&lt;E&gt;
    implements NavigableSet&lt;E&gt;, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = -2479143111061671589L;
    // 存储使用的map
    private final ConcurrentNavigableMap&lt;E,Object&gt; m;
    // 初始化
    public ConcurrentSkipListSet() {
        m = new ConcurrentSkipListMap&lt;E,Object&gt;();
    }
    // 传入比较器
    public ConcurrentSkipListSet(Comparator&lt;? super E&gt; comparator) {
        m = new ConcurrentSkipListMap&lt;E,Object&gt;(comparator);
    }
    // 使用ConcurrentSkipListMap初始化map
    // 并将集合c中所有元素放入到map中
    public ConcurrentSkipListSet(Collection&lt;? extends E&gt; c) {
        m = new ConcurrentSkipListMap&lt;E,Object&gt;();
        addAll(c);
    }
    // 使用ConcurrentSkipListMap初始化map
    // 并将有序Set中所有元素放入到map中
    public ConcurrentSkipListSet(SortedSet&lt;E&gt; s) {
        m = new ConcurrentSkipListMap&lt;E,Object&gt;(s.comparator());
        addAll(s);
    }
    // ConcurrentSkipListSet类内部返回子set时使用的
    ConcurrentSkipListSet(ConcurrentNavigableMap&lt;E,Object&gt; m) {
        this.m = m;
    }
    // 克隆方法
    public ConcurrentSkipListSet&lt;E&gt; clone() {
        try {
            @SuppressWarnings("unchecked")
            ConcurrentSkipListSet&lt;E&gt; clone =
                (ConcurrentSkipListSet&lt;E&gt;) super.clone();
            clone.setMap(new ConcurrentSkipListMap&lt;E,Object&gt;(m));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
    /* ---------------- Set operations -------------- */
    // 返回元素个数
    public int size() {
        return m.size();
    }
    // 检查是否为空
    public boolean isEmpty() {
        return m.isEmpty();
    }
    // 检查是否包含某个元素
    public boolean contains(Object o) {
        return m.containsKey(o);
    }
    // 添加一个元素
    // 调用map的putIfAbsent()方法
    public boolean add(E e) {
        return m.putIfAbsent(e, Boolean.TRUE) == null;
    }
    // 移除一个元素
    public boolean remove(Object o) {
        return m.remove(o, Boolean.TRUE);
    }
    // 清空所有元素
    public void clear() {
        m.clear();
    }
    // 迭代器
    public Iterator&lt;E&gt; iterator() {
        return m.navigableKeySet().iterator();
    }
    // 降序迭代器
    public Iterator&lt;E&gt; descendingIterator() {
        return m.descendingKeySet().iterator();
    }
    /* ---------------- AbstractSet Overrides -------------- */
    // 比较相等方法
    public boolean equals(Object o) {
        // Override AbstractSet version to avoid calling size()
        if (o == this)
            return true;
        if (!(o instanceof Set))
            return false;
        Collection&lt;?&gt; c = (Collection&lt;?&gt;) o;
        try {
            // 这里是通过两次两层for循环来比较
            // 这里是有很大优化空间的，参考上篇文章CopyOnWriteArraySet中的彩蛋
            return containsAll(c) &amp;&amp; c.containsAll(this);
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }
    }
    // 移除集合c中所有元素
    public boolean removeAll(Collection&lt;?&gt; c) {
        // Override AbstractSet version to avoid unnecessary call to size()
        boolean modified = false;
        for (Object e : c)
            if (remove(e))
                modified = true;
        return modified;
    }
    /* ---------------- Relational operations -------------- */
    // 小于e的最大元素
    public E lower(E e) {
        return m.lowerKey(e);
    }
    // 小于等于e的最大元素
    public E floor(E e) {
        return m.floorKey(e);
    }
    // 大于等于e的最小元素
    public E ceiling(E e) {
        return m.ceilingKey(e);
    }
    // 大于e的最小元素
    public E higher(E e) {
        return m.higherKey(e);
    }
    // 弹出最小的元素
    public E pollFirst() {
        Map.Entry&lt;E,Object&gt; e = m.pollFirstEntry();
        return (e == null) ? null : e.getKey();
    }
    // 弹出最大的元素
    public E pollLast() {
        Map.Entry&lt;E,Object&gt; e = m.pollLastEntry();
        return (e == null) ? null : e.getKey();
    }
    /* ---------------- SortedSet operations -------------- */
    // 取比较器
    public Comparator&lt;? super E&gt; comparator() {
        return m.comparator();
    }
    // 最小的元素
    public E first() {
        return m.firstKey();
    }
    // 最大的元素
    public E last() {
        return m.lastKey();
    }
    // 取两个元素之间的子set
    public NavigableSet&lt;E&gt; subSet(E fromElement,
                                  boolean fromInclusive,
                                  E toElement,
                                  boolean toInclusive) {
        return new ConcurrentSkipListSet&lt;E&gt;
            (m.subMap(fromElement, fromInclusive,
                      toElement,   toInclusive));
    }
    // 取头子set
    public NavigableSet&lt;E&gt; headSet(E toElement, boolean inclusive) {
        return new ConcurrentSkipListSet&lt;E&gt;(m.headMap(toElement, inclusive));
    }
    // 取尾子set
    public NavigableSet&lt;E&gt; tailSet(E fromElement, boolean inclusive) {
        return new ConcurrentSkipListSet&lt;E&gt;(m.tailMap(fromElement, inclusive));
    }
    // 取子set，包含from，不包含to
    public NavigableSet&lt;E&gt; subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }
    // 取头子set，不包含to
    public NavigableSet&lt;E&gt; headSet(E toElement) {
        return headSet(toElement, false);
    }
    // 取尾子set，包含from
    public NavigableSet&lt;E&gt; tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }
    // 降序set
    public NavigableSet&lt;E&gt; descendingSet() {
        return new ConcurrentSkipListSet&lt;E&gt;(m.descendingMap());
    }
    // 可分割的迭代器
    @SuppressWarnings("unchecked")
    public Spliterator&lt;E&gt; spliterator() {
        if (m instanceof ConcurrentSkipListMap)
            return ((ConcurrentSkipListMap&lt;E,?&gt;)m).keySpliterator();
        else
            return (Spliterator&lt;E&gt;)((ConcurrentSkipListMap.SubMap&lt;E,?&gt;)m).keyIterator();
    }
    // 原子更新map，给clone方法使用
    private void setMap(ConcurrentNavigableMap&lt;E,Object&gt; map) {
        UNSAFE.putObjectVolatile(this, mapOffset, map);
    }
    // 原子操作相关内容
    private static final sun.misc.Unsafe UNSAFE;
    private static final long mapOffset;
    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class&lt;?&gt; k = ConcurrentSkipListSet.class;
            mapOffset = UNSAFE.objectFieldOffset
                (k.getDeclaredField("m"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
</code></pre>
<p>可以看到，ConcurrentSkipListSet基本上都是使用ConcurrentSkipListMap实现的，虽然取子set部分是使用ConcurrentSkipListMap中的内部类，但是这些内部类其实也是和ConcurrentSkipListMap相关的，它们返回ConcurrentSkipListMap的一部分数据。</p>
<p>另外，这里的equals()方法实现的相当敷衍，有很大的优化空间，作者这样实现，应该也是知道几乎没有人来调用equals()方法吧。</p>
<h2><span id="i-4">总结</span></h2>
<p>（1）ConcurrentSkipListSet底层是使用ConcurrentNavigableMap实现的；</p>
<p>（2）ConcurrentSkipListSet有序的，基于元素的自然排序或者通过比较器确定的顺序；</p>
<p>（3）ConcurrentSkipListSet是线程安全的；</p>
<h2><span id="i-5">彩蛋</span></h2>
<p>Set大汇总：</p>
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
		</article>