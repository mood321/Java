package algorithm.Interview.practice02;

import java.util.Stack;

/**
 * @author mood321
 * @date 2020/3/17 0:06
 * @email 371428187@qq.com
 */
public class P_2_11_ReverseKNode {
    public static class  Node{
        private Integer data;
        private Node next;

        public Node(Integer data) {
            this.data = data;
        }
    }
    public Node reverse(Node head,int k) {
        if(head == null || k<2) return head;
        Node pre = null; //pre开始是K个节点中第一个节点的前一个节点
        Node start = null;//start是K个节点的第一个节点
        Node cur = head;
        Node next = null;
        int count = 1;
        while(cur!=null) {
            next = cur.next;
            if(count == k) {
                start = pre == null ?head:pre.next;
                head= pre == null ?cur:head;
                resign(pre,start,cur,next);
                pre = start;
                count = 0;
            }
            count ++;
            cur = next;
        }
        return head;
    }

    public void resign(Node left,Node start,Node end,Node right) {
        Node pre = start;
        Node cur = start.next;
        Node next = null;
        while(cur!=right) {
            next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        if(left!=null) {
            left.next = end;
        }
        start.next = right;
    }
    /**
     *   利用栈
     * @param head
     * @param k
     * @return
     */
    public static  Node reverseKNode1(Node head,int k){
        if(head ==null || k<2){
            return head;
        }
        Stack<Node> stack = new Stack<>();
         Node cur=head;
         Node newHead=head;
         Node pre=null;   // 前节点
         Node next=null;
         while(cur !=null){
             next=cur.next;
             if(stack.size()== k){
                 pre= reverseNodeByStack(stack,pre,next);
                 newHead=newHead==head?cur:newHead;
             }
             cur=next;
         }
         return  newHead;
    }

    // 反转   并返回尾节点
    private static Node reverseNodeByStack(Stack<Node> stack, Node pre, Node next) {
        Node cur = stack.pop();
        if(pre== null){
            pre=cur;
        }
        Node n=null;
        while(!stack.isEmpty()){
            n= stack.pop();
            cur.next=n;
            cur=n;
            
        }
        cur.next=next;
        return cur;
    }
}