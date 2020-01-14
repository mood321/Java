package algorithm.Interview.practice02;

import lombok.Data;

/**
 * @author mood321
 * @date 2020/1/14 19:58
 * @email 371428187@qq.com
 */
public class P_2_2_DeleteNode {

    @Data
    public class Node{
         private int  data;
         private Node next;

        public Node(int data) {
            this.data = data;
        }
    }
    /**
     *  删除 链表中间节点
     */
    public  Node deleteMidNode(Node head){
                if(head ==null || head.next ==null) {
                    return head;
                }
                if(head.next.next==null){
                    return head.next;
                }
                Node pre=head;
                Node cur=   head.next.next;
                while(pre!=null && cur !=null){
                    pre=pre.next;
                   cur=   cur.next.next;
                }
                pre.next=pre.next.next;
        return head;
    }

    /**
     *  删除 a/b 的节点
     */
    public Node removeByRatio(Node head,int a ,int  b){
        if (a<1 || a>b){
            return head;
        }
        int  n=0;
        Node cur=head;
        while (cur!=null){
            n++;
            cur=cur.next;
        }
        n= (int)Math.ceil((a * n) / b);      // 计算是第几个节点
        if(n==1){
            head=head.next;
        }

        if (n>0){
            cur=head;
          while(--n>0){
                 cur=cur.next;
          }
          cur.next=cur.next.next;
        }
        return head;
    }
}