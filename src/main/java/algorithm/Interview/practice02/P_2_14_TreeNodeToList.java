package algorithm.Interview.practice02;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author mood321
 * @date 2020/3/19 0:53
 * @email 371428187@qq.com
 */
public class P_2_14_TreeNodeToList {
    public static class TreeNode {
        public int val;
        public TreeNode left = null;
        public TreeNode right = null;

        public TreeNode(int val) {
            this.val = val;
        }
    }

    /**
     * 使用辅助容器记录中序遍历
     */
    public static TreeNode convert1(TreeNode node) {
        if (node == null) {
            return null;

        }
        Queue<TreeNode> list = new LinkedList<>();
        insertNodeToList(list, node);

        node = list.poll();  //
        TreeNode pre = node;
        //  双向链表 1 <=== >2 <===>3
        pre.left = null;
        TreeNode cur = null;
        while (!list.isEmpty()) {
            cur = list.poll();
            cur.left = pre;
            pre.right = cur;
            pre = cur;
        }
        pre.right = null;
        return node;
    }


    // 利用递归的 中序遍历
    private static void insertNodeToList(Queue<TreeNode> list, TreeNode node) {
        if (node == null) {
            return;
        }
        insertNodeToList(list, node.left);
        list.add(node);
        insertNodeToList(list, node.right);
    }


    /**
     * 不使用使用辅助容器
     * 二叉树的递归
     */
    public static TreeNode convert2(TreeNode node) {
        if (node == null) {
            return node;
        }
        TreeNode last = process(node);
        node = last.right;
        last.right = null;
        return node;
    }

    /**
     * process 返回尾节点  而且尾节点right指向头结点
     *
     * @param node
     * @return
     */
    private static TreeNode process(TreeNode node) {
        if (node == null) {
            return null;
        }
        TreeNode leftEnd = process(node.left);// 左边尾
        TreeNode rightEnd = process(node.right);// 左边尾
        TreeNode leftS = leftEnd == null ? null : leftEnd.right;// 左边头
        TreeNode rightS = rightEnd == null ? null : rightEnd.right;// 右边头

        if (leftEnd != null && rightEnd != null) {// 有左右子树
            leftEnd.right = node;
            node.left = leftEnd;// 左子相连
            rightS.left = node;
            node.right = rightS;// 右子相连
            rightEnd.right = leftS; // 处理特殊点
            return rightEnd;
        } else if (leftEnd != null) {    //只有左树
            leftEnd.right = node;
            node.left = leftEnd;// 左子相连
            node.right = leftS;
            return node;

        } else if (rightEnd != null) {    //只有右树
            // node.left=null;
            node.right = rightS;
            rightS.left = node;
            rightEnd.right = node;
            return rightEnd;
        } else {
            node.right = node;
            return node;
        }
    }

}