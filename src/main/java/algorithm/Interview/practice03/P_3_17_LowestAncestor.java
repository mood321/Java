package algorithm.Interview.practice03;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author mood321
 * @date 2020/5/21 0:52
 * @email 371428187@qq.com
 */
public class P_3_17_LowestAncestor {
    public  static class Node{
        public  int value;
        public Node left;
        public Node right;

        public Node(int data)
        {
            this.value=data;
        }
    }

    /**
     *   找到最经公共
     */
    public static  Node lowAncestor(Node head,Node o1,Node o2){
        if(head== null || o1 == null || o2 ==null){
            return  null;
        }
        Node l = lowAncestor(head.left, o1, o2);
        Node r = lowAncestor(head.right, o1, o2);
        if(l !=null && r != null){
            return head;
        }
        return r==null?l:r;
    }


//***************************************************
    // 进阶问题--方法一(构造hash表)
    public static class Record1 {
        private HashMap<Node, Node> map;

        public Record1(Node head) {
            map = new HashMap<Node, Node>();
            if (head != null) {
                map.put(head, null);
            }
            setMap(head);
        }

        private void setMap(Node head) {
            if (head == null) {
                return;
            }
            if (head.left != null) {
                map.put(head.left, head);
            }
            if (head.right != null) {
                map.put(head.right, head);
            }
            setMap(head.left);
            setMap(head.right);
        }

        public Node query(Node o1, Node o2) {
            HashSet<Node> path = new HashSet<Node>();
            while (map.containsKey(o1)) {
                path.add(o1);
                o1 = map.get(o1);
            }
            while (!path.contains(o2)) {
                o2 = map.get(o2);
            }
            return o2;
        }

    }

    // 进阶问题--方法二
    public static class Record2 {
        private HashMap<Node, HashMap<Node, Node>> map;

        public Record2(Node head) {
            map = new HashMap<Node, HashMap<Node, Node>>();
            initMap(head);
            setMap(head);
        }

        private void initMap(Node head) {
            if (head == null) {
                return;
            }
            map.put(head, new HashMap<Node, Node>());
            initMap(head.left);
            initMap(head.right);
        }

        private void setMap(Node head) {
            if (head == null) {
                return;
            }
            headRecord(head.left, head);
            headRecord(head.right, head);
            subRecord(head);
            setMap(head.left);
            setMap(head.right);
        }

        private void headRecord(Node n, Node h) {
            if (n == null) {
                return;
            }
            map.get(n).put(h, h);
            headRecord(n.left, h);
            headRecord(n.right, h);
        }

        private void subRecord(Node head) {
            if (head == null) {
                return;
            }
            preLeft(head.left, head.right, head);
            subRecord(head.left);
            subRecord(head.right);
        }

        private void preLeft(Node l, Node r, Node h) {
            if (l == null) {
                return;
            }
            preRight(l, r, h);
            preLeft(l.left, r, h);
            preLeft(l.right, r, h);
        }

        private void preRight(Node l, Node r, Node h) {
            if (r == null) {
                return;
            }
            map.get(l).put(r, h);
            preRight(l, r.left, h);
            preRight(l, r.right, h);
        }

        public Node query(Node o1, Node o2) {
            if (o1 == o2) {
                return o1;
            }
            if (map.containsKey(o1)) {
                return map.get(o1).get(o2);
            }
            if (map.containsKey(o2)) {
                return map.get(o2).get(o1);
            }
            return null;
        }

    }
}