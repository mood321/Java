package algorithm.Interview.practice02;

/**
 * @author mood321
 * @date 2020/3/6 23:58
 * @email 371428187@qq.com
 */
public class P_2_7_ListPartiton {

    public static class Node {
         int data;
         Node next;

        public Node(int data) {
            this.data = data;
        }
    }

    public static Node listPartition2(Node head, int num) {
        if (head == null) {
            return head;
        }
        Node lS=null;
        Node lE=null;   // 小的部分的 头尾
        Node cS=null;
        Node cE=null;   // 相等的部分的 头尾
        Node rS=null;
        Node rE=null;  // 大的部分的 头尾
      Node cur=head;
      while (cur != null){
          if(cur.data< num){
              if(lS== null){
                  lS=cur;
                  lE=cur;
              } else  {
                  lE.next=cur;
                  lE=cur;
              }
          } else if(cur.data== num) {
              if(cS== null){
                  cS=cur;
                  cE=cur;
              } else  {
                  cE.next=cur;
                  cE=cur;
              }
          } else {
              if(rS== null){
                  rS=cur;
                  rE=cur;
              } else  {
                  rE.next=cur;
                  rE=cur;
              }

          }
          cur=cur.next;
      }

      // 重连
        if(lE !=null){          // 小的和相等  合并
            lE.next=cS;
            cE = cE == null ? lE : cE;
        }
        if(cE !=null){       // 和大的合并
            cE.next=rS;
        }
         if(rS !=null){   // 记得处理原链表的 next   也可以在放到各自链表时处理
             rE.next=null;
         }else {
             cE.next=null;
         }

        return  lS!=null ? lS: cS!=null ?cS:rS;      // 必定 不是三个都是null  是两次三元

    }


    public static Node listPartition(Node head, int num) {
        if (head == null) {
            return head;
        }
        Node cur = head;
        int length = 0;
        while (cur != null) {
            length++;
            cur = cur.next;
        }
        Node[] nodes = new Node[length];
        cur = head;
        length = 0;
        while (cur != null) {
            nodes[length] = cur;
            length++;
            cur = cur.next;
        }
        arrNodepartition(nodes, num);
        for (int i = 0; i < nodes.length - 1; i++) {
            nodes[i].next = nodes[i + 1];
        }
        nodes[nodes.length - 1].next = null;// 原节点next  可能有值
        return nodes[0];
    }

    /**
     * 数组的partition
     *
     * @param nodes
     * @param num
     * @param head
     */
    private static void arrNodepartition(Node[] nodes, int num) {
        int left = -1;
        int right = nodes.length;
        int index = 0;
        while (left != right) {
            if (nodes[index].data < num) {
                swap(nodes, ++left, index++);
            } else if (nodes[index].data == num) {
                index++;
            } else if (nodes[index].data > num) {
                swap(nodes, --right, index);
            }
        }
    }

    private static void swap(Node[] nodes, int i, int i1) {
        Node tem = nodes[i];
        nodes[i] = nodes[i1];
        nodes[i1] = tem;
    }
    public static void main(String[] args) {
        System.out.println("----------测试第一种方法----------");
        Node head = new Node(9);
        head.next = new Node(0);
        head.next.next = new Node(4);
        head.next.next.next = new Node(5);
        head.next.next.next.next = new Node(1);
        printList(head);
        head = listPartition(head,3);
        printList(head);


        System.out.println("----------测试第二种方法----------");
        Node head2 = new Node(9);
        head2.next = new Node(0);
        head2.next.next = new Node(4);
        head2.next.next.next = new Node(5);
        head2.next.next.next.next = new Node(1);
        printList(head2);
        head2 = listPartition2(head2,3);
        printList(head2);
    }
    private static void printList(Node head) {
        while(head !=null)
        {
            System.out.print( head.data +"  ");
            head=head.next;
        }
        System.out.println();
    }
}