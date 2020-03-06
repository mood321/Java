package algorithm.Interview.practice02;

import java.util.Stack;

/**
 * @author mood321
 * @date 2020/3/3 23:04
 * @email 371428187@qq.com
 */
public class P_2_6_IsPalindRom {
    public static class Node {
        Integer data;
        Node next;

        public Node(Integer data) {
            this.data = data;
        }
    }

    /**
     * 思路里面的思路2
     *
     * @param head
     * @return
     */
    public static boolean isPalindrome2(Node head) {
        if (head == null || head.next == null) {
            return false;
        }

        Node cur = head;
        Node right = head.next;
        while (cur.next != null && cur.next.next != null) {       //
            right = right.next;
            cur = cur.next.next;
        }
        Stack<Node> stack = new Stack<>();
        while (right != null) {
            stack.push(right);
            right = right.next;
        }
        while (!stack.isEmpty()) {
            if (!head.data.equals(stack.pop().data)) {
                return false ;
            }
            head = head.next;

        }
        return true;
    }

    /**
     * 进阶思路
     *        1   3   1
     *
     *        1   3   null
     *        3   1   null
     *
     *        1   3   null
     *        3   1
     *
     * @param head
     * @return
     */
    public static boolean isPalindrome3(Node head) {
        if (head == null || head.next == null) {
            return false;
        }

        Node cur = head;
        Node n2 = head;
        while (cur.next != null && cur.next.next != null) {       //
            n2 = n2.next;
            cur = cur.next.next;
        }
       cur=n2.next;
        n2.next=null;
        Node n3=null;
        while (cur !=null){      //三个节点  原头 cur  新头n2  中转n3
            n3=cur.next;
            cur.next=n2;
            n2=cur;
            cur=n3;
        }

        n3=n2;// n2  此时是原链表最后节点
        cur=head;
         boolean res=true;
        while(cur !=null && n2!=null){
              if(!cur.data.equals(n2.data)){
                  res= false;
                  break;
              }
              cur=cur.next;
              n2=n2.next;
        }
        printLinkedList(n2);
        // 恢复链表
        cur=n3.next;        //
        n3.next=null;

        while(cur !=null){   // 原头还是n2 新头n3 中转cur
            n2=cur.next;
            cur.next=n3;
            n3=cur;
            cur=n2;
        }

        return res;
    }
    public static void main(String[] args) {

        Node head = null;
      /*  printLinkedList(head);
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");

        head = new Node(1);
        printLinkedList(head);
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");

        head = new Node(1);
        head.next = new Node(2);
        printLinkedList(head);
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");

        head = new Node(1);
        head.next = new Node(1);
        printLinkedList(head);
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");

        head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        printLinkedList(head);
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");
*/
        head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(1);
        printLinkedList(head);
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");
/*
        head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(1);
        printLinkedList(head);
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");

        head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(2);
        head.next.next.next = new Node(1);
        printLinkedList(head);
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");

        head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(2);
        head.next.next.next.next = new Node(1);
        printLinkedList(head);
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");*/

    }

    private static void printLinkedList(Node head) {
        while(head !=null)
        {
            System.out.print( head.data +"  ");
            head=head.next;
        }
        System.out.println();
    }
}