package algorithm.Interview.practice02;

import java.util.HashMap;

/**
 * @author mood321
 * @date 2020/3/12 23:31
 * @email 371428187@qq.com
 * Node类中的value是节点值，next指针和正常链表中next指针的意义一样，rand指针可能指向任意一个Node，也可以指向null。
 *
 * 给定一个由Node节点类型组成的无环单链表的头结点head,请实现一个函数完成这个链表结构的复制，并返回新链表的头结点。
 * 例如：链表1->2->3->null,假设1的rand指针指向3,2的rand指正指向null,3的rand指针指向1。复制后的链表应该也是这种结构。
 * 解法一：HashMap解决
 * 1。遍历链表一次，设置key为Node,value为复制的Node的value，没有设置next和rand。
 * 2。第二次遍历设置next和rand。
 * 3。返回头
 */
public class P_2_8_CopyRandNode {
    public static class Node{
        Integer data;
        Node next;
        Node rand;

        public Node(Integer data) {
            this.data = data;
        }
    }

    // 普通解法
    public  static  Node CopyRandNode1(Node head){
         if(head == null) {
             return  head;
         }
        HashMap<Node, Node> map = new HashMap<>();    //
         Node cur=head;
         while(cur!=null){        // 对应
             map.put(cur,new Node(cur.data));
             cur=cur.next;
         }
       cur=head;
        while(cur!=null){         // 处理 next和rand 节点
            Node node = map.get(cur);
            node.next=cur.next;
            node.rand=cur.rand;
            cur=cur.next;
        }
        return  map.get(head);

    }

    /**
     * 首先我们把链表的形式由1-2-3-null变为1-1’-2-2’-3-3’-null,意思就是把我们复制的链表插在我们的中间。
     * <p>           2。复制rand。
     * <p>           3 大链表 拆成两个各自的链表
     * @param head
     * @return
     */
    public  static  Node CopyRandNode2(Node head){
        if(head==null){
            return  head;
        }
        Node cur=head;
        while(cur!= null){
            Node next = cur.next;
            Node node = new Node(cur.data);
            cur.next=node;
             node.next=next;
             cur=next;
        }
         // 处理随机节点
        cur=head;
        while(cur!= null){
            Node next = cur.next;
            next.rand=cur.rand;
            cur=next.next;
        }
        // 分离大链表
        cur=head;
        Node res=cur.next;
        while(cur!= null){
            Node copy = cur.next;    //
            Node node = copy.next;
            cur.next=node;
            copy.next= node==null ? null: copy;
            cur=node;

        }
        return  res;
    }
}