<blockquote>
<p>作者：彤哥</p>
<p>出处：<a href="https://www.cnblogs.com/tong-yuan/">https://www.cnblogs.com/tong-yuan/</a></p>
</blockquote>
<hr />
<h2><span id="i">问题</span></h2>
<p>（1）CopyOnWriteArraySet是用Map实现的吗？</p>
<p>（2）CopyOnWriteArraySet是有序的吗？</p>
<p>（3）CopyOnWriteArraySet是并发安全的吗？</p>
<p>（4）CopyOnWriteArraySet以何种方式保证元素不重复？</p>
<p>（5）如何比较两个Set中的元素是否完全一致？</p>
<h2><span id="i-2">简介</span></h2>
<p>CopyOnWriteArraySet底层是使用CopyOnWriteArrayList存储元素的，所以它并不是使用Map来存储元素的。</p>
<p>但是，我们知道CopyOnWriteArrayList底层其实是一个数组，它是允许元素重复的，那么用它来实现CopyOnWriteArraySet怎么保证元素不重复呢？</p>
<p>CopyOnWriteArrayList回顾请点击【<a href="https://mp.weixin.qq.com/s/k03E5KnrhGv-R1AodSutsQ">死磕 java集合之CopyOnWriteArrayList源码分析</a>】。</p>
<h2><span id="i-3">源码分析</span></h2>
<p>Set类的源码一般都比较短，所以我们直接贴源码上来一行一行分析吧。</p>
<p>Set之类的简单源码适合泛读，主要是掌握一些不常见的用法，做到心里有说，坐个车三五分钟可能就看完了。</p>
<p>像ConcurrentHashMap、ConcurrentSkipListMap之类的比较长的我们还是倾向分析主要的方法，适合精读，主要是掌握实现原理以及一些不错的思想，可能需要一两个小时才能看完一整篇文章。</p>
<pre><code class="java">public class CopyOnWriteArraySet&lt;E&gt; extends AbstractSet&lt;E&gt;
        implements java.io.Serializable {
    private static final long serialVersionUID = 5457747651344034263L;
    // 内部使用CopyOnWriteArrayList存储元素
    private final CopyOnWriteArrayList&lt;E&gt; al;
    // 构造方法
    public CopyOnWriteArraySet() {
        al = new CopyOnWriteArrayList&lt;E&gt;();
    }
    // 将集合c中的元素初始化到CopyOnWriteArraySet中
    public CopyOnWriteArraySet(Collection&lt;? extends E&gt; c) {
        if (c.getClass() == CopyOnWriteArraySet.class) {
            // 如果c是CopyOnWriteArraySet类型，说明没有重复元素，
            // 直接调用CopyOnWriteArrayList的构造方法初始化
            @SuppressWarnings("unchecked") CopyOnWriteArraySet&lt;E&gt; cc =
                (CopyOnWriteArraySet&lt;E&gt;)c;
            al = new CopyOnWriteArrayList&lt;E&gt;(cc.al);
        }
        else {
            // 如果c不是CopyOnWriteArraySet类型，说明有重复元素
            // 调用CopyOnWriteArrayList的addAllAbsent()方法初始化
            // 它会把重复元素排除掉
            al = new CopyOnWriteArrayList&lt;E&gt;();
            al.addAllAbsent(c);
        }
    }
    // 获取元素个数
    public int size() {
        return al.size();
    }
    // 检查集合是否为空
    public boolean isEmpty() {
        return al.isEmpty();
    }
    // 检查是否包含某个元素
    public boolean contains(Object o) {
        return al.contains(o);
    }
    // 集合转数组
    public Object[] toArray() {
        return al.toArray();
    }
    // 集合转数组，这里是可能有bug的，详情见ArrayList中分析
    public &lt;T&gt; T[] toArray(T[] a) {
        return al.toArray(a);
    }
    // 清空所有元素
    public void clear() {
        al.clear();
    }
    // 删除元素
    public boolean remove(Object o) {
        return al.remove(o);
    }
    // 添加元素
    // 这里是调用CopyOnWriteArrayList的addIfAbsent()方法
    // 它会检测元素不存在的时候才添加
    // 还记得这个方法吗？当时有分析过的，建议把CopyOnWriteArrayList拿出来再看看
    public boolean add(E e) {
        return al.addIfAbsent(e);
    }
    // 是否包含c中的所有元素
    public boolean containsAll(Collection&lt;?&gt; c) {
        return al.containsAll(c);
    }
    // 并集
    public boolean addAll(Collection&lt;? extends E&gt; c) {
        return al.addAllAbsent(c) &gt; 0;
    }
    // 单方向差集
    public boolean removeAll(Collection&lt;?&gt; c) {
        return al.removeAll(c);
    }
    // 交集
    public boolean retainAll(Collection&lt;?&gt; c) {
        return al.retainAll(c);
    }
    // 迭代器
    public Iterator&lt;E&gt; iterator() {
        return al.iterator();
    }
    // equals()方法
    public boolean equals(Object o) {
        // 如果两者是同一个对象，返回true
        if (o == this)
            return true;
        // 如果o不是Set对象，返回false
        if (!(o instanceof Set))
            return false;
        Set&lt;?&gt; set = (Set&lt;?&gt;)(o);
        Iterator&lt;?&gt; it = set.iterator();
        // 集合元素数组的快照
        Object[] elements = al.getArray();
        int len = elements.length;
        // 我觉得这里的设计不太好
        // 首先，Set中的元素本来就是不重复的，所以不需要再用个matched[]数组记录有没有出现过
        // 其次，两个集合的元素个数如果不相等，那肯定不相等了，这个是不是应该作为第一要素先检查
        boolean[] matched = new boolean[len];
        int k = 0;
        // 从o这个集合开始遍历
        outer: while (it.hasNext()) {
            // 如果k&gt;len了，说明o中元素多了
            if (++k &gt; len)
                return false;
            // 取值
            Object x = it.next();
            // 遍历检查是否在当前集合中
            for (int i = 0; i &lt; len; ++i) {
                if (!matched[i] &amp;&amp; eq(x, elements[i])) {
                    matched[i] = true;
                    continue outer;
                }
            }
            // 如果不在当前集合中，返回false
            return false;
        }
        return k == len;
    }
    // 移除满足过滤条件的元素
    public boolean removeIf(Predicate&lt;? super E&gt; filter) {
        return al.removeIf(filter);
    }
    // 遍历元素
    public void forEach(Consumer&lt;? super E&gt; action) {
        al.forEach(action);
    }
    // 分割的迭代器
    public Spliterator&lt;E&gt; spliterator() {
        return Spliterators.spliterator
            (al.getArray(), Spliterator.IMMUTABLE | Spliterator.DISTINCT);
    }
    // 比较两个元素是否相等
    private static boolean eq(Object o1, Object o2) {
        return (o1 == null) ? o2 == null : o1.equals(o2);
    }
}
</code></pre>
<p>可以看到，在添加元素时调用了CopyOnWriteArrayList的addIfAbsent()方法来保证元素不重复。</p>
<p>还记得这个方法的实现原理吗？点击直达【<a href="https://mp.weixin.qq.com/s/k03E5KnrhGv-R1AodSutsQ">死磕 java集合之CopyOnWriteArrayList源码分析</a>】。</p>
<h2><span id="i-4">总结</span></h2>
<p>（1）CopyOnWriteArraySet是用CopyOnWriteArrayList实现的；</p>
<p>（2）CopyOnWriteArraySet是有序的，因为底层其实是数组，数组是不是有序的？！</p>
<p>（3）CopyOnWriteArraySet是并发安全的，而且实现了读写分离；</p>
<p>（4）CopyOnWriteArraySet通过调用CopyOnWriteArrayList的addIfAbsent()方法来保证元素不重复；</p>
<h2><span id="i-5">彩蛋</span></h2>
<p>（1）如何比较两个Set中的元素是否完全相等？</p>
<p>假设有两个Set，一个是A，一个是B。</p>
<p>最简单的方式就是判断是否A中的元素都在B中，B中的元素是否都在A中，也就是两次两层循环。</p>
<p>其实，并不需要。</p>
<p>因为Set中的元素并不重复，所以只要先比较两个Set的元素个数是否相等，再作一次两层循环就可以了，需要仔细体味。代码如下：</p>
<pre><code class="java">public class CopyOnWriteArraySetTest {
    public static void main(String[] args) {
        Set&lt;Integer&gt; set1 = new CopyOnWriteArraySet&lt;&gt;();
        set1.add(1);
        set1.add(5);
        set1.add(2);
        set1.add(7);
//        set1.add(3);
        set1.add(4);
        Set&lt;Integer&gt; set2 = new HashSet&lt;&gt;();
        set2.add(1);
        set2.add(5);
        set2.add(2);
        set2.add(7);
        set2.add(3);
        System.out.println(eq(set1, set2));
        System.out.println(eq(set2, set1));
    }
    private static &lt;T&gt; boolean eq(Set&lt;T&gt; set1, Set&lt;T&gt; set2) {
        if (set1.size() != set2.size()) {
            return false;
        }
        for (T t : set1) {
            // contains相当于一层for循环
            if (!set2.contains(t)) {
                return false;
            }
        }
        return true;
    }
}
</code></pre>
<p>（2）那么，如何比较两个List中的元素是否完全相等呢？</p>
<p>我们知道，List中元素是可以重复的，那是不是要做两次两层循环呢？</p>
<p>其实，也不需要做两次两层遍历，一次也可以搞定，设定一个标记数组，标记某个位置的元素是否找到过，请仔细体味。代码如下：</p>
<pre><code class="java">public class ListEqTest {
    public static void main(String[] args) {
        List&lt;Integer&gt; list1 = new ArrayList&lt;&gt;();
        list1.add(1);
        list1.add(3);
        list1.add(6);
        list1.add(3);
        list1.add(8);
        list1.add(5);
        List&lt;Integer&gt; list2 = new ArrayList&lt;&gt;();
        list2.add(3);
        list2.add(1);
        list2.add(3);
        list2.add(8);
        list2.add(5);
        list2.add(6);
        System.out.println(eq(list1, list2));
        System.out.println(eq(list2, list1));
    }
    private static &lt;T&gt; boolean eq(List&lt;T&gt; list1, List&lt;T&gt; list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        // 标记某个元素是否找到过，防止重复
        boolean matched[] = new boolean[list2.size()];
        outer: for (T t : list1) {
            for (int i = 0; i &lt; list2.size(); i++) {
                // i这个位置没找到过才比较大小
                if (!matched[i] &amp;&amp; list2.get(i).equals(t)) {
                    matched[i] = true;
                    continue outer;
                }
            }
            return false;
        }
        return true;
    }
}
</code></pre>
<p>这种设计是不是很巧妙？^^</p>
		</article>