package algorithm.Interview.practice03;

import java.util.HashMap;

/**
 * @author mood321
 * @date 2020/5/27 0:55
 * @email 371428187@qq.com
 */
public class P_3_20_PosToTree {
    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    public Node preInToTree(int[] pre, int[] in) {
        if (pre == null || in == null) {
            return null;
        }

        HashMap<Integer, Integer> map = new HashMap<>();        // 哈希表的键值对用于记录元素及其索引
        for (int i = 0; i < in.length; i++) {
            map.put(in[i], i);
        }

        return preIn(pre, 0, pre.length - 1, in, 0, in.length - 1, map);
    }

    public Node preIn(int[] p, int pi, int pj, int[] n, int ni, int nj, HashMap<Integer, Integer> map) {
        if (pi > pj) {
            return null;
        }

        Node head = new Node(p[pi]);            // 当前先序数组的第一个元素是当前子树的头节点
        int index = map.get(p[pi]);
        head.left = preIn(p, pi + 1, pi + index - ni, n, ni, index - 1, map);
        head.right = preIn(p, pi + index - ni + 1, pj, n, index + 1, nj, map);
        return head;
    }

    public Node inPosToTree(int[] in, int[] pos) {
        if (in == null || pos == null) {
            return null;
        }

        HashMap<Integer, Integer> map = new HashMap<>();             // 哈希表的键值对用于记录元素及其索引
        for (int i = 0; i < in.length; i++) {
            map.put(in[i], i);
        }

        return inPos(in, 0, in.length - 1, pos, 0, pos.length - 1, map);
    }

    public Node inPos(int[] n, int ni, int nj, int[] s, int si, int sj, HashMap<Integer, Integer> map) {
        if (si > sj) {
            return null;
        }

        Node head = new Node(s[sj]);           // 当前后序数组的最后一个元素是当前子树的头节点
        int index = map.get(s[sj]);
        head.left = inPos(n, ni, index - 1, s, si, si + index - ni - 1, map);
        head.right = inPos(n, index + 1, nj, s, si + index - ni, sj - 1, map);
        return head;
    }

    public Node prePosToTree(int[] pre, int[] pos) {
        if (pre == null || pos == null) {
            return null;
        }

        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < pos.length; i++) {
            map.put(pos[i], i);
        }

        return prePos(pre, 0, pre.length - 1, pos, 0, pos.length - 1, map);
    }

    public Node prePos(int[] p, int pi, int pj, int[] s, int si, int sj, HashMap<Integer, Integer> map) {
        Node head = new Node(s[sj--]);
        if (pi == pj) {
            return head;
        }
        int index = map.get(p[++pi]);
        head.left = prePos(p, pi, pi + index - si, s, si, index, map);
        head.right = prePos(p, pi + index - si + 1, pj, s, index + 1, sj, map);
        return head;
    }
}