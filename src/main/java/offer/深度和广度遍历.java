package offer;

import offer.bean.TreeNode;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class 深度和广度遍历 {
    public static void main(String[] args) {
        TreeNode head=new TreeNode(1);
        TreeNode second=new TreeNode(2);
        TreeNode three=new TreeNode(3);
        TreeNode four=new TreeNode(4);
        TreeNode five=new TreeNode(5);
        TreeNode six=new TreeNode(6);
        TreeNode seven=new TreeNode(7);
        head.right=three;
        head.left=second;
        second.right=five;
        second.left=four;
        three.right=seven;
        three.left=six;

        System.out.print("广度优先遍历结果：");
       BroadFirstSearch(head);
        System.out.println();
        System.out.print("深度优先遍历结果：");
        depthFirstSearch(head);
    }

    //广度优先遍历是使用队列实现的
    public static void BroadFirstSearch(TreeNode nodeHead) {
        if(nodeHead==null) {
            return;
        }
        Queue<TreeNode> myQueue=new LinkedList<>();
        myQueue.add(nodeHead);
        while(!myQueue.isEmpty()) {
            TreeNode node=myQueue.poll();
            System.out.print(node.val+" ");
            if(null!=node.left) {
                myQueue.add(node.left);    //深度优先遍历，我们在这里采用每一行从左到右遍历
            }
            if(null!=node.right) {
                myQueue.add(node.right);
            }

        }
    }

    //深度优先遍历
    public static void depthFirstSearch(TreeNode nodeHead) {
        if(nodeHead==null) {
            return;
        }
        Stack<TreeNode> myStack=new Stack<>();
        myStack.add(nodeHead);
        while(!myStack.isEmpty()) {
            TreeNode node=myStack.pop();    //弹出栈顶元素
            System.out.print(node.val+" ");
            if(node.right!=null) {
                myStack.push(node.right);    //深度优先遍历，先遍历左边，后遍历右边,栈先进后出
            }
            if(node.left!=null) {
                myStack.push(node.left);
            }
        }

    }


}
