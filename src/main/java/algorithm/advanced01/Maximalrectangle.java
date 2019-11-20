package algorithm.advanced01;

import java.util.Stack;

/**
 * @author mood321
 * @date 2019/11/21 0:24
 * @email 371428187@qq.com
 *
 * @desc  用单调栈 解决二维数组 最大矩阵的问题
 */
public class Maximalrectangle {
    public static int maxRectSize(int[][] map){
        if(map==null || map.length==0 || map[0]==null || map[0].length==0){
            return 0;
        }
        int res=0;
        int[] arr = new int[map[0].length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                arr[j] = map[i][j] == 0 ? 0 : arr[j] + 1;
            }
            
            res =Math.max(maxRectFromBotton(arr),res);
        }

        return res;
    }

    // 单调栈方法 求出每一一维数组最大
    public static  int maxRectFromBotton(int[] arr){
        if(arr==null || arr.length==0){
            return 0;
        }

        int res=0;
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < arr.length; i++) {
            while (! stack.isEmpty()&& arr[stack.peek()]>=arr[i]){
                Integer j = stack.pop();
                int k = stack.isEmpty() ? -1 : stack.peek();
                int cur = (i - k -1) * arr[j];
                res=Math.max(cur,res);
            }
            stack.push(i);
        }
        while (! stack.isEmpty()){
            Integer j = stack.pop();
            int k = stack.isEmpty() ? -1 : stack.peek();
            int cur = (arr.length - k - 1) * arr[j];
            res=Math.max(cur,res);
        }
           return  res;

    }

    public static void main(String[] args) {
       int[][] ints= {
               {  1,0,1,1},
               {  1,1,1,1},
               {  1,1,1,0}} ;
        System.out.println(maxRectSize(ints));
    }
}