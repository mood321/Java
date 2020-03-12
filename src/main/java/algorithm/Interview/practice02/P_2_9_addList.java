package algorithm.Interview.practice02;

import java.util.Stack;


/**
 * @author mood321
 * @date 2020/3/13 0:24
 * @email 371428187@qq.com
 */
public class P_2_9_addList {

    public  static  class Node{
        Integer data;

        Node next;

        public Node(Integer data) {
            this.data = data;
        }
    }

    // 方法一  用额外空间
    // 和leetcode 实现不太一样  
    public  static  Node addList1(Node list1,Node list2){

        Stack<Integer>  s1=new Stack<>();
        Stack<Integer>  s2=new Stack<>();

        while (list1!=null){
            s1.push(list1.data);
            list1=list1.next;
        }
        while (list2!=null){
            s2.push(list2.data);
            list2=list2.next;
        }
        int  n1=0;
        int  n2=0;
        int  sum=0;
        Node node= null; // 新节点
        Node pre=null;        // 原节点
        while(!s1.isEmpty() || !s2.isEmpty() || sum !=0){
           n1= s1.isEmpty()?0:s1.pop();
           n2= s2.isEmpty()?0:s2.pop();
           int t=n1+n2+sum;
          node = new Node(t%10);
            node.next= pre  ;
           pre=node;
           sum=t/10;
        }
        return  node;

    }
    // 方法二  不用额外空间
    public  static  Node addList2(Node list1,Node list2){
        Node node1 = reverseNode(list1);
        Node node2 = reverseNode(list2);
        list1=node1;
        list2=node2;
        //   这儿的逻辑一样
        int  n1=0;
        int  n2=0;
        int  sum=0;
        Node node= null; // 新节点
        Node pre=null;        // 原节点
        while(node1!=null  || node2!=null || sum !=0){
            n1= node1==null ?0:node1.data;
            n2= node2==null ?0:node2.data;
            int t=n1+n2+sum;
            node = new Node(t%10);
            node.next= pre  ;
            pre=node;
            sum=t/10;
            node1=node1==null? null:node1.next;
            node2=node2==null?null:node2.next;
        }
        reverseNode(list1);
        reverseNode(list2);
        return pre;
    }

    private static Node reverseNode(Node list1) {
        Node next=null;
        Node pre=null;
        while (list1!=null){
            next= list1.next;
             list1.next=pre;
            pre=list1;
            list1=next;
        }
        return pre;
    }

    public static void main(String[] args) {

        Node node = new Node(0);
        Node node1 = new Node(1);
        Node node2 = new Node(8);
        node1.next=node2;
       print( addList2(node,node1));
    }

    private static void print(Node addList1) {
        while(addList1 !=null)       {
            System.out.print(addList1.data+"  ");
           addList1=addList1.next;
        }

    }
}