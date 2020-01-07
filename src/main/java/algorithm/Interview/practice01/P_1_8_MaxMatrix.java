package algorithm.Interview.practice01;

import java.util.Stack;

/**
 * @author mood321
 * @date 2020/1/8 0:00
 * @email 371428187@qq.com
 *
 *
 *   最大矩阵的大小
 *   : 这实际分成了两部分
 *   1 当前列有多少个
 *   2 当前数前面列的最大矩阵
 *   <p> 实际上 我认为这儿是一种DP

 */
public class P_1_8_MaxMatrix {
    /**
     *  1 先拿到每一行的 高
     */
    public static int    maxRectSize(int[][] arr){
        if(arr==null || arr.length<1 || arr[0]==null || arr[0].length<1){
            return  0;
        }

        int res=0;
        int[] hight = new int[arr[0].length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                  hight[j]= arr[i][j]==1 ? hight[j]+1:0;
            }
        res=Math.max(  maxRecFromBottom(hight),res);
        }
        return res;

    }

    /**
     * 
     * @param arr
     * @return
     *
     * 得到 每一行最大
     *     1 1 0 1 0 0
     *     2 2 1 2 0 1
     *     3 3 2 3 1 2   *****
     *
     *    重写了一次  发现流程蛮杂的   
     */
    private static  int maxRecFromBottom(int[] arr) {
          if(arr== null || arr.length <1){
              return 0;
          }
          int res=0;
        Stack<Integer> stack = new Stack<>();     // 单调栈  这是个递减的
        // 数据  3 3 2 3 1 2 
        for (int i = 0; i < arr.length; i++) {
             while( !stack.isEmpty() && arr[stack.peek()] >= arr[i]){     // 如果不满足递减  即:要大于等于要加入的   弹出

                 Integer j = stack.pop();     // 弹出
                 int k = stack.isEmpty() ? -1 : stack.peek(); // 
                res= Math.max((i-k-1 )*arr[j],res);//
             }
             stack.push(i);
        }
        while (!stack.isEmpty()) {
            Integer j = stack.pop();     // 弹出
            int k = stack.isEmpty() ? -1 : stack.peek(); //
            res= Math.max((arr.length-k-1 )  *arr[j],res);//

        }
        System.out.println(res);
        return res;
    }
    public static void main(String[] args) {
        int[][] ints= {
                {  1,0,1,1},
                {  1,1,1,1},
                {  1,1,1,0}} ;
        System.out.println(maxRectSize(ints));
    }
}