package algorithm.Interview.practice02;

import lombok.Data;

/**
 * @author mood321
 * @date 2020/1/14 21:15
 * @email 371428187@qq.com
 */
public class P_2_4_ReversePart {
    @Data
        public static class  Node{
          private int data;
          private Node next;

            public Node(int data) {
                this.data = data;
            }
        }
        public static   Node  resverPart(Node head,int from,int to){
            int n=0;
            Node  cur=head;
            Node fPre=null;
            Node  lPos=null;
            while (cur!=null){
                n++;
                fPre=n==from-1?cur:fPre;
                lPos=n==to+1?cur:lPos;
                cur=cur.next;
            }
            if(to<from || from< 0 || to> n){
                return head;
            }
            cur=fPre==null ? head:fPre.next;  // 可能从0 开始
            Node node= cur.next;
            cur.next=lPos;
            Node next= null;
            while(node != lPos){
                next=node.next;
                node.next=cur;
                cur=node;
                node=next;
            }
            if(fPre != null){
                fPre.next=cur;
                return head;
            }

            return  cur;
        }

    public static void main(String[] args) {
        Node node1 = new Node(1);
        Node node2 = new Node(2);
        Node node3 = new Node(3);
        Node node4 = new Node(4);
        Node node5 = new Node(5);
        Node node6 = new Node(6);
        Node node7 = new Node(7);
        node1.next=node2;
        node2.next=node3;
        node3.next=node4;
        node4.next=node5;
        node5.next=node6;
        node6.next=node7;
        print(node1);
        System.out.println();
        print(resverPart(node1,2,5));
        
    }

    private static void print(Node node1) {
        while(node1 !=null){
            System.out.print(node1.data+"  ,");
            node1=node1.next;
        }
    }

}