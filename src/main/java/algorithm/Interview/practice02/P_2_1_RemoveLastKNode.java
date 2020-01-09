package algorithm.Interview.practice02;

import lombok.Data;

/**
 * @author mood321
 * @date 2020/1/9 23:29
 * @email 371428187@qq.com
 */
public class P_2_1_RemoveLastKNode {
    @Data
    public static class Node {
        private int data;
        private Node next;

        public Node(int data) {
            this.data = data;
        }
    }

    @Data
    public static class DoubleNode {
        private int data;
        private DoubleNode next;
        private DoubleNode pare;

        public DoubleNode(int data) {
            this.data = data;
        }
    }

    public Node removeLastKNode(Node head, int last) {
        if (head == null || last < 1) {
            return head;
        }
        /**
         *  这里不用辅助空间  复用last
         */
        Node cur = head;
        while (cur != null) {
            last--;
            cur = cur.next;
        }
        // last 如果还大于0 那就是链表没有 k 长 不做处理

        if (last == 0) {
            head = head.next;

        } else if (last < 0) {
            cur = head;
            while (++last != 0) {     // ++ last 是因为 拿到的倒数第k+1个
                head = head.next;
            }
            head.next = head.next.next;

        }
        return head;
    }

    public DoubleNode removeLastKNode(DoubleNode head, int last) {
        if (head == null || last < 1) {
            return head;
        }
        /**
         *  这里不用辅助空间  复用last
         */
        DoubleNode cur = head;
        while (cur != null) {
            last--;
            cur = cur.next;
        }
        // last 如果还大于0 那就是链表没有 k 长 不做处理

        if (last == 0) {
            head = head.next;
            head.pare = null;
        } else if (last < 0) {
            cur = head;
            while (++last != 0) {     // ++ last 是因为 拿到的倒数第k+1个
                head = head.next;
            }
            DoubleNode next = head.next.next;
            head.next = next;
            if (next != null) {
                next.pare = head;
            }
        }
        return head;
    }


}