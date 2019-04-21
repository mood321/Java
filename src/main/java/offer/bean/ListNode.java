package offer.bean;

public class ListNode <T>{
    public int val;
    public ListNode<T> next; //下一个节点

    public ListNode(int x){ //构造函数的初始化
        val = x;
        next = null;
    }
}
