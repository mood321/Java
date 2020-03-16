package algorithm.Interview.practice02;

import java.util.HashSet;

/**
 * @author mood321
 * @date 2020/3/17 0:38
 * @email 371428187@qq.com
 */
public class P_2_12_RemoveNode {
    public static class Node {
        private Integer data;
        private Node next;

        public Node(Integer data) {
            this.data = data;
        }
    }

    /*
    方法1
     */
    public static void removeNode(Node head) {
        if (head == null) {
            return;
        }
        HashSet<Integer> set = new HashSet<>();

        Node pre = head;
        Node next = head.next;
        set.add(head.data);
        while (next != null) {
            if (set.contains(next.data)) {
                pre.next = next.next;
            } else {
                set.add(next.data);
                pre = next;
            }
            next = next.next;

        }
    }

    public static void removeNode2(Node head) {
        if (head == null) {
            return;
        }
        Node cur = head;
        Node pre  =null;
        Node next =null;
        while (cur != null) {
            pre  = cur;  // 上一节点
            next= pre.next;
            while (next != null) {
                if (cur.data.equals(next.data)) {
                    pre.next = next.next;
                } else {
                    pre = next;
                }
                next = next.next;
            }
            cur = cur.next;
        }
    }
}