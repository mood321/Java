package algorithm.basic03;

/**
 * @Created by mood321
 * @Date 2019/10/25 0025
 * @Description TODO
 */
public class Code_10_PrintCommonPart {

    public static class Node {
        public Integer data;
        public Node next;

        public Node(Integer data) {
            this.data = data;
        }
    }

    public static void printList(Node node) {
        System.out.println("printList");
        while (node != null) {
            System.out.print(node.data + "  ");
            node = node.next;
        }
        System.out.println();
    }

    public static void printCommonPart(Node head1, Node head2) {

        while (head1 != null && head2 != null) {
            if(head1.data<head2.data){
                head1=head1.next;
            }else if(head2.data<head1.data){
                head2=head2.next;

            }else {
                System.out.print(head1.data+" ");
                head1=head1.next;
                head2=head2.next;
            }
        }
        System.out.println();
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

        printList(node1);
        printList(node2);
        printCommonPart(node1, node2);

    }
}
