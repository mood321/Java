package algorithm.Interview.practice02;

import lombok.Data;

/**
 * @author mood321
 * @date 2020/2/26 23:51
 * @email 371428187@qq.com
 * 普通版和进阶   每一个结点,都要走m步,所有时间复杂度为O(m*n),进阶解法要求做到时间复杂度O(n)
 * 进阶: 要求O(N)     这个没怎么研究  太久没看数学
 */
public class P_2_5_Josephu {

    public static class Node {
        int data;
        Node next;

        public Node(int data) {
            this.data = data;
        }

    }

    public static Node josephusKill(Node head, int m) {
        if (head == null || head.next == head || m < 1) {
            return head;
        }
        Node last = head;//
        while (last.next != head) {    // last 第一个作用 找到最后节点
            last = last.next;
        }
        int count=0;
        while (last !=head) {     // 这里的 head  last 只是复用   head是当前的节点  last是指向他的节点

            if(++count !=m){
                last   =last.next;
            }else {
                last.next=head.next;
                count=0;
            }
            head=last.next;
        }
        return head;
    }
}