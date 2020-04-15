package algorithm.Interview.practice03;

/**
 * @author mood321
 * @date 2020/4/15 0:04
 * @email 371428187@qq.com
 */
public class P_3_1_PrintEdge {
    public static class Node {
        private int data;
        private Node left;
        private Node right;

        public Node(int data) {
            this.data = data;
        }
    }
    /**
     * 标准2
     *  1 只有一个孩子的 直接打印
     *  2 有左右的 判断是有左右孩子  只有一个打印  叶子打印
     */
    public static void printEdge2(Node root) {
        if(root ==null){
            return;
        }
        System.out.print(root.data+" ");
        if( root.right != null &&  root.left !=null){
            // 分别处理左子 右子树
                 printLeft(root.left,true);
                 printright(root.right,true);
        }   else {
            printEdge2(root.left==null?root.right:root.left);
        }

    }

    // 后序
    private static void printright(Node right, boolean b) {
        if(right ==null ){
            return;
        }

        printright(right.left,b && right.right ==null);
        printright(right.right,b);
        //  逆序
        if(b  ||  ( right.left ==null && right.right== null)){
            System.out.print(right.data+ "  ");
        }

    }

    // 先序
    private static void printLeft(Node left, boolean b) {
        if(left ==null ){
            return;
        }
        if(b  ||  ( left.left ==null && left.right== null)){
            System.out.print(left.data+ "  ");
        }
        printLeft(left.left,b);
        printLeft(left.right,b && left.left ==null);
    }

    /**
     * 标准1
     * 1 找到高度
     * 2 找到最左最右
     * 3 打印最左
     * 4 打印叶子
     * 5 逆序打印最右
     */
    public static void printEdge1(Node root) {
        if (root == null) {
            return;
        }
        int height = getHeight(root, 1);
        Node[][] edgeMap = new Node[height][2];// 存左右节点
        setEdgeMap(edgeMap, 0, root);
        // 3 打印最左
        for (int i = 0; i < edgeMap.length; i++) {
            System.out.print(edgeMap[i][0].data+"  ");
        }
        // 4 打印叶子
        printLeafNoInMap(root,0,edgeMap);
        // 5 逆序打印最右
        for (int i = edgeMap.length - 1; i >= 0; i--) {
              if( edgeMap[i][0] != edgeMap[i][1]){
                  System.out.print(edgeMap[i][1].data +"  ");
              }
        }
        System.out.println(" end ");
    }

    /**
     *  还是先序  打印
     * @param root
     * @param i
     * @param edgeMap
     */
    private static void printLeafNoInMap(Node root, int i, Node[][] edgeMap) {
        if(root ==null ){
            return;

        }
        if(root.left ==null && root.right==null && root!= edgeMap[i][0] && root != edgeMap[i][1]){
            System.out.print(root.data +"  ");
        }
        printLeafNoInMap(root.left,i+1,edgeMap);
        printLeafNoInMap(root.right,i+1,edgeMap);
    }


    /**
     * 还是递归  找左右节点
     *
     * @param edgeMap
     * @param i
     */
    private static void setEdgeMap(Node[][] edgeMap, int i, Node head) {
        if (head == null) {
            return;
        }
        //  递归的时候要 处理在最右数的时候  重新赋值的情况
        // 这是个先序遍历
        edgeMap[i][0] = edgeMap[i][0] == null ? head : edgeMap[i][0];
        edgeMap[i][1] = head;  // 右侧的值会一直覆盖 最后覆盖上去的是最右
        setEdgeMap(edgeMap, i + 1, head.left);
        setEdgeMap(edgeMap, i + 1, head.right);
    }

    /**
     * 递归去二叉树高度
     *
     * @param root
     * @param lev
     * @return
     */
    private static int getHeight(Node root, int lev) {
        if (root == null) {
            return lev;
        }
        return Math.max(getHeight(root.left, lev + 1), getHeight(root.right, lev + 1));
    }

}