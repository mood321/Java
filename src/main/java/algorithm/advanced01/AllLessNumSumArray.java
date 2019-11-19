package algorithm.advanced01;

import java.util.LinkedList;

/**
 * @author mood321
 * @date 2019/11/20 0:12
 * @email 371428187@qq.com
 * <p></>最大值减去最小值 小于等于num的子数组数量
 * <p> 给定数组arr 和整数num 一共多少组 子数组的最大 最小满足
 */
public class AllLessNumSumArray {

    /**
     * 暴力方式
     *
     * @param arr
     * @param num
     * @return
     */
    public static int getNum(int[] arr, int num) {
        int res = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = i; j < arr.length; j++) {
               if(isValid(arr, i, j, num))  {
                   if(j== arr.length){
                       System.out.println(1);
                   }
                   res++;
               }

            }
        }
        return res;
    }

    // 判断子数组 是否是有效的
    private static boolean isValid(int[] arr, int i, int j, int num) {
        if(i==j){
            return false;
        }
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (int k = i; k < j; k++) {
            max = Math.max(max, arr[k]);
            min = Math.min(min, arr[k]);
        }
        return max - min <= num;
    }

    /**
     * 利用窗口 完成统计
     */

    public static int getNum2(int[] arr, int num) {
        if (arr == null || arr.length == 0)
            return 0;

        int L = 0, R = 0, res = 0;
        LinkedList<Integer> max = new LinkedList<>();
        LinkedList<Integer> min = new LinkedList<>();

        while (L < arr.length) {
            while (R < arr.length) {     //   L  R  都值往前推  不会退   所以方法是O(N)

                // 维护小的窗口
                while (!min.isEmpty() && arr[R] <= arr[min.peekLast()])  {
                    min.pollLast();
                }
                min.addLast(R);

                // 维护 最大值的窗口
                while (!max.isEmpty() && arr[R] >= arr[max.peekLast()]) {
                    max.pollLast();
                }
                max.addLast(R);
                // 如果  现在子数组 最大最小 已经大于num
                if (arr[max.peekFirst()] - arr[min.peekFirst()] > num) {
                    break;
                }
                // 还是小于等于 num  R向后推
                R++;
            }
            res += R - L; // 加上 这时 以L 为起点满足条件的个数
            // 这时 L 向后推
            //如果在数组 L到R 范围的子数组 满足条件  L，R之间的任何一个子数组满足条件
            // 因为上面的思路 所以 R可以补回退  只判断 R是否向后推
            L++;
        }

        return res;
    }

    public static void main(String[] args) {
        int[] ints = {1,2,3,4,5};
        System.out.println(getNum(ints,3));
        System.out.println(getNum2(ints,3));
    }
}