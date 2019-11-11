package algorithm.basic07;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author mood321
 * @date 2019/11/11 21:20
 * @email 371428187@qq.com
 */
public class Code_03_IPO {
    public static class Node {
        int p;
        int c;

        public Node(int p, int c) {
            this.p = p;
            this.c = c;
        }

    }

    public static class MinCostComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            return o1.c - o2.c;
        }
    }

    public static class MaxProfitComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            return o2.p - o1.p;
        }
    }

    public static int findMaximizedCapital(int k, int W, int[] Profits, int[] Capital) {
        Node[] nodes = new Node[Capital.length];
        final int c = 0;
        for (int i = 0; i < Capital.length; i++) {
            nodes[i] = new Node(Profits[i], Capital[i]);
        }

        PriorityQueue<Node> cost = new PriorityQueue<>(new MinCostComparator());
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(new MaxProfitComparator());
        for (Node node : nodes) {
            cost.add(node);
        }
        // 正常情况    能做k个项目
        for (int i = 0; i < k; i++) {
            while (!cost.isEmpty() && cost.peek().c <= W) {
                priorityQueue.add(cost.poll());
            }
            if(priorityQueue.isEmpty()){
                return W;
            }
            W+=priorityQueue.poll().p;
        }
        return W;
    }
}
