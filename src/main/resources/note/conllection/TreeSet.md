<blockquote>
<p>作者：彤哥</p>
<p>出处：<a href="https://www.cnblogs.com/tong-yuan/">https://www.cnblogs.com/tong-yuan/</a></p>
</blockquote>
<hr />
<h2><span id="i">问题</span></h2>
<p>（1）TreeSet真的是使用TreeMap来存储元素的吗？</p>
<p>（2）TreeSet是有序的吗？</p>
<p>（3）TreeSet和LinkedHashSet有何不同？</p>
<h2><span id="i-2">简介</span></h2>
<p>TreeSet底层是采用TreeMap实现的一种Set，所以它是有序的，同样也是非线程安全的。</p>
<h2><span id="i-3">源码分析</span></h2>
<p>经过前面我们学习HashSet和LinkedHashSet，基本上已经掌握了Set实现的套路了。</p>
<p>所以，也不废话了，直接上源码：</p>
<pre><code class="java">package java.util;

// TreeSet实现了NavigableSet接口，所以它是有序的
public class TreeSet&lt;E&gt; extends AbstractSet&lt;E&gt;
    implements NavigableSet&lt;E&gt;, Cloneable, java.io.Serializable
{
    // 元素存储在NavigableMap中
    // 注意它不一定就是TreeMap
    private transient NavigableMap&lt;E,Object&gt; m;
    // 虚拟元素, 用来作为value存储在map中
    private static final Object PRESENT = new Object();
    // 直接使用传进来的NavigableMap存储元素
    // 这里不是深拷贝,如果外面的map有增删元素也会反映到这里
    // 而且, 这个方法不是public的, 说明只能给同包使用
    TreeSet(NavigableMap&lt;E,Object&gt; m) {
        this.m = m;
    }
    // 使用TreeMap初始化
    public TreeSet() {
        this(new TreeMap&lt;E,Object&gt;());
    }
    // 使用带comparator的TreeMap初始化
    public TreeSet(Comparator&lt;? super E&gt; comparator) {
        this(new TreeMap&lt;&gt;(comparator));
    }
    // 将集合c中的所有元素添加的TreeSet中
    public TreeSet(Collection&lt;? extends E&gt; c) {
        this();
        addAll(c);
    }
    // 将SortedSet中的所有元素添加到TreeSet中
    public TreeSet(SortedSet&lt;E&gt; s) {
        this(s.comparator());
        addAll(s);
    }
    // 迭代器
    public Iterator&lt;E&gt; iterator() {
        return m.navigableKeySet().iterator();
    }
    // 逆序迭代器
    public Iterator&lt;E&gt; descendingIterator() {
        return m.descendingKeySet().iterator();
    }
    // 以逆序返回一个新的TreeSet
    public NavigableSet&lt;E&gt; descendingSet() {
        return new TreeSet&lt;&gt;(m.descendingMap());
    }
    // 元素个数
    public int size() {
        return m.size();
    }
    // 判断是否为空
    public boolean isEmpty() {
        return m.isEmpty();
    }
    // 判断是否包含某元素
    public boolean contains(Object o) {
        return m.containsKey(o);
    }
    // 添加元素, 调用map的put()方法, value为PRESENT
    public boolean add(E e) {
        return m.put(e, PRESENT)==null;
    }
    // 删除元素
    public boolean remove(Object o) {
        return m.remove(o)==PRESENT;
    }
    // 清空所有元素
    public void clear() {
        m.clear();
    }
    // 添加集合c中的所有元素
    public  boolean addAll(Collection&lt;? extends E&gt; c) {
        // 满足一定条件时直接调用TreeMap的addAllForTreeSet()方法添加元素
        if (m.size()==0 &amp;&amp; c.size() &gt; 0 &amp;&amp;
            c instanceof SortedSet &amp;&amp;
            m instanceof TreeMap) {
            SortedSet&lt;? extends E&gt; set = (SortedSet&lt;? extends E&gt;) c;
            TreeMap&lt;E,Object&gt; map = (TreeMap&lt;E, Object&gt;) m;
            Comparator&lt;?&gt; cc = set.comparator();
            Comparator&lt;? super E&gt; mc = map.comparator();
            if (cc==mc || (cc != null &amp;&amp; cc.equals(mc))) {
                map.addAllForTreeSet(set, PRESENT);
                return true;
            }
        }
        // 不满足上述条件, 调用父类的addAll()通过遍历的方式一个一个地添加元素
        return super.addAll(c);
    }
    // 子set（NavigableSet中的方法）
    public NavigableSet&lt;E&gt; subSet(E fromElement, boolean fromInclusive,
                                  E toElement,   boolean toInclusive) {
        return new TreeSet&lt;&gt;(m.subMap(fromElement, fromInclusive,
                                       toElement,   toInclusive));
    }
    // 头set（NavigableSet中的方法）
    public NavigableSet&lt;E&gt; headSet(E toElement, boolean inclusive) {
        return new TreeSet&lt;&gt;(m.headMap(toElement, inclusive));
    }
    // 尾set（NavigableSet中的方法）
    public NavigableSet&lt;E&gt; tailSet(E fromElement, boolean inclusive) {
        return new TreeSet&lt;&gt;(m.tailMap(fromElement, inclusive));
    }
    // 子set（SortedSet接口中的方法）
    public SortedSet&lt;E&gt; subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }
    // 头set（SortedSet接口中的方法）
    public SortedSet&lt;E&gt; headSet(E toElement) {
        return headSet(toElement, false);
    }
    // 尾set（SortedSet接口中的方法）
    public SortedSet&lt;E&gt; tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }
    // 比较器
    public Comparator&lt;? super E&gt; comparator() {
        return m.comparator();
    }
    // 返回最小的元素
    public E first() {
        return m.firstKey();
    }
    // 返回最大的元素
    public E last() {
        return m.lastKey();
    }
    // 返回小于e的最大的元素
    public E lower(E e) {
        return m.lowerKey(e);
    }
    // 返回小于等于e的最大的元素
    public E floor(E e) {
        return m.floorKey(e);
    }
    // 返回大于等于e的最小的元素
    public E ceiling(E e) {
        return m.ceilingKey(e);
    }
    // 返回大于e的最小的元素
    public E higher(E e) {
        return m.higherKey(e);
    }
    // 弹出最小的元素
    public E pollFirst() {
        Map.Entry&lt;E,?&gt; e = m.pollFirstEntry();
        return (e == null) ? null : e.getKey();
    }
    public E pollLast() {
        Map.Entry&lt;E,?&gt; e = m.pollLastEntry();
        return (e == null) ? null : e.getKey();
    }
    // 克隆方法
    @SuppressWarnings("unchecked")
    public Object clone() {
        TreeSet&lt;E&gt; clone;
        try {
            clone = (TreeSet&lt;E&gt;) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
        clone.m = new TreeMap&lt;&gt;(m);
        return clone;
    }
    // 序列化写出方法
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        // Write out any hidden stuff
        s.defaultWriteObject();
        // Write out Comparator
        s.writeObject(m.comparator());
        // Write out size
        s.writeInt(m.size());
        // Write out all elements in the proper order.
        for (E e : m.keySet())
            s.writeObject(e);
    }
    // 序列化写入方法
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        // Read in any hidden stuff
        s.defaultReadObject();
        // Read in Comparator
        @SuppressWarnings("unchecked")
            Comparator&lt;? super E&gt; c = (Comparator&lt;? super E&gt;) s.readObject();
        // Create backing TreeMap
        TreeMap&lt;E,Object&gt; tm = new TreeMap&lt;&gt;(c);
        m = tm;
        // Read in size
        int size = s.readInt();
        tm.readTreeSet(size, s, PRESENT);
    }
    // 可分割的迭代器
    public Spliterator&lt;E&gt; spliterator() {
        return TreeMap.keySpliteratorFor(m);
    }
    // 序列化id
    private static final long serialVersionUID = -2479143000061671589L;
}
</code></pre>
<p>源码比较简单，基本都是调用map相应的方法。</p>
<h2><span id="i-4">总结</span></h2>
<p>（1）TreeSet底层使用NavigableMap存储元素；</p>
<p>（2）TreeSet是有序的；</p>
<p>（3）TreeSet是非线程安全的；</p>
<p>（4）TreeSet实现了NavigableSet接口，而NavigableSet继承自SortedSet接口；</p>
<p>（5）TreeSet实现了SortedSet接口；（彤哥年轻的时候面试被问过TreeSet和SortedSet的区别^^）</p>
<h2><span id="i-5">彩蛋</span></h2>
<p>（1）通过之前的学习，我们知道TreeSet和LinkedHashSet都是有序的，那它们有何不同？</p>
<p>LinkedHashSet并没有实现SortedSet接口，它的有序性主要依赖于LinkedHashMap的有序性，所以它的有序性是指按照插入顺序保证的有序性；</p>
<p>而TreeSet实现了SortedSet接口，它的有序性主要依赖于NavigableMap的有序性，而NavigableMap又继承自SortedMap，这个接口的有序性是指按照key的自然排序保证的有序性，而key的自然排序又有两种实现方式，一种是key实现Comparable接口，一种是构造方法传入Comparator比较器。</p>
<p>（2）TreeSet里面真的是使用TreeMap来存储元素的吗？</p>
<p>通过源码分析我们知道TreeSet里面实际上是使用的NavigableMap来存储元素，虽然大部分时候这个map确实是TreeMap，但不是所有时候都是TreeMap。</p>
<p>因为有一个构造方法是<code>TreeSet(NavigableMap&lt;E,Object&gt; m)</code>，而且这是一个非public方法，通过调用关系我们可以发现这个构造方法都是在自己类中使用的，比如下面这个：</p>
<pre><code class="java">    public NavigableSet&lt;E&gt; tailSet(E fromElement, boolean inclusive) {
        return new TreeSet&lt;&gt;(m.tailMap(fromElement, inclusive));
    }
</code></pre>
<p>而这个m我们姑且认为它是TreeMap，也就是调用TreeMap的tailMap()方法：</p>
<pre><code class="java">    public NavigableMap&lt;K,V&gt; tailMap(K fromKey, boolean inclusive) {
        return new AscendingSubMap&lt;&gt;(this,
                                     false, fromKey, inclusive,
                                     true,  null,    true);
    }
</code></pre>
<p>可以看到，返回的是AscendingSubMap对象，这个类的继承链是怎么样的呢？</p>
<p><img src="https://gitee.com/alan-tang-tt/yuan/raw/master/死磕%20java集合系列/resource/AscendingSubMap.png" alt="AscendingSubMap" /></p>
<p>可以看到，这个类并没有继承TreeMap，不过通过源码分析也可以看出来这个类是组合了TreeMap，也算和TreeMap有点关系，只是不是继承关系。</p>
<p>所以，TreeSet的底层不完全是使用TreeMap来实现的，更准确地说，应该是NavigableMap。</p>
		</article>