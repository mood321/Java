package algorithm.Interview.practice03;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author mood321
 * @date 2020/4/28 0:55
 * @email 371428187@qq.com
 */
public class P_3_8_PrintByLevel {
    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }
        // 
    public static void printLevel(Node head) {
        if (head == null) {
            return;
        }
        Deque<Node> queue = new LinkedList<>();
        int level = 1;
        Node  last=head;
        Node  nLast=null;
        queue.offer(head);
        System.out.print( " Level "+(level++));

        System.out.print("Level " + level++ + " : ");
        while (!queue.isEmpty()) {
            head = queue.poll();
            System.out.print(head.value + " ");
            if (head.left != null) {
                queue.offer(head.left);
                nLast = head.left;
            }
            if (head.right != null) {
                queue.offer(head.right);
                nLast = head.right;
            }
            if (head == last && !queue.isEmpty()) {
                System.out.println();
                System.out.print("Level " + level++ +" : ");
                last = nLast;
            }
        }

    }


    //二叉树按照Ziazag打印(利用双端队列)
    public static void ZigzagPrint(Node head)
    {
        if(head==null)
        {
            return;
        }
        /**
         System.out.print(head.value);
         if(head.right!=null)
         {
         System.out.print(head.right.value);
         }
         if(head.left!=null)
         {
         System.out.print(head.left.value);
         }
         */
        //运用双端队列的方式存储(基数行左->右,偶数行右->左)
        Deque<Node>dq=new LinkedList<Node>();  //双端队列
        int level=1;
        boolean lr=true;  //记录打印的方向
        Node last=head; //当前行的最后节点
        Node nlast=null; //下一行的最后节点
        dq.offerFirst(head);
        printLevelAndOrientation(level++,lr);
        while(!dq.isEmpty())
        {
            if(lr) //奇数行(尾进头出)
            {
                head=dq.pollFirst();
                if(head.left!=null)
                {
                    nlast=nlast==null?head.left:nlast;
                    dq.offerLast(head.left);
                }
                if(head.right!=null)
                {
                    nlast=nlast==null?head.right:nlast;
                    dq.offerLast(head.right);
                }

            }else //偶数行(头进尾出)
            {
                head=dq.pollLast();
                if(head.right!=null)
                {
                    nlast=nlast==null?head.right:nlast;
                    dq.offerFirst(head.right);
                }
                if(head.left!=null)
                {
                    nlast=nlast==null?head.left:nlast;
                    dq.offerFirst(head.left);
                }
            }
            System.out.print(head.value+" ");

            if(head==last&&!dq.isEmpty())
            {
                lr=!lr; //进行换行的操作
                last=nlast;
                nlast=null;
                System.out.println();
                printLevelAndOrientation(level++,lr);
            }

        }
        System.out.println();

    }

    //显示打印的方向
    public static void printLevelAndOrientation(int level,boolean lr)
    {
        System.out.print("Level"+level+" from ");
        System.out.print(lr?"left to right ":"right to left: ");
    }

}