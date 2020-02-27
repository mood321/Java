package algorithm.leetcode;

/**
 * @author mood321
 * @date 2020/2/28 0:29
 * @email 371428187@qq.com
 * 给出两个 非空 的链表用来表示两个非负的整数。其中，它们各自的位数是按照 逆序 的方式存储的，并且它们的每个节点只能存储 一位 数字。
 *
 * 如果，我们将这两个数相加起来，则会返回一个新的链表来表示它们的和。
 *
 * 您可以假设除了数字 0 之外，这两个数都不会以 0 开头。
 *
 * 示例：
 *
 * 输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)
 * 输出：7 -> 0 -> 8
 * 原因：342 + 465 = 807
 *
 * 来源：力扣（LeetCode.md）
 * 链接：https://leetcode-cn.com/problems/add-two-numbers
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class addTwoNumbers {
    public class ListNode {
     int val;
     ListNode next;
     ListNode(int x) { val = x; }
 }
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode root = new ListNode(0);
        ListNode list = root;
        int sum=0;
        while (l1 != null || l2 !=null || sum!= 0) {

            int i1 = l1 == null ? 0 : l1.val;
            int i2 = l2 == null ? 0 : l2.val;
            int i3 = i1 + i2 + sum;
            sum=i3/10;
            list.next= new ListNode(i3 % 10);
            list=list.next;
            l1=l1==null ? null :l1.next;
            l2=l2==null ? null :l2.next;
        }
        return root.next;
    }
}