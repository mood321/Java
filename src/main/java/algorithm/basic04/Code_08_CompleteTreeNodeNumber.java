package algorithm.basic04;

/**
 * @Created by mood321
 * @Date 2019/11/5 0005
 * @Description TODO
 */
public class Code_08_CompleteTreeNodeNumber {
    public  static class Node{
        int data;
        Node left;
        Node right;
        public Node(int data){
            this.data=data;
        }
    }
    public static int nodeNum(Node head) {
        if(head==null){
            return 0;
        }
        return bs(head,1,mostLealLeft(head,1));
    }

    private static int bs(Node head, int leavl, int h) {
        if(leavl == h){
            return 1;
        }
        if(h == mostLealLeft(head.right, leavl +1)){
            return  (1<< (h-leavl)) +bs(head.right, leavl +1, h); // << 运算等级 很低
        }else {
            return (1<<(h -leavl -1)) +bs(head.left,leavl+1,h);

        }
    }

    private static int mostLealLeft(Node head, int leal) {
        while(head!=null){
            leal++;
            head=head.left;
        }
        return leal-1;

    }
    public static void main(String[] args) {
        Node head = new Node(1);
        head.left = new Node(2);
        head.right = new Node(3);
        head.left.left = new Node(4);
        head.left.right = new Node(5);
        head.right.left = new Node(6);
        System.out.println(nodeNum(head));

    }
}
