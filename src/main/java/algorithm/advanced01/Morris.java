package algorithm.advanced01;

import algorithm.basic04.Code_01_PreInPosTraversal;

/**
 * @author mood321
 * @date 2019/11/25 20:23
 * @email 371428187@qq.com
 * @desc 二叉树的morris遍历
 */
public class Morris {

    public static class Node {

        Integer data;
        Node left;
        Node right;

        public Node(Integer value) {
            data = value;
        }
    }

    public static void morrispre(Node head) {
        if (head == null) {
            return;
        }
        Node cur = head;
        Node morrRight = null;
        while (cur != null) {
            morrRight = cur.left;
            if (morrRight != null) {
                // 左子树不为null  拿到最右叶子节点
                // 叶子节点的右等于null 第一次来   不等第二次来到这个节点
                while (morrRight.right != null && morrRight.right != cur){
                    morrRight= morrRight.right;
                }
                if(morrRight.right==null){       // 第一次

                    System.out.println(cur.data);
                    morrRight.right=cur;
                    cur=cur.left;

                }  else {     //第二次
                    cur=cur .right;
                    morrRight.right=null;
                }

            } else {
                // 左子树为 输出cur 来到右
                System.out.println(cur.data);
                cur = cur.right;
            }
        }

    }   public static void morrisin(Node head) {
        if (head == null) {
            return;
        }
        Node cur = head;
        Node morrRight = null;
        while (cur != null) {
            morrRight = cur.left;
            if (morrRight != null) {
                // 左子树不为null  拿到最右叶子节点
                // 叶子节点的右等于null 第一次来   不等第二次来到这个节点
                while (morrRight.right != null && morrRight.right != cur){
                    morrRight= morrRight.right;
                }
                if(morrRight.right==null){       // 第一次


                    morrRight.right=cur;
                    cur=cur.left;

                }  else {     //第二次
                    System.out.println(cur.data);
                    cur=cur .right;
                    morrRight.right=null;
                }

            } else {
                // 左子树为 输出cur 来到右
                System.out.println(cur.data);
                cur = cur.right;
            }
        }

    }
    public static void morrispos(Node head) {
        if (head == null) {
            return;
        }
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
                    printEdge(cur1.left);
                }
            }
            cur1 = cur1.right;
        }
        printEdge(head);
        System.out.println();
    }

    public static void printEdge(Node head) {
        Node tail = reverseEdge(head);
        Node cur = tail;
        while (cur != null) {
            System.out.print(cur.data + " ");
            cur = cur.right;
        }
        reverseEdge(tail);
    }

    public static Node reverseEdge(Node from) {
        Node pre = null;
        Node next = null;
        while (from != null) {
            next = from.right;
            from.right = pre;
            pre = from;
            from = next;
        }
        return pre;
    }
    public static void main(String[] args) {
        Node head = new Node(5);
        head.left = new Node(3);
        head.right = new Node(8);
        head.left.left = new Node(2);
        head.left.right = new Node(4);
        head.left.left.left = new Node(1);
        head.right.left = new Node(7);
        head.right.left.left = new Node(6);
        head.right.right = new Node(10);
        head.right.right.left = new Node(9);
        head.right.right.right = new Node(11);

        // recursive
        System.out.println("==============recursive==============");
        System.out.print("pre-order: ");
        morrispre(head);
        System.out.println();
        System.out.print("in-order: ");
        morrisin(head);
        System.out.println();
        System.out.print("pos-order: ");
       morrispos(head);
        System.out.println();

      // 前两种在基础04 里面
      

    }

}