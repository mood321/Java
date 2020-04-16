package algorithm.Interview.practice03;

/**
 * @author mood321
 * @date 2020/4/17 0:02
 * @email 371428187@qq.com
 */
public class P_3_2_PrintBinaryTree {
    public static class Node {
        private int data;
        private Node left;
        private Node right;

        public Node(int data) {
            this.data = data;
        }
    }

    public static void main(String[] args) {
        Node head = new Node(1);
        head.left = new Node(-222222222);
        head.right = new Node(3);
        head.left.left = new Node(Integer.MIN_VALUE);
        head.right.left = new Node(55555555);
        head.right.right = new Node(66);
        head.left.left.right = new Node(777);
        printTree(head);

        head = new Node(1);
        head.left = new Node(2);
        head.right = new Node(3);
        head.left.left = new Node(4);
        head.right.left = new Node(5);
        head.right.right = new Node(6);
        head.left.left.right = new Node(7);
        printTree(head);

        head = new Node(1);
        head.left = new Node(1);
        head.right = new Node(1);
        head.left.left = new Node(1);
        head.right.left = new Node(1);
        head.right.right = new Node(1);
        head.left.left.right = new Node(1);
        printTree(head);

    }

    private static void printTree(Node head) {
        System.out.println("Print Tree:");
        printInOrder(head, 0, "R", 17);
        System.out.println();
    }

    private static void printInOrder(Node head, int height, String r, int len) {
               if(head==null){
                   return;

               }
        // 横屏从最右开始
        printInOrder(head.right,height+1,"v", len);
            // 下面五行都是格式化格式的
        String val = r + head.data + r;
        int lenM = val.length();
        int lenL = (len - lenM) / 2;
        int lenR = len - lenM - lenL;
        val = getSpace(lenL) + val + getSpace(lenR);


        System.out.println(getSpace(height*len)+val);
        printInOrder(head.left,height+1,"^", len);

    }

    private static String getSpace(int num) {
        String space=" ";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < num; i++) {
                   stringBuffer.append(space);
        }
        return stringBuffer.toString();
    }
}