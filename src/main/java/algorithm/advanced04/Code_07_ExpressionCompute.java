package algorithm.advanced04;

import java.util.LinkedList;

/**
 * @author mood321
 * @date 2019/12/2 0:43
 * @email 371428187@qq.com
 */
public class Code_07_ExpressionCompute {
    public static void main(String[] args) {
        String exp = "48*((70-65)-43)+8*1";
        System.out.println(getValue(exp));

        exp = "4*(6+78)+53-9/2+45*8";
        System.out.println(getValue(exp));

        exp = "10-5*3";
        System.out.println(getValue(exp));

        exp = "-3*4";
        System.out.println(getValue(exp));

        exp = "3+1*4";
        System.out.println(getValue(exp));

    }
    public static int getValue(String str) {
        return value(str.toCharArray(), 0)[0];
    }

    private static int[] value(char[] str, int i) {   // 数组 0 为结果  1 为下标
        LinkedList<String> queue = new LinkedList<>();
         int pre=0;
         int[] arr=new int[2];
         while (i<str.length && ')'!=str[i])    {
                if(str[i]<='9' && str[i]>='0'){
                    pre=pre*10 +str[i++]-'0';
                }else if('('!=str[i]){      // 进入while 中    三种情况 1 数字 2 符号 3 （  // 这一定是符号
                    addNum(queue, pre);  // 把符号前面的数字 入栈
                    queue.addLast(String.valueOf(str[i++]));
                    pre = 0;
                }  else {    // 这儿一定是 ( 
                    arr = value(str, i + 1);    // 子过程
                    pre = arr[0]; // 计算结果
                    i = arr[1] + 1;    // 子过程 计算到的位置
                }
         }
        addNum(queue, pre);   // 自己 遇到） 
        return new int[] { getNum(queue), i };
    }

    public static int getNum(LinkedList<String> que) {   // 得到整个栈的结果
        int res = 0;
        boolean add = true;
        String cur = null;
        int num = 0;
        while (!que.isEmpty()) {
            cur = que.pollFirst();
            if (cur.equals("+")) {
                add = true;
            } else if (cur.equals("-")) {
                add = false;
            } else {
                num = Integer.valueOf(cur);
                res += add ? num : (-num);
            }
        }
        return res;
    }

    private static void addNum(LinkedList<String> queue, int pre) {    // 把一个数 入栈
        if(!queue.isEmpty()){
            int cur=0;
            String top = queue.pollLast();
            if(top.equals("+")|| top.equals("-")){
                queue.addLast(top);// + -  直接加入栈
            } else {
                cur=Integer.valueOf(queue.pollLast());
                pre=top.equals("*")   ?pre*cur:cur/pre;

            }
        }
        queue.addLast(String.valueOf(pre));
    }


}