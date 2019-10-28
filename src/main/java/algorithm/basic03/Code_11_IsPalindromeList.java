package algorithm.basic03;

import java.util.Stack;

/**
 * @Created by mood321
 * @Date 2019/10/28 0028
 * @Description TODO
 */
public class Code_11_IsPalindromeList {
    public static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
        }
    }

    // need n extra space
    public static boolean isPalindrome1(Node head) {
        if (head == null || head.next ==null){
            return false;
        }
        Stack<Node> stack = new Stack<>();
        Node cur = head;
        while (cur != null) {
            stack.push(cur);
            cur = cur.next;
        }
        while (head != null) {
            if (head.data != stack.pop().data) {
                return false;
            }
            head = head.next;
        }
        return true;
    }

    // need n/2 extra space
    public static boolean isPalindrome2(Node head) {
        if (head == null || head.next ==null){
            return false;
        }
        Node left = head;
        Node right = head;
        while (right.next != null && right.next.next != null) {
            left = left.next;
            right = right.next.next;
        }
        Stack<Node> stack = new Stack<>();
        while (left != null) {
            stack.push(left);
            left = left.next;
        }
        while (!stack.isEmpty()) {
            if (stack.pop().data != head.data) {
                return false;

            }
            head = head.next;
        }
        return true;
    }

    // need O(1) extra space
    public static boolean isPalindrome3(Node head) {
        if (head == null || head.next == null) {
            return true;
        }
        Node left = head;
        Node right = head;
        while (right.next != null && right.next.next != null) { // find mid node
            left = left.next; // left -> mid
            right = right.next.next; // right -> end
        }
        right = left.next; // right -> right part first node
        left.next = null; // mid.next -> null
        Node tem = null;
        while (right != null) { // right part convert
            tem = right.next; // tem -> save next node
            right.next = left; // next of right node convert
            left = right; // left move
            right = tem; // right move
        }
        tem = left; // tem -> save last node
        right = head;// right -> left first node
        boolean res = true;
        while (left != null && right != null) { // check palindrome
            if (left.data != right.data) {
                res = false;
                break;
            }
            left = left.next; // left to mid
            right = right.next; // right to mid
        }
        left = tem.next;
        tem.next = null;
        while (left != null) { // recover list
            right = left.next;
            left.next = tem;
            tem = left;
            left = right;
        }
        return res;
    }

    private static Node reverse(Node left1,Node stop) {
        Node left=left1;
         Node right = left.next;
        left.next = null;
        Node tem = null;
        while (right != null) {
            if(left==stop){
                break;
            }
            tem = right.next;
            right.next = left;
            left = right;
            right = tem;
        }
        return left;
    }


    public static void printLinkedList(Node node) {
        System.out.print("Linked List: ");
        while (node != null) {
            System.out.print(node.data + " ");
            node = node.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {

        Node head = null;
       /* printLinkedList(head);
        System.out.print(isPalindrome1(head) + " | ");
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");

        head = new Node(1);
        printLinkedList(head);
        System.out.print(isPalindrome1(head) + " | ");
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");

        head = new Node(1);
        head.next = new Node(2);
        printLinkedList(head);
        System.out.print(isPalindrome1(head) + " | ");
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");

        head = new Node(1);
        head.next = new Node(1);
        printLinkedList(head);
        System.out.print(isPalindrome1(head) + " | ");
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");

        head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        printLinkedList(head);
        System.out.print(isPalindrome1(head) + " | ");
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");

        head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(1);
        printLinkedList(head);
        System.out.print(isPalindrome1(head) + " | ");
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");

        head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(1);
        printLinkedList(head);
        System.out.print(isPalindrome1(head) + " | ");
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");

        head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(2);
        head.next.next.next = new Node(1);
        printLinkedList(head);
        System.out.print(isPalindrome1(head) + " | ");
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");
*/
        head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(2);
        head.next.next.next.next = new Node(1);
        printLinkedList(head);
        System.out.print(isPalindrome1(head) + " | ");
        System.out.print(isPalindrome2(head) + " | ");
        System.out.println(isPalindrome3(head) + " | ");
        printLinkedList(head);
        System.out.println("=========================");

    }
}
