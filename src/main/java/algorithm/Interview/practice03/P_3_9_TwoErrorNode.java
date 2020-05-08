package algorithm.Interview.practice03;

import java.util.Stack;

/**
 * @author mood321
 * @date 2020/5/8 23:58
 * @email 371428187@qq.com
 */
public class P_3_9_TwoErrorNode {
    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    public Node[] getTwoErrorNode(Node head) {
        Node[] error = new Node[2];
        if (head == null) {
            return error;
        }
        Stack<Node> stack = new Stack<>();
        Node pre = null;
        while (!stack.isEmpty() || head != null) {
            if (head != null) {
                stack.push(head);
                head = head.left;
            } else {
                head = stack.pop();
                if (pre != null && pre.value > head.value) {
                    error[0] = error[0] == null ? pre : error[0];
                    error[1] = head;
                }
                pre = head;
                head = head.right;
            }

        }
        return error;
    }

    /**
     * 获取错误节点的父节点
     *
     * @param head
     * @return
     */
    public Node[] getTwoErrorParentNodes(Node head, Node e1, Node e2) {
        Node[] parents = new Node[2];
        if (head == null) {
            return parents;
        }
        Stack<Node> stack = new Stack<>();
        while (!stack.isEmpty() || head != null) {
            if (head != null) {
                stack.push(head);
                head = head.left;
            } else {
                head = stack.pop();
                if (head.left == e1 || head.right == e1) {
                    parents[0] = head;
                }
                if (head.left == e2 || head.right == e2) {
                    parents[1] = head;
                }
                head = head.right;
            }
        }
        return parents;
    }

   
}