package offer;


import offer.bean.TreeNode;

import java.util.Arrays;

public class 前序遍历和中序推出二叉树 {
    public static void main(String[] args) {
        int s[]={1,2,4,7,3,5,6,8};
        int c[]={4,7,2,1,5,3,8,6};

        TreeNode treeNode = reConstructBinaryTree2(s, c);
        System.out.println(treeNode);
    }


    public static TreeNode reConstructBinaryTree2(int [] pre,int [] in) {
        if(pre.length == 0||in.length == 0){
            return null;
        }
        //前序遍历第一个必为root 中序的在root前的都是左子树  后面的后右子树
        TreeNode node = new TreeNode(pre[0]);
        for(int i = 0; i < in.length; i++){
            if(pre[0] == in[i]){
                node.left = reConstructBinaryTree2(Arrays.copyOfRange(pre, 1, i+1), Arrays.copyOfRange(in, 0, i));
                node.right = reConstructBinaryTree2(Arrays.copyOfRange(pre, i+1, pre.length), Arrays.copyOfRange(in, i+1,in.length));
            }
        }
        return node;
    }
    //前序遍历{1,2,4,7,3,5,6,8}和中序遍历序列{4,7,2,1,5,3,8,6}
    /**
     *          1
     *        2   3
     *       4   5  6
     *        7    8
     *
     */
    public static TreeNode reConstructBinaryTree(int [] pre, int [] in) {
        if(pre==null||in==null){
            return null;
        }

        java.util.HashMap<Integer,Integer> map= new java.util.HashMap<Integer, Integer>();
        for(int i=0;i<in.length;i++){
            map.put(in[i],i);
        }
        return preIn(pre,0,pre.length-1,in,0,in.length-1,map);
    }

    public static TreeNode preIn(int[] p, int pi, int pj, int[] n, int ni, int nj, java.util.HashMap<Integer,Integer> map){

        if(pi>pj){
            return null;
        }
        TreeNode head=new TreeNode(p[pi]);
        int index=map.get(p[pi]);
        head.left=preIn(p,pi+1,pi+index-ni,n,ni,index-1,map);
        head.right=preIn(p,pi+index-ni+1,pj,n,index+1,nj,map);
        return head;
    }
}
