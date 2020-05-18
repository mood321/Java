package algorithm.Interview.practice03;

/**
 * @author mood321
 * @date 2020/5/19 1:00
 * @email 371428187@qq.com
 */
public class P_3_15_generateBST {
    //二叉树节点的定义
    public  static class Node{
        public  int value;
        public Node left;
        public Node right;

        public Node(int data)
        {
            this.value=data;
        }
    }

    //生成平衡二叉搜索树
    public static Node generateBSTree(int[]arr)
    {
        if(arr==null)
        {
            return null;
        }
        return generate(arr,0,arr.length-1);

    }
    //递归生成二叉排序树
    public static Node generate(int[]arr,int start,int end)
    {
        if(start>end)
        {
            return null;
        }
        int mid=(start+end)/2;
        Node head=new Node(arr[mid]);//生成头节点
        head.left=generate(arr,start,mid-1);
        head.right=generate(arr,mid+1,end);
        return head;

    }

}