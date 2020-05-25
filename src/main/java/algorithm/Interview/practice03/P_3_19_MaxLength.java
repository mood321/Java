package algorithm.Interview.practice03;

/**
 * @author mood321
 * @date 2020/5/26 0:46
 * @email 371428187@qq.com
 */
public class P_3_19_MaxLength {
    public  static class Node{
        public  int value;
        public Node left;
        public Node right;

        public Node(int data)
        {
            this.value=data;
        }
    }

    public  static int    getMaxLength(Node head){
        int[] res = new int[1];
        return  posHandler(head,res);
    }

    private static int posHandler(Node head, int[] res) {
        if(head ==null) {
            res[0]=0;
            return  0;
        }
        int l = posHandler(head.left, res);
        int maxL=res[0];
        int r = posHandler(head.left, res);
        int maxR=res[0];
        int maxCur = maxL + maxR + 1;
        res[0]=Math.max(maxL,maxR)+1;
        return Math.max(Math.max(maxL,maxR),maxCur);
    }
}