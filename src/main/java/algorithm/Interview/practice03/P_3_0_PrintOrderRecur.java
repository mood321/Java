package algorithm.Interview.practice03;

import java.util.Stack;

/**
 * @author mood321
 * @date 2020/4/13 23:47
 * @email 371428187@qq.com
 */
public class P_3_0_PrintOrderRecur {
    public static  class  Node{
      private int data;
      private Node left;
      private Node right;

        public Node(int data) {
            this.data = data;
        }
    }

    // 递归版
    public static void printOrderRecur(Node head){
        if(head==null){
            return;
        }
        System.out.println(head.data+"  ");// 先序
        printOrderRecur(head.left);
       //System.out.println(head.data+"  ");// 中序
        printOrderRecur(head.right);
       // System.out.println(head.data+"  ");// 后序
    }
    // 非递归版   先序
    // 1,先来到父,2,处理,3,栈存右,4来到左,4.1左不为null ,继续1,2,3,4   4.2 为null,从栈弹出一个,继续1,2,3,4  栈空结束
    public static void printPreOrderRecur(Node head) {
        if (head == null) {
            return;
        }
        Stack<Node> nodes = new Stack<>();
        nodes.push(head);
        while(!nodes.isEmpty()){   // 思路同上  但写法简化了
            head = nodes.pop();
            System.out.print(head.data+"  ");
            if(head.right != null){
                nodes.push(head.right);
            }
            if(head.left != null){
                nodes.push(head.left);
            }
        }
        System.out.println();

    }
    // 非递归版 中序
    //  1,先来到父  1.1 左不为空 栈存父,来到左   1.2 左null 处理 ,来到右  2 右不为null 继续,为null 栈弹出
    public static void printInOrderRecur(Node head){
        if(head==null){
            return;
        }
        Stack<Node> stack = new Stack<>();
         while(!stack.isEmpty() || head !=null){   // 写法简化
              if(head !=null){
                  stack.push(head);
                  head=head.left;
              } else {
                  head=stack.pop();
                  System.out.print(head.data+"  ");
                  head=head.right;
              }
         }
        System.out.println();
    }
     // 非递归版 后序
    // 1 根据先序改出来 父->右->左 2 逆序
    public static void printPosOrderRecur(Node head){
        if(head==null){
            return;
        }
        Stack<Node> pre = new Stack<>();
        Stack<Node> temp = new Stack<>();
        pre.push(head);
        while(!pre.isEmpty()){
            head=pre.pop();
            temp.push(head);
            // 此处改写先序 相反
            if(head.left!=null){
                pre.push(head.left);
            }
            if(head.right !=null){
                pre.push(head.left);
            }
        }
        while(!temp.isEmpty()){
            System.out.print(temp.pop().data+"  ");
        }
        System.out.println();
    }
}