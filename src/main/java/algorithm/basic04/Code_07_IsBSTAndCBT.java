package algorithm.basic04;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * @Created by mood321
 * @Date 2019/11/4 0004
 * @Description TODO
 */
public class Code_07_IsBSTAndCBT {
    public static class Node {
        int data;
        Node left;
        Node right;

        public Node(int data) {
            this.data = data;
        }
    }

    // 非递归版本中序遍历
    public static boolean isBST1(Node head) {
        if (head == null) {
            return true;
        }
        Stack<Node> stack = new Stack<>();
        Node pre = new Node(Integer.MIN_VALUE);
        while (!stack.isEmpty() || head != null) {
            if (head != null) {
                stack.push(head);
                head = head.left;
            } else {
                Node pop = stack.pop();
                if (pre.data > pop.data)
                    return false;
                pre = pop;
                head = pop.right;

            }
        }
        return true;
    }

    public static void main(String[] args) {
        Node head = new Node(4);
        head.left = new Node(2);
        head.right = new Node(6);
        head.left.left = new Node(1);
        head.left.right = new Node(3);
        head.right.left = new Node(5);

        /* printTree(head);*/
        System.out.println(isBST(head));//morris 遍历 判断是否是搜索树
        System.out.println(isBST1(head));// 非递归遍历 判断是否是搜索树
        System.out.println(isBST2(head));// 递归遍历 判断是否是搜索树
        System.out.println(isCBT(head));// 判断是否是一个 完全二叉树


    }

    /**
     *  判断时候是完全二叉树
     *   1. 有右子节点 无左子树 一定不是
     *   2. 有左子树 无右子树  接下来所有节点必须是叶子节点 才是完全二叉树
     * @param head
     * @return
     */
    public static boolean isCBT(Node head) {
        if(head==null){
            return true;
        }
        Queue<Node> queue = new LinkedList<>();
      queue.offer(head);
      Node l=null;
      Node r=null;
      //
        boolean flag=false;
        while (!queue.isEmpty()){
             head = queue.poll();
             l=head.left;
             r=head.right;
             // 在第一种情况下 左为空 右不为空   不是
            // 在第二种情况下 已经发生 节点必须是叶子节点  进入这种情况 左子不为空
            if ((flag && (l != null || r != null)) || (l == null && r != null))
                return false;

            // 逻辑与上面相等
            /*if(l == null && r != null)
                return false;
            if(flag){
                if(l != null || r != null)
                    return  false;
            }*/
            //
            if(l!=null)
                queue.offer(l);
            if(r!=null)
                queue.offer(r);
        }

        return true;

    }
    public static boolean isBST(Node head) {
        if (head == null) {
            return true;
        }
        boolean res = true;
        Node pre = null;
        Node cur1 = head;
        Node cur2 = null;
        while (cur1 != null) {
            cur2 = cur1.left;
            if (cur2 != null) {
                while (cur2.right != null && cur2.right != cur1) {
                    cur2 = cur2.right;
                }
                if (cur2.right == null) {
                    cur2.right = cur1;
                    cur1 = cur1.left;
                    continue;
                } else {
                    cur2.right = null;
                }
            }
            if (pre != null && pre.data > cur1.data) {
                res = false;
            }
            pre = cur1;
            cur1 = cur1.right;
        }
        return res;
    }

    // 递归
    private static boolean isBST2(Node head) {
        if (head == null) {
            return true;
        }
        Boolean[] res = {true};
        checkNode(head, head.data, false, res);
        return res[0];
    }

    private static void checkNode(Node head, int minValue, boolean flag, Boolean[] res) {
        if (head == null) {
            return;

        }

        checkNode(head.left, head.data, false, res);
        if (!res[0] ) {
            return;
        }
        if (!flag) {
            if (minValue < head.data)
                res[0] = false;
        } else {
            if (minValue > head.data)
                res[0] = false;
        }

        checkNode(head.right, head.data, true, res);
        if (!res[0] ) {
            return;
        }

    }
}
