package algorithm.Interview.practice02;

/**
 * @author mood321
 * @date 2020/4/7 23:57
 * @email 371428187@qq.com
 */
public class P_2_17_InsertNum {
    public static class Node {
        int data;
        Node next;

        public Node(int data) {
            this.data = data;
        }
    }

    /**
     * 思路: 通过有序找到头尾节点 插入 和顺序
     *
     * @param head
     * @param num
     * @return
     */
    public Node insertNum(Node head, int num) {
        Node node = new Node(num);
        if (head == null) {
            node.next = node;
            return node;
        }
        Node cur = head.next;
        Node pre = head;

        while (cur != head) {    // 这处理插入节点不在头尾
            if (pre.data <= num && cur.data >= num) {
                break;
            }
            cur = cur.next;
            pre = pre.next;
        }
        // 插入
        pre.next = node;
        node.next = cur;
        return head.data < num ? head : node;
    }
}