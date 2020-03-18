package algorithm.Interview.practice02;

import java.util.Stack;

/**
 * @author mood321
 * @date 2020/3/19 0:18
 * @email 371428187@qq.com
 */
public class P_2_13_RemoveNumNode {
    public static class Node {
        private Integer data;
        private Node next;

        public Node(Integer data) {
            this.data = data;
        }
    }

    /**
     * 方法1   辅助空间
     */
    public static Node removeNumNode1(Node head,int num){
        if(head ==null){
            return  head ;
        }
        Stack<Node> stack = new Stack<>();
        Node cur=head;

        while (cur!=null){
            if(num!= cur.data){
                  stack.push(cur);
            }
            cur=cur.next;
        }

        while (!stack.isEmpty()){
            Node pop = stack.pop();
            pop.next=cur;
            cur=pop;
        }

        return cur;
    }

    /**
     * 方法1   不使用辅助空间
     */
    public static Node removeNumNode2(Node head,int num){
        //先找到不为numm的第一个节点
        while (head !=null){
            if(head.data !=  num){
                break;
            }
            head=head.next;
        }
        Node pre=head;   // 满足条件的
        Node cur=head;   // 原来的
        while (cur != null){
             if( cur.data == num){
                 pre.next=cur.next;
             }                      else {
                 pre=cur;

             }
        cur=cur.next;
        }
        return head;
    }

    public static void main(String[] args) {
        Node node = new Node(1);
        Node node2 = new Node(2);
        Node node3 = new Node(3);
        node.next=node2;
        node2.next=node3;
        print(node);
        print(removeNumNode2(node,3));
       // print(removeNumNode2(node,1));
    }
    private static void print(Node addList1) {
        while(addList1 !=null)       {
            System.out.print(addList1.data+"  ");
            addList1=addList1.next;
        }

    }
}