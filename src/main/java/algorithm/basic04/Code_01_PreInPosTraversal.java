package algorithm.basic04;

import java.util.Stack;

/**
 * @Created by mood321
 * @Date 2019/11/1 0001
 * @Description  二叉树的前中后 序遍历
 */
public class Code_01_PreInPosTraversal {

    //基本结构
    public static  class Node{
        public Integer data;
        public Node left;
        public Node right;
        public Node(int data){
            this.data=data;
        }
    }

    // 使用递归 前序
    public static void preOrderRecur(Node head) {
        if(head==null){
            return ;
        }
        System.out.print(head.data);
        preOrderRecur(head.left);
        preOrderRecur(head.right);
    }
    // 使用递归 中序
    public static void inOrderRecur(Node head) {
        if(head==null){
            return ;
        }

        inOrderRecur(head.left);
        System.out.print(head.data);
        inOrderRecur(head.right);
    }
    // 使用递归 后序
    public static void posOrderRecur(Node head) {
        if(head==null){
            return ;
        }
        posOrderRecur(head.left);
        posOrderRecur(head.right);
        System.out.print(head.data);
    }
    // 非递归 前序
    // 思路 : 默认头结点 压栈  出栈   顺序由栈维护  先压右子节点  然后左子节点
    public static void preOrderUnRecur(Node head) {
        System.out.print("pre-order: ");
        if(head==null)
            return ;
        Stack<Node> stack = new Stack<>();
        stack.push(head);
        while (!stack.isEmpty()){ //
            Node pop = stack.pop();
            System.out.print(pop.data+"   ");
            if(pop.right!=null){
                stack.push(pop.right);
            }
            if(pop.left!=null){
                stack.push(pop.left);
            }
        }
        System.out.println();
    }
    // 非递归 中序
    // 思路： 先从父节点 先把左子节点 全部压栈   为null 打印  回到父节点 到父节点的右子节点继续
    // coding重点  节点不为null 压栈 到左子节点  为空从栈取父节点 到右子节点
    //
    public static void inOrderUnRecur(Node head) {
        System.out.print("in-order: ");
        if(head==null)
            return;
        Stack<Node> stack = new Stack<>();
        while(!stack.isEmpty()|| head!=null){
            if(head!=null){
                stack.push(head);
                head=head.left;
            }else {
                head=stack.pop();
                System.out.print(head.data+" ");
                head=head.right;
            }
        }

    }
    // 非递归 后序
    // 思路: 由于前序非递归的 顺序 是 中左右  很容易改出中右左  然后再利用栈 很容易得到 左右中 (左神牛逼,这个思路真的吊)
    public static void posOrderUnRecur(Node head) {
        System.out.print("pos-order: ");
        if(head==null)
            return;
        Stack<Node> pre = new Stack<>();
        Stack<Node> pos = new Stack<>();
        pre.push(head);
        while (!pre.isEmpty()){
            Node n = pre.pop();
            pos.push(n);
            if(n.left!=null){
                pre.push(n.left);
            }
            if(n.right!=null){
                pre.push(n.right);
            }
        }
        // //////
        while (!pos.isEmpty()){
            System.out.print(pos.pop().data+"  ");
        }

    }
    public static void posOrderUnRecur2(Node h) {
        System.out.print("pos-order: ");
        if (h != null) {
            Stack<Node> stack = new Stack<Node>();
            stack.push(h);
            Node c = null;
            while (!stack.isEmpty()) {
                c = stack.peek();// 取出目前最栈内 最左节点  但不弹出
                if (c.left != null && h != c.left && h != c.right) { // 有左子节点 加入栈  下一次最左就是这个节点的左子节点  如果null 右子节点入栈  继续逻辑
                    stack.push(c.left);
                } else if (c.right != null && h != c.right) {// 在当前左子节点为null 时 判断右子 如果不为null 继续逻辑 为null 直接弹出
                    stack.push(c.right);
                } else {// 弹出  h 回到父节点  上面两个判断这时就要 刚弹出的的节点 不可以重复入栈
                    System.out.print(stack.pop().data + " ");
                    h = c;
                }
            }
        }
        System.out.println();
    }
    public static void main(String[] args) {
        Node head = new Node(5);
        head.left = new Node(3);
        head.right = new Node(8);
        head.left.left = new Node(2);
        head.left.right = new Node(4);
        head.left.left.left = new Node(1);
        head.right.left = new Node(7);
        head.right.left.left = new Node(6);
        head.right.right = new Node(10);
        head.right.right.left = new Node(9);
        head.right.right.right = new Node(11);

        // recursive
        System.out.println("==============recursive==============");
        System.out.print("pre-order: ");
        preOrderRecur(head);
        System.out.println();
        System.out.print("in-order: ");
        inOrderRecur(head);
        System.out.println();
        System.out.print("pos-order: ");
        posOrderRecur(head);
        System.out.println();

        // unrecursive
        System.out.println("============unrecursive=============");
        preOrderUnRecur(head);
        inOrderUnRecur(head);
        posOrderUnRecur2(head);
        posOrderUnRecur(head);

    }

}
