package algorithm.Interview.practice03;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mood321
 * @date 2020/5/29 0:34
 * @email 371428187@qq.com
 */
public class P_3_22_NumTree {

    public static int numTree(int n) {
        if (n < 2) {
            return 1;
        }
        int[] num = new int[n + 1];
        num[0] = 1;
        for (int i = 1; i < n + 1; i++) {

            for (int j = i; j < i + 1; j++) {
                num[i] += num[j - 1] * num[i - j];
            }
        }
        return num[n];
    }

    /**
     * 进阶
     */
    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int value) {
            this.value = value;
        }
    }

    public static List<Node> generateTrees(int n) {
        return generate(1, n);
    }

    public static List<Node> generate(int start, int end) {
        List<Node> res = new LinkedList<>();
        if (start > end) {
            res.add(null);
        }
        Node head = null;
        for (int i = start; i < end + 1; i++) {
            head = new Node(i);
            List<Node> lsub = generate(start, i - 1);
            List<Node> rsub = generate(i + 1, end);
            for (Node l : lsub) {
                for (Node r : rsub) {
                    head.left = l;
                    head.right = r;
                    res.add(cloneTree(head));
                }
            }

        }
        return res;
    }

    private static Node cloneTree(Node head) {
        if (head == null) {
            return null;
        }
        Node res = new Node(head.value);
        res.left = cloneTree(head.left);
        res.right = cloneTree(head.right);
        return res;
    }
}