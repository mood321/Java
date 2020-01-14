package algorithm.Interview.practice02;

import lombok.Data;
import lombok.Getter;

/**
 * @author mood321
 * @date 2020/1/14 20:37
 * @email 371428187@qq.com
 */
public class P_2_3_ReverseList {
    @Data
    public  class Node {
        private int  data;
        private  Node  next;

        public Node(int data) {
            this.data = data;
        }
    }
    /**
     *  单链表的反转
     */
    public Node reverseList(Node head){
       if(head ==null || head.next ==null){
           return head;
       }
       Node pre =null;
       Node next = null;
       while(head !=null){
          next= head.next;
          head.next=pre;
          pre=head;
          head=next;
       }
        return pre  ;

    }

    @Data
    public  class DoubleNode{
        private int data;
         private DoubleNode next;
         private DoubleNode last;

        public DoubleNode(int data) {
            this.data = data;
        }
    }
/**
 * 双向链表的反转
 */
public DoubleNode reverseList(DoubleNode head){
    if(head ==null || head.next==null){
        return  head;
    }
    DoubleNode pre=null;
    DoubleNode next=null;
    while (head !=null ){
        next=head.next;
        head.next=pre;
        head.last=next;
        pre=head;
        head=next;

    }
    return  pre;

}
}