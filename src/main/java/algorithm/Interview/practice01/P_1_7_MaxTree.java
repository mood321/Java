package algorithm.Interview.practice01;

import java.util.HashMap;
import java.util.Stack;

/**
 * @author mood321
 * @date 2020/1/2 22:07
 * @email 371428187@qq.com
 */
public class P_1_7_MaxTree {

    public static   class Node{
        public int value;
        public Node left;
        public Node right;
        public Node(int data){
            this.value=data;
        }
    }


    public static Node getMaxTree(int[] arr) {
        Node[] nodes = new Node[arr.length];
        for (int i = 0; i < arr.length; i++) {
                nodes[i]=new Node(arr[i]);
        }
       // 大概三步
        // 1 找出左大
        // 2 找出右大
        // 3 构建树
        Stack<Node> stack = new Stack<Node>();
        HashMap<Node, Node> lmap = new HashMap<>();
        HashMap<Node, Node> rmap = new HashMap<>();
        // 逻辑还是窗口 找大值那个逻辑  栈中存有序的数 如果现在要加一个比栈顶大的数进去  就把那些小的弹出来
        for (int i = 0; i < nodes.length; i++) {
            Node cur = nodes[i];
            while(!stack.isEmpty() && stack.peek().value< cur.value){
                popSetMap(stack,lmap);

            }
            stack.push(cur);
        }
        while (!stack.isEmpty()){
            popSetMap(stack,lmap);

        }
        // 找右边的逻辑相反
        for (int i = nodes.length-1; i >=0 ; i--) {
            Node cur = nodes[i];
            while(!stack.isEmpty() && stack.peek().value< cur.value){
                popSetMap(stack,rmap);

            }
            stack.push(cur);
        }
        while (!stack.isEmpty()){
            popSetMap(stack,rmap);

        }
        // 这时候开始构建树

        // 逻辑 数组里面有所有的节点
        // 没有左右的是 头
        // 有左右的放在小的小面

        Node head=null;

        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            Node left = lmap.get(node);
            Node right = rmap.get(node);
            if(left==null && right==null){
                head=node;
            }else if(left==null){
                if(right.left==null){
                    right.left=node;

                }else {
                    right.right=node;
                }
            } else if(right==null) {
                if(left.left==null){
                    left.left=node;

                }else {
                    left.right=node;
                }

            }else {
               Node parent=left.value>right.value?right:left;
                if(parent.left==null){
                    parent.left=node;

                }else {
                    parent.right=node;
                }
            }
        }
        return  head;
    }

    private static void popSetMap(Stack<Node> stack, HashMap<Node, Node> map) {
        Node pop = stack.pop();
        if(stack.isEmpty()){
            map .put(pop,null);
        } else {
            map.put(pop,stack.peek());

        }
    }
    public static void main(String[] args) {
        int[] arr=new int[]{3,4,5,1,2};
        Node maxTree = getMaxTree(arr);
        System.out.println();
    }
}