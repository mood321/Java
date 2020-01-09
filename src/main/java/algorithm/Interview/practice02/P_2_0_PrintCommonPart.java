package algorithm.Interview.practice02;

import lombok.*;

/**
 * @author mood321
 * @date 2020/1/9 22:02
 * @email 371428187@qq.com
 */
public class P_2_0_PrintCommonPart {
   @Data
    public static  class Node{
       private int data;
       private Node next;

       public Node(int data) {
           this.data = data;
       }
   }

   public  static  void printCommonPart(Node head,Node head2){
       if(head ==null || head2 ==null){
           return;
       }

       while(head != null && head2 != null ){
           if(head.getData() == head2.getData()){
               System.out.println( head.data);
               head= head.next;
               head2=head2.next;

           }   else  if(head.getData() < head2.getData()){
               head=head.getNext();
           }   else {
               head2=head2.next;
           }
       }

   }

    public static void main(String[] args) {
        Node node1 = new Node(2);
        node1.next = new Node(3);
        node1.next.next = new Node(5);
        node1.next.next.next = new Node(6);

        Node node2 = new Node(1);
        node2.next = new Node(2);
        node2.next.next = new Node(5);
        node2.next.next.next = new Node(7);
        node2.next.next.next.next = new Node(8);

      
        printCommonPart(node1, node2);
    }
}