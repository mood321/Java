package algorithm.basic03;

/**
 * @Created by mood321
 * @Date 2019/10/22 0022
 * @Description TODO
 */
public class Code_07_ReverseList {
    public static class Node {
        public int value;
        public Node next;

        public Node(int data) {
            this.value = data;
        }
    }

    public static Node reverseList(Node head) {
        Node newN = null;
        Node next = null;
        while (head != null) {
            next = head.next; //  先取剩下的链表
            head.next = newN;// 把当前节点 加到新的链表最前面
            newN = head;// 新的 链表重置头结点
            head = next;// 继续取原来链表
        }
        return newN;
    }

    public static class DoubleNode {
        public int value;
        public DoubleNode pre;
        public DoubleNode next;

        public DoubleNode(int data) {
            this.value = data;
        }
    }

    public static DoubleNode reverseList(DoubleNode head) {
        DoubleNode newN = null;
        DoubleNode next = null;
        while (head != null) {
            next = head.next; // 逻辑同上 先取出未遍历链表
            head.next = newN;// 当前链表 添加到新的链表最前面
            head.pre = next;//
            newN = head; // 修改后的链表 放在新的链表里
            head = next; // 继续 未遍历的链表
        }
        return newN;

    }
    public static void printLinkedList(Node head) {
        System.out.print("Linked List: ");
        while (head != null) {
            System.out.print(head.value + " ");
            head = head.next;
        }
        System.out.println();
    }

    public static void printDoubleLinkedList(DoubleNode head) {
        System.out.print("Double Linked List: ");
        DoubleNode end = null;
        while (head != null) {
            System.out.print(head.value + " ");
            end = head;
            head = head.next;
        }
        System.out.print("| ");
        while (end != null) {
            System.out.print(end.value + " ");
            end = end.pre;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Node head1 = new Node(1);
        head1.next = new Node(2);
        head1.next.next = new Node(3);
        printLinkedList(head1);
        head1 = reverseList(head1);
        printLinkedList(head1);

        DoubleNode head2 = new DoubleNode(1);
        head2.next = new DoubleNode(2);
        head2.next.pre = head2;
        head2.next.next = new DoubleNode(3);
        head2.next.next.pre = head2.next;
        head2.next.next.next = new DoubleNode(4);
        head2.next.next.next.pre = head2.next.next;
        printDoubleLinkedList(head2);
        printDoubleLinkedList(reverseList(head2));

    }
}
