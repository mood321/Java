package algorithm.Interview.practice03;

import java.util.Stack;

/**
 * @author mood321
 * @date 2020/4/21 0:01
 * @email 371428187@qq.com
 */
public class P_3_4_Morris {
    public static class Node {

        Integer data;
        Node left;
        Node right;

        public Node(Integer value) {
            data = value;
        }
    }

    /**
     * 逻辑 到一个节点cur时 ,如果有左 ,吧左的最右节点的右子设为cur
     * 来到左,循环
     * 第二次来到cur 和最右的右向等时  表示第二次来到 ,此时来到cur的右子
     *
     * @param head
     */
    public static void morrisPre(Node head) {
        if (head == null) {
            return;
        }
        Node cur = head;
        Node morrisRight = null;
        while (cur != null) {
            morrisRight = cur.left;
            if (morrisRight == null) {// 左子直接为null
                while (morrisRight.right != null && morrisRight.right != cur) {
                    morrisRight = morrisRight.right;
                }
                if (morrisRight.right == null) {    // 第一次来
                    System.out.print(cur.data + " ");
                    morrisRight.right = cur;
                    cur = cur.left;

                }  else {     // 第二次
                    morrisRight.right=null;
                    cur=cur.right;
                }
            } else {
                System.out.print(cur.data + " ");
                cur = cur.right;
            }
        }

    }

    /**
     *  遍历 和上面逻辑一致   但他是第二次来到一个节点  输出
     * @param head
     */
    public  static  void morrisIn(Node head){
        if(head ==null){
            return;
        }
        Node cur=head;
        Node morrisRight=null;
        while(cur !=null){
            morrisRight=cur.left;
            if(morrisRight ==null){
                while(morrisRight.right !=null && morrisRight.right != cur){
                    morrisRight=morrisRight.right;
                }
                if(morrisRight.right != cur) {    // 第一次
                    morrisRight.right=cur;
                    cur=cur.left;

                }    else {
                    System.out.print(cur.data +" ");
                    morrisRight.right=null;
                    cur=cur.right;
                }

            }   else { // 不存在左子

                System.out.print(cur.data+" ");
                cur=cur.right;
            }
        }
        
   }

    /**
     *  遍历还是一致的,
     *  打印时机,有左来到左,
     *  没有左 逆序打印左子树的 右侧,   这样是吧一个二叉树拆成多个 由右侧构成的链表
     *
     *  笔记里面有逆序打印链表 ,这儿用栈吧
     * @param head
     */
   public static  void morrisPos(Node head){
       if (head == null) {
           return;
       }
       Node cur = head;
       Node morrisRight = null;
       Stack<Node> nodes = new Stack<>();
       while (cur != null) {
           morrisRight = cur.left;
           if (morrisRight != null) {
               while (morrisRight.right != null && morrisRight.right != cur) {
                   morrisRight = morrisRight.right;
               }
               if (morrisRight.right == null) {   // 先来到左  结束本次循环  一直到最左
                   morrisRight.right = cur;
                   cur = cur.left;
                   continue;
               } else {
                   morrisRight.right = null;

                   // 用栈实现
                   Node left=cur.left;
                    while(left != null){
                        nodes.push(left);
                        left=left.right;
                    }
                    while (!nodes.isEmpty()){
                        System.out.print(nodes.pop().data+"  ");
                    }
               }
           }
           cur = cur.right;
       }
       Node left=head;
       while(left != null){
           nodes.push(left);
           left=left.right;
       }
       while (!nodes.isEmpty()){
           System.out.print(nodes.pop().data+"  ");
       }
       System.out.println();
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
       // morrispre(head);
        System.out.println();
        System.out.print("in-order: ");
       // morrisin(head);
        System.out.println();
        System.out.print("pos-order: ");
        morrisPos(head);
        System.out.println();
       // pos-order: 1 2 4 3 6 7 9 11 10 8 5
    }
}