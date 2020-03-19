package algorithm.Interview.practice02;

/**
 * @author mood321
 * @date 2020/3/20 0:25
 * @email 371428187@qq.com
 */
public class P_2_15_SelectionSortNode {

    public static  class Node {
        Integer data;
        Node next;

        public Node(Integer data) {
            this.data = data;
        }
    }
      public  static  Node selectionSort(Node head){
        if(head ==null){
            return head;
        }
        // 声明需要变量
          Node cur=head;// 未排序头
          Node tail=null; // 已排序尾
          Node small=null; // 未排序最小的
          Node smallPre=null;// 最小前面的  用于重连

          while(cur !=null){
              small=cur;
              smallPre = getSmallPre(cur);
              if(smallPre!= null){
                  small=smallPre.next;
                  smallPre.next=small.next;//
              }
              cur=cur==small?cur.next:cur;
              if(tail == null){
                  tail=small;
              }             else {
                  tail.next=small;
              }
              tail=small;
          }
          return head;

      }

    private static Node getSmallPre(Node head) {
        Node smallPre=null;
        Node small= head;
        Node cur=head.next;
        Node pre=head;

        while (cur!=null){
            if(cur.data < small.data){
                smallPre=pre;
                small=cur;
            }
            pre=cur;
            cur=cur.next;
        }
        return smallPre;
    }

    public static void main(String[] args) {
        Node node = new Node(1);
        Node node1 = new Node(3);
        Node node3 = new Node(5);
        Node node4 = new Node(2);
        node.next=node1;node1.next=node3;node3.next=node4;
        print(selectionSort(node))      ;
    }
    private static void print(Node addList1) {
        while(addList1 !=null)       {
            System.out.print(addList1.data+"  ");
            addList1=addList1.next;
        }

    }
}