package algorithm.Interview.practice03;

/**
 * @author mood321
 * @date 2020/5/12 0:18
 * @email 371428187@qq.com
 */
public class P_0_11_IsBalance {
    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    public boolean isBanlance(Node head){
        Boolean[] res = new Boolean[1];
        res[0]=true;
        getHeight(head,1,res);
        return  res[0];
    }

    private int getHeight(Node head, int i, Boolean[] res) {
        if(head== null){
            return i;
        }
        int lh = getHeight(head.left, i + 1, res);
        if(!res[0]){
            return  i;
        }

        int rh = getHeight(head.right, i + 1, res);
        if(!res[0]){
            return  i;
        }
         if(Math.abs(lh-rh)>1){
             res[0]=false;
         }
         return Math.max(lh,rh);
    }
}