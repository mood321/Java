package algorithm.Interview.practice03;

/**
 * @author mood321
 * @date 2020/4/23 0:13
 * @email 371428187@qq.com
 */
public class P_3_6_BiggestSubBSTInTree {

    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    /**
     * 整体算是后序
     *   逻辑就是 递归左子  右子      ,拿到左子右子 的  最大搜索树节点数 ,头结点,最大值,最小值
     *   1 判断 左子 右子 能不能和cur 相连      能,相连返回
     *   2  不能连 ,看左子右子 ,那个大
     *
     *   原型那个实现,用了两种方法 ,但逻辑差不多
     *
     */
    public static Node biggestSubBST(Node head) {
        int[] record = new int[3]; // 0->size, 1->min, 2->max
        return posOrder(head, record);
    }

    private static Node posOrder(Node head, int[] record) {
        if(head ==null){
            record[0]=0;
            record[1]=Integer.MAX_VALUE;
            record[2]=Integer.MIN_VALUE;
            return null;
        }
        Node left = head.left;
        Node right = head.right;
        int value = head.value;
        
        // 左子树
        Node lHead = posOrder(left, record);
        int lSize=record[0];
        int lMIN=record[1];
        int lMAX=record[2];

        // 右子
        Node rHead = posOrder(right, record);
        int rSize=record[0];
        int rMIN=record[1];
        int rMAX=record[2];

        // 判断
        record[1]=Math.min(lMIN,value) ;
        record[2]=Math.max(rMAX,value);

        if(lHead == left && rHead== right && lMAX < value && rMIN >value){
            record[0]=lSize+rSize;
            return  head;
        }
        record[0]=Math.max(lSize,rSize);
        return lSize>rSize?lHead:rHead;
    }
}