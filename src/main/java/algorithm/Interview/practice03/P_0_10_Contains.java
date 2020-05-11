package algorithm.Interview.practice03;

/**
 * @author mood321
 * @date 2020/5/12 0:03
 * @email 371428187@qq.com
 */
public class P_0_10_Contains {
    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    public boolean contaisn(Node t1,Node t2){
        return check(t1,t2) || contaisn(t1.left,t2) ||contaisn(t1.right,t2);  // 双递归
    }

    private boolean check(Node t1, Node t2) {

        if(t2 == null){      // 结束了 t2
            return true;
        }
        if( t1 == null ||  t1.value != t2.value){  // 不相等
            return  false;
        }
        // 走到这儿 是当前节点相等
        return check(t1.left,t2.left) &&     check (t1.right,t2.right);
    }
}