package algorithm.Interview.practice02;

/**
 * @author mood321
 * @date 2020/4/8 0:25
 * @email 371428187@qq.com
 */
public class P_2_18_MergeNode {
    public static class Node {
        int data;
        Node next;

        public Node(int data) {
            this.data = data;
        }

        public Node() {
        }
    }

    public static Node merge(Node a, Node b) {
        if (a == null || b == null) {
            return a == null ? b : a;
        }
        Node auxA = a.next;
        Node auxB = b.next;
        Node auxC = null;

        int small = a.data < b.data ? a.data : b.data;
        int big = a.data < b.data ? b.data : a.data;
        auxC = new Node(small);
        Node c=auxC;
        Node node = new Node(big);
        node.next = null;
        auxC.next = node;
        auxC = node;

        while (auxA != null && auxB != null) {
            Node newNode = new Node();
            newNode.next = null;

            // A 中更小
            if (auxA.data < auxB.data) {
                newNode.data = auxA.data;
                auxA = auxA.next;
                // B中更小，或相等
            } else {
                newNode.data = auxB.data;
                auxB = auxB.next;
            }

            auxC.next = newNode;
            auxC = auxC.next;
        }

        // 剩余部分直接接在尾巴
        if (auxA != null) {
            auxC.next = auxA;
        }

        if (auxB != null) {
            auxC.next = auxB;
        }

        return c;
    }
}