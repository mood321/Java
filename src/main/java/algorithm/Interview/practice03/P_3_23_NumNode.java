package algorithm.Interview.practice03;

/**
 * @author mood321
 * @date 2020/6/10 0:24
 * @email 371428187@qq.com
 */
public class P_3_23_NumNode {
    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int value) {
            this.value = value;
        }
    }

    public static int numNode(Node head) {
        if (head == null) {
            return 0;
        }
        return bs(head, 1, mostleftLevel(head, 1));
    }

    private static int bs(Node head, int l, int h) {
        if (l == h) {
            return 1;
        }
        if (mostleftLevel(head.right, l + 1) == h) {     // 左子是满的

            return ((1 << h - 1) + bs(head.right, l + 1, h));
        } else {    // 右子 层数 -1 是满的
            return ((1 << h - l - 1) + bs(head.left, l + 1, h));
        }

    }

    private static int mostleftLevel(Node head, int i) {
        while (head != null) {
            i++;
            head = head.left;
        }
        return i - 1;
    }
}