package algorithm.Interview.practice02;

/**
 * @author mood321
 * @date 2020/3/16 0:16
 * @email 371428187@qq.com
 */
public class P_2_10_LookNode {
    public static class Node {
        private int data;
        private Node next;

        public Node(int data) {
            this.data = data;
        }
    }

    /**
     * 主方法
     *
     * @param node1
     * @param node2
     * @return
     */
    public static Node getInterSectNode(Node node1, Node node2) {
        if (node1 == null || node2 == null) {
            return null;
        }
        Node lookNode = getLookNode(node1);
        Node lookNode2 = getLookNode(node2);

        if (lookNode == null && lookNode2 == null) {
            return noLook(node1, node2);
        } else if(lookNode != null && lookNode2 != null)  {
             return bothLook(node1,node2,lookNode,lookNode2);
        }
            return null;

    }

    /**
     *  到这 是都有环的情况
     * @param node1
     * @param node2
     * @param lookNode
     * @param lookNode2
     * @return
     */
    public static Node bothLook(Node node1, Node node2, Node lookNode, Node lookNode2) {
        if(lookNode == lookNode2){    // 入环节点一样
            Node next = lookNode.next;
            Node next2 = lookNode2.next;
            lookNode.next=null;
            lookNode2.next=null;
            Node no=noLook(node1,node2);
            lookNode.next=next;
            lookNode2.next=next2;
            return no;
        } else {
          Node cur=lookNode;
          while(cur != lookNode){ // 绕环一周
              if(cur == lookNode2){   // 遇到另一个入环节点
                 return cur;
              }
              cur=cur.next;
          }
          
        }
       return null;
    }

    /**
     * 两链表都无环
     *
     * @param node1
     * @param node2
     * @return
     */
    public static Node noLook(Node node1, Node node2) {

        if (node1 == null || node2 == null) {
            return null;
        }
        Node cur = node1;
        Node cur2 = node2;
        int n = 0;
        while (cur != null) {
            n++;
            cur = cur.next;
        }
        while (cur2 != null) {
            n--;
            cur2 = cur2.next;
        }
        cur = n > 0 ? node1 : node2;// 这是n 大于0  则node1 更长
        cur2 = cur == node1 ? node2 : node1;
        n = Math.abs(n);
        while(n!=0){     // 长度超长的 先走长度
            n--;
            cur=cur.next;
        }

        while (cur != cur2){
            cur=cur.next;
            cur2=cur2.next;
        }

        return  cur;
    }

    /**
     * 判断一个链表是否有环
     * 双指针
     * 快指针到null 一定无环
     * 快慢指针相遇 有环  快指针回到头结点开始慢走  再次相遇节点为入环节点  证明详情参见百度
     */
    public static Node getLookNode(Node head) {
        if (head == null || head.next == null || head.next.next == null) {
            return null;
        }
        Node n1 = head;// 快
        Node n2 = head;// 慢
        while (n1 != n2) {
            if (n2.next == null || n2.next.next == null) {
                return null;
            }
            n2 = n2.next.next;
            n1 = n1.next.next;
        }
        n2 = head;
        while (n1 != n2) {
            n1 = n1.next;
            n2 = n2.next;
        }
        return n1;
    }


}