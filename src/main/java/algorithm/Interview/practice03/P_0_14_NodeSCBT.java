package algorithm.Interview.practice03;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * @author mood321
 * @date 2020/5/15 0:33
 * @email 371428187@qq.com
 */
public class P_0_14_NodeSCBT {
    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    /**
     * 判断 是不是搜索二叉树
     */
    public static boolean isBST(Node head) {
        if (head == null) {
            return true;
        }
        // 栈遍历
        Stack<Node> stack = new Stack<>();
        Node pre = null;
        while (!stack.isEmpty() || head != null) {
            if (head != null) {
                stack.push(head);
                head = head.left;
            } else {
                head = stack.pop();
                if (pre == null || pre.value < head.value) {
                    pre = head;
                    head = head.right;
                } else {
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * 判断 是不是完全二叉树
     * 层级遍历,左到右,1, 一个节点有右无左,不是 2,一个节点右为空,后面节点都为叶节点,否则 不是, 3 遍历完,没有遇到1,2 是
     */
    public static boolean isCBT(Node head) {
        if (head == null) {
            return true;
        }
        Queue<Node> queue= new LinkedList<>();

        boolean f=false;//  判断2
        Node l=null;
        Node r=null;
        queue.offer(head);
        while(!queue.isEmpty()){
           head= queue.poll();
           l=head.left;
           r=head.right;
           if((l==null && r!=null) || f&& (l!=null)){    //   f&& (l!=null || r!=null)  ???
               return  false;
           }
           if(l != null){
                queue.offer(l);
           }
           if( r != null){
               queue.offer(r);
           }            else {
               f=true;
           }
        }
        return true;
    }
}