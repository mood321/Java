package offer;

import offer.bean.ListNode;

import java.util.ArrayList;
import java.util.Stack;

public class 链表按链表值从尾到头的顺序返回一个ArrayList {
    public static void main(String[] args) {
        ListNode<Integer> listNode = new ListNode<>(1);
        listNode.next=new ListNode<>(2);
        System.out.println(printListFromTailToHead(listNode));
    }

    public static  ArrayList<Integer> printListFromTailToHead(ListNode listNode) {
        Stack<Integer> stack=new Stack();
        while(listNode!=null){
            stack.push(listNode.val);
            listNode=listNode.next;
        }
        ArrayList<Integer> list= new ArrayList<>();
        while(!stack.isEmpty()){
            list.add(stack.pop());
        }
        return list;
    }
}
