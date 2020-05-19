package algorithm.Interview.practice03;

/**
 * @author mood321
 * @date 2020/5/20 0:33
 * @email 371428187@qq.com
 */
public class P_3_16_NextNode {
    public  static class Node{
        public  int value;
        public Node left;
        public Node right;
        public Node parent;

        public Node(int data)
        {
            this.value=data;
        }
    }
    public static  Node getNextNode(Node head){
        if(head== null){
            return head;
        }
        if(head.right !=null){   // 有右子
            return getLeftMost(head);
        }   else {
            Node parent = head.parent;
            while (parent !=null && parent.left != head){
                head=parent;
                parent=head.parent;
            }
            return parent;
        }
    }

    private static Node getLeftMost(Node head) {
        if(head ==null){
            return head;
        }
        while (head.left !=null){
            head=head.left;
        }
        return head;
    }
}