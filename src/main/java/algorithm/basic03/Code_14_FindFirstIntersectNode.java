package algorithm.basic03;

import java.util.HashMap;

/**
 * @Created by mood321
 * @Date 2019/10/31 0031
 * @Description 注释和思路详情见笔记
 */
public class Code_14_FindFirstIntersectNode {

    public static class Node {
        int data;
        Node next;

        public Node(int data) {
            this.data = data;

        }
    }

    //  判断链表是否有环 有则返回入环节点
    public static Node getLoopNode(Node head) {

        if (head == null) {
            return null;
        }
        Node fast = head.next.next;
        Node slow = head.next;
        while (fast != slow) {
            if ( fast.next == null ||fast.next.next == null  ) {
                return null;

            } else{
                fast = fast.next.next;
                slow = slow.next;
            }

        }
        fast = head;
        while (fast != slow) {
            fast = fast.next;
            slow = slow.next;
        }
        return fast;
    }

    //通过map 判断链表是否有环
    public static Node getLoopMapNode(Node head) {
        HashMap<Node, Integer> map = new HashMap<>();

        Node cur = head;
        while (cur != null) {
            if (map.containsKey(cur)) {
                return cur;
            }
            map.put(cur, 0);
            cur = cur.next;
        }
        return null;
    }

    // 主方法  判断两个链表是否相交
    public static Node getIntersectNode(Node head1, Node head2) {
        if (head1 == null || head2 == null) {
            return null;
        }
        Node loopNode = getLoopNode(head1);
        Node loopNode2 = getLoopNode(head2);
        //无环的情况
        if (loopNode == null && loopNode2 == null) {
            return noLoop(head1, head2);
            //return noLoopMap(head1, head2);
        }
        // 有环
        if (loopNode != null && loopNode2 != null) {
            return bothLoop(head1, loopNode, head2, loopNode2);
        }
        // 一个有环  一个无环  肯定不相交
        return null;
    }

    private static Node bothLoop(Node head1, Node loopNode, Node head2, Node loopNode2) {
        if (head1 == null || head2 == null || loopNode == null || loopNode2 == null) {
            return null;

        }
        if (loopNode == loopNode2) {
            Node next1 = loopNode.next;
            Node next2 = loopNode2.next;
            loopNode.next = null;
            loopNode2.next = null;
            Node node = noLoop(head1, head2);
            loopNode.next = next1;
            loopNode2.next = next2;
            return node;
        } else {
            Node next = loopNode.next;
            while (next != loopNode) {
                if (next == loopNode2) {
                    return loopNode2;

                }
                next = next.next;
            }
            return null;
        }
    }

    // 无环链表 相交节点 用map辅助
    private static Node noLoopMap(Node head1, Node head2) {
        if (head1 == null || head2 == null)
            return null;
        HashMap<Node, Integer> map = new HashMap<>();
        Node n1 = head1;
        while (n1 != null) {
            map.put(n1, 0);
        }
        Node n2 = head2;
        while (n2 != null) {
            if (map.containsKey(n2)) {
                return n2;
            }
            n2 = n2.next;
        }
        return null;
    }

    // 无环链表 相交节点 无辅助空间
    private static Node noLoop(Node head1, Node head2) {
        if (head1 == null || head2 == null) {
            return null;

        }
        Node n1 = head1;
        Node n2 = head2;
        int size1 = 0;
        while (n1.next != null) {
            size1++;
            n1 = n1.next;
        }
        while (n2.next != null) {
            size1--;
            n2 = n2.next;
        }
        if (n1 != n2) {
            return null;
        }
        n1 = size1 > 0 ? head1 : head2;
        n2 = size1 > 0 ? head2 : head1;
        size1=Math.abs(size1);
        for (; size1 > 0; size1--) {
            n1 = n1.next;
        }
        while (n1 != n2 ) {
            n1 = n1.next;
            n2 = n2.next;
        }
        return n1;
    }
    public static void main(String[] args) {
        // 1->2->3->4->5->6->7->null
        Node head1 = new Node(1);
        head1.next = new Node(2);
        head1.next.next = new Node(3);
        head1.next.next.next = new Node(4);
        head1.next.next.next.next = new Node(5);
        head1.next.next.next.next.next = new Node(6);
        head1.next.next.next.next.next.next = new Node(7);

        // 0->9->8->6->7->null
        Node head2 = new Node(0);
        head2.next = new Node(9);
        head2.next.next = new Node(8);
        head2.next.next.next = head1.next.next.next.next.next; // 8->6
        System.out.println(getIntersectNode(head1, head2).data);

        // 1->2->3->4->5->6->7->4...
        head1 = new Node(1);
        head1.next = new Node(2);
        head1.next.next = new Node(3);
        head1.next.next.next = new Node(4);
        head1.next.next.next.next = new Node(5);
        head1.next.next.next.next.next = new Node(6);
        head1.next.next.next.next.next.next = new Node(7);
        head1.next.next.next.next.next.next = head1.next.next.next; // 7->4

        // 0->9->8->2...
        head2 = new Node(0);
        head2.next = new Node(9);
        head2.next.next = new Node(8);
        head2.next.next.next = head1.next; // 8->2
        System.out.println(getIntersectNode(head1, head2).data);

        // 0->9->8->6->4->5->6..
        head2 = new Node(0);
        head2.next = new Node(9);
        head2.next.next = new Node(8);
        head2.next.next.next = head1.next.next.next.next.next; // 8->6
        System.out.println(getIntersectNode(head1, head2).data);

    }
}
