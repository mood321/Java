package algorithm.Interview.practice03;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author mood321
 * @date 2020/4/20 0:32
 * @email 371428187@qq.com
 */
public class P_3_3_SerialNode {
    public static class Node {
        private int data;
        private Node left;
        private Node right;

        public Node(int data) {
            this.data = data;
        }
    }
    public String preSerialNode(Node head){
        if(head ==null){
            return "#!";
        }
        String res = head.data + "!";
        res += preSerialNode(head.left);
        res += preSerialNode(head.left);
        return  res;
    }

    public Node reconByString(String str){
        String[] split = str.split("!");
        Queue<String> queue = new LinkedList<>();
        for (int i = 0; i < split.length; i++) {
                   queue.offer(split[i]);
        }
        return  reconNodeOrder(queue);
    }

    private Node reconNodeOrder(Queue<String> queue) {
        String poll = queue.poll();
        if(poll.equals("#")){
            return  null;
        }
        Node head = new Node(Integer.valueOf(poll));
        head.left=reconNodeOrder(queue);
        head.right=reconNodeOrder(queue);
        return  head;
    }
}