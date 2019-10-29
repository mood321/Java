package algorithm.basic03;

/**
 * @Created by mood321
 * @Date 2019/10/30 0030
 * @Description TODO
 */
public class Code_12_SmallerEqualBigger {
    public static class Node {
        int data;
        Node next;

        public Node(int data) {
            this.data = data;
        }
    }

    /**
     * 使用辅助数组
     *
     * @param head
     * @param pivot
     * @return
     */
    public static Node listPartition1(Node head, int pivot) {
        if (head == null) {
            return head;
        }
        int i = 0;
        Node cur = head;
        while (cur != null) {
            cur = cur.next;
            i++;
        }
        Node[] nodeArr = new Node[i];
        i = 0;
        cur = head;
        for (i = 0; i != nodeArr.length; i++) {
            nodeArr[i] = cur;
            cur = cur.next;
        }
        arrPartition(nodeArr, pivot);
        for (i = 1; i != nodeArr.length; i++) {
            nodeArr[i - 1].next = nodeArr[i];
        }
        nodeArr[i - 1].next = null;
        return nodeArr[0];
    }

    // 荷兰国旗问题
    public static void arrPartition(Node[] nodeArr, int pivot) {
        int small = -1;
        int big = nodeArr.length;
        int index = 0;
        while (index != big) {
          if(nodeArr[index].data<pivot){
            swap(nodeArr,++small,index++);
          }else if(nodeArr[index].data>pivot){
              swap(nodeArr,--big,index);
            }else {
              index++;
          }
        }
    }

    public static void swap(Node[] nodeArr, int a, int b) {
        Node tmp = nodeArr[a];
        nodeArr[a] = nodeArr[b];
        nodeArr[b] = tmp;
    }
   // 辅助空间 O(1)
   public static Node listPartition2(Node head, int pivot) {
       Node sH = null; // small head
       Node sT = null; // small tail
       Node eH = null; // equal head
       Node eT = null; // equal tail
       Node bH = null; // big head
       Node bT = null; // big tail
       Node next = null; // save next node
       // every node distributed to three lists
       while (head != null) {
           next = head.next;
           head.next = null;
           if (head.data < pivot) {
               if (sH == null) {
                   sH = head;
                   sT = head;
               } else {
                   sT.next = head;
                   sT = head;
               }
           } else if (head.data == pivot) {
               if (eH == null) {
                   eH = head;
                   eT = head;
               } else {
                   eT.next = head;
                   eT = head;
               }
           } else {
               if (bH == null) {
                   bH = head;
                   bT = head;
               } else {
                   bT.next = head;
                   bT = head;
               }
           }
           head = next;
       }
       // small and equal reconnect
       if (sT != null) {
           sT.next = eH;
           eT = eT == null ? sT : eT;
       }
       // all reconnect
       if (eT != null) {
           eT.next = bH;
       }
       return sH != null ? sH : eH != null ? eH : bH;
   }

    public static void printLinkedList(Node node) {
        System.out.print("Linked List: ");
        while (node != null) {
            System.out.print(node.data + " ");
            node = node.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Node head1 = new Node(7);
        head1.next = new Node(9);
        head1.next.next = new Node(1);
        head1.next.next.next = new Node(8);
        head1.next.next.next.next = new Node(5);
        head1.next.next.next.next.next = new Node(2);
        head1.next.next.next.next.next.next = new Node(5);
        printLinkedList(head1);
        head1 = listPartition1(head1, 4);
      //  head1 = listPartition2(head1, 5);
        printLinkedList(head1);

    }
}
