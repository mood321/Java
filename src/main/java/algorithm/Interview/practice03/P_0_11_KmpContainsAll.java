package algorithm.Interview.practice03;

/**
 * @author mood321
 * @date 2020/5/13 0:13
 * @email 371428187@qq.com
 */
public class P_0_11_KmpContainsAll {
    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    public static boolean  isContainsAll(Node t1,Node t2){
        String lStr = serialByPre(t1);
        String rStr = serialByPre(t2);     // 序列化
        return    indexOf(lStr,rStr) !=-1;
    }

    /**
     *
     * kmp 大概思路
     *  找到各自最长前缀 最长后缀
     *  通过前缀 后缀去减少次数
     */
    public static int[] getNextArray(char[] s) {
        if (s.length == 1)
            return new int[]{1};

        int[] next = new int[s.length];

        next[0] = -1;
        next[1] = 0;
        int i = 2;    // 走到字符位置
        int cur = 0;  // 默认所在字符最长前缀  但查询的时候 是动态参数
        while (i < s.length) {           //  1 拿到next[i-1]的值 i-1的最长前缀 记为cur  看str[cur] 与str[i] 是否相等
            if (s[i - 1] == s[cur]) {      //  2  相等 next[i]等于 next[i]+1  结束
                next[i++] = ++cur;
            } else {
                if (next[cur] > 0) {     //3 不相等 来到next[next[i]] 记最长前缀所在的字符的位置 记为cur 拿到cur的最长前缀  流程和1,2 一致
                    cur = next[cur];
                } else {        //4 如果一直不等  来到0位置  那next[i]=0
                    next[i++] = 0;
                }
            }

        }
        return next;
    }

    // KMP
    public static int indexOf(String s1, String s2) {
        if (s1 == null || s2 == null || s2.length() > s1.length() || s1.length() == 0 || s2.length() == 0) {
            return -1;
        }
        char[] s1Arr = s1.toCharArray();
        char[] s2Arr = s2.toCharArray();
        int[] nextArray = getNextArray(s2Arr);
        int m1 = 0;           // s1 判断时 所在位置 持续增长
        int m2 = 0;           // s2 判断时 所在位置  处理时动态变化  来到一个元素时  一定他前一个元素的最长前缀的下一个元素
        while (m1 < s1Arr.length && m2 < s2Arr.length) {
            if (s1Arr[m1] == s2Arr[m2]) {     // 判断s1中m1  所在元素 和  s2 中m2 是否相等 相同 都后移
                m1++;
                m2++;
            } else if (nextArray[m2] == -1) {  // 不相等 s2 中m2 在0位置   s1 后移
                m1++;
            } else {                     // 不相等  s2中m2 不在0位置  因为最长前缀和后缀相等  所以此时 s1 中m1 前面字符一定和 s2的m2的最长前缀相等
                // 调整让m2来到 最长前缀的位置  继续while
                m2 = nextArray[m2];
            }
        }
        // 如果m2 来到s2 最后  说明全部匹配   结果就是s1 当前位置m1减去s2的长度(这时长度就是m2)
        //没来到最后  说明没匹配上  -1
        return m2==s2Arr.length?m1-m2:-1;
    }

    private static String serialByPre(Node t1) {
        if(t1 ==null){
            return "#!";
        }
        String res = t1.value + "!";
        res += serialByPre(t1.left);
        res += serialByPre(t1.right);
        return res;
    }
}