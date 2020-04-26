package algorithm.Interview.practice03;


import java.util.HashMap;
import java.util.Map;

/**
 * @author mood321
 * @date 2020/4/27 0:38
 * @email 371428187@qq.com
 */
public class P_3_7_BstTopoSize {       //bstTopoSize1
    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    public int bstTopoSize1(Node head){
        if(head == null) return 0;
        int max = maxTopo(head,head);
        max = Math.max(bstTopoSize1(head.left), max);
        max = Math.max(bstTopoSize1(head.right), max);
        return max;
    }


    public int maxTopo(Node h,Node n){
        if( h!=null && n!=null && isBSTNode(h,n,n.value)){
            return maxTopo(h, n.left) + maxTopo(h, n.right) + 1;
        }
        return 0;
    }

    //当以h开头的时候，n在不在搜索二叉树上
    public boolean isBSTNode(Node h,Node n,int value){
        if(h == null)//没有在搜索二叉树上找到
            return false;
        if(h == n)//通过左右移动找到
            return true;
        return isBSTNode(h.value>value?h.left:h.right, n, value);
    }

    // 记录拓扑结构   左右子树高度
    public static class Record {
        int l;
        int r;

        public Record(int l, int r) {
            this.l = l;
            this.r = r;
        }
    }
    public static int bstTopoSize2(Node head) {
        Map<Node, Record> map = new HashMap<Node, Record>();
        return posOrder(head, map);
    }

    public static int posOrder(Node h, Map<Node, Record> map) {
        if (h == null) {
            return 0;
        }
        //获取左右子树的
        int ls = posOrder(h.left, map);
        int rs = posOrder(h.right, map);
        //在以value为头的情况下更新
        modifyMap(h.left, h.value, map, true);
        modifyMap(h.right, h.value, map, false);
        //根据记录更新head的数据
        Record lr = map.get(h.left);
        Record rr = map.get(h.right);
        int lbst = lr == null ? 0 : lr.l + lr.r + 1;
        int rbst = rr == null ? 0 : rr.l + rr.r + 1;
        //保存数据
        map.put(h, new Record(lbst, rbst));
        //获取最大的情况
        return Math.max(lbst + rbst + 1, Math.max(ls, rs));
    }
    //根据左右来更新

    public static int modifyMap(Node n, int v, Map<Node, Record> m, boolean s) {
        if (n == null || (!m.containsKey(n))) {
            return 0;
        }
        Record r = m.get(n);
        if ((s && n.value > v) || ((!s) && n.value < v)) {
            m.remove(n);
            return r.l + r.r + 1;
        } else {
            int minus = modifyMap(s ? n.right : n.left, v, m, s);
            if (s) {
                r.r = r.r - minus;
            } else {
                r.l = r.l - minus;
            }
            m.put(n, r);
            return minus;
        }
    }
}