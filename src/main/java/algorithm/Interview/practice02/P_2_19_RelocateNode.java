package algorithm.Interview.practice02;

/**
 * @author mood321
 * @date 2020/4/9 0:40
 * @email 371428187@qq.com
 */
public class P_2_19_RelocateNode {
    public static class Node {
        private Integer data;
        private Node next;

        public Node(Integer data) {
            this.data = data;
        }
    }
    public static  void relocate(Node head){
       if(head ==null || head.next ==null){
           return ;
       }
       Node mid=head;
       Node right=head.next;
       while(right.next !=null && right.next.next!=null){
           mid=mid.next;
           right=right.next.next;
       }
       right=mid.next;
       mid.next=null;
       // 重连
        mergeLR(head,right);

    }

    private static void mergeLR(Node head, Node right) {
        Node next=null;
        while(head !=null){
            next=right.next;
            right.next=head.next;
            head.next=right;
            head=right.next;
            right=next;

        }
        head.next=right;// 
    }
}