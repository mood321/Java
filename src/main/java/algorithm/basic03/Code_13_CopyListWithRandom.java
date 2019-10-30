package algorithm.basic03;

import java.util.HashMap;

/**
 * @Created by mood321
 * @Date 2019/10/30 0030
 * @Description TODO
 */
public class Code_13_CopyListWithRandom {

    public static class Node{
        int data;
        Node next;
        Node rand;
        public Node (int data){
            this.data=data;
        }
    }
    // 用辅助map 记录关系 辅助空间O(N)
    public   static Node CopyListWithRand1(Node head){
        if(head==null){
            return null;
        }
        //
        HashMap<Node, Node> map = new HashMap<>();
        Node cur=head;
        while(cur!=null){
            map.put(cur,new Node(cur.data));
            cur=cur.next;
        }
        cur=head;
        while(cur!=null){
            map.get(cur).next=map.get(cur.next);
            map.get(cur).rand=map.get(cur.rand);
            cur=cur.next;
        }
        return  map.get(head);
    }

    public   static Node CopyListWithRand2(Node head){
        if (head == null) {
            return null;
        }
        Node cur = head;
        Node next = null;
        // copy node and link to every node
        while (cur != null) {
            next = cur.next;
            cur.next = new Node(cur.data);
            cur.next.next = next;
            cur = next;
        }
        cur = head;
        Node curCopy = null;
        // set copy node rand
        while (cur != null) {
            next = cur.next.next;
            curCopy = cur.next;
            curCopy.rand = cur.rand != null ? cur.rand.next : null;
            cur = next;
        }
        Node res = head.next;
        cur = head;
        // split
        while (cur != null) {
            next = cur.next.next;
            curCopy = cur.next;
            cur.next = next;
            curCopy.next = next != null ? next.next : null;
            cur = next;
        }
        return res;
    }

    public static void printRandLinkedList(Node head) {
        Node cur = head;
        System.out.print("order: ");
        while (cur != null) {
            System.out.print(cur.data + " ");
            cur = cur.next;
        }
        System.out.println();
        cur = head;
        System.out.print("rand:  ");
        while (cur != null) {
            System.out.print(cur.rand == null ? "- " : cur.rand.data + " ");
            cur = cur.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Node head = null;
        Node res1 = null;
        Node res2 = null;
        printRandLinkedList(head);
        res1 = CopyListWithRand1(head);
        printRandLinkedList(res1);
        res2 = CopyListWithRand2(head);
        printRandLinkedList(res2);
        printRandLinkedList(head);
        System.out.println("=========================");

        head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(4);
        head.next.next.next.next = new Node(5);
        head.next.next.next.next.next = new Node(6);

        head.rand = head.next.next.next.next.next; // 1 -> 6
        head.next.rand = head.next.next.next.next.next; // 2 -> 6
        head.next.next.rand = head.next.next.next.next; // 3 -> 5
        head.next.next.next.rand = head.next.next; // 4 -> 3
        head.next.next.next.next.rand = null; // 5 -> null
        head.next.next.next.next.next.rand = head.next.next.next; // 6 -> 4

        printRandLinkedList(head);
        res1 = CopyListWithRand1(head);
        printRandLinkedList(res1);
        res2 = CopyListWithRand2(head);
        printRandLinkedList(head);
        printRandLinkedList(res2);
        System.out.println("=========================");

    }
}
