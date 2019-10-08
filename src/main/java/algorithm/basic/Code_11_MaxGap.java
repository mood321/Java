package algorithm.basic;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @Created by mood321
 * @Date 2019/10/6 0006
 * @Description 给定一个数组，求如果排序之后，相邻两数的最大差值，
 * 要求时间复杂度O(N)，且要求不能用非基于比较的排序
 */
public class Code_11_MaxGap {

    /**
     * 1 找出数据项最大 最小 用作分桶 (最小放第一个桶  最大放在最后一个桶)
     * 2 按某种规律放到桶中
     * 3 最大差值必定是一个非空桶的小值 和前非空桶的差值最大的一个 找出最大的一个
     *
     * @param nums
     * @return
     */
    public static int maxGap(int[] nums) {
        if (nums == null || nums.length < 2) {
            return 0;
        }

        // 对数器 是生产 整数类型 可能带符号 所以默认0 并不好
        int len = nums.length;
        int numMax = Integer.MIN_VALUE;
        int numMin = Integer.MAX_VALUE;
        for (int i = 0; i < nums.length; i++) {
            numMin = Math.min(numMin, nums[i]);
            numMax = Math.max(numMax, nums[i]);
        }

        if (numMax == numMin) {
            return 0;
        }
        // 定义三个数组 分别代表桶的三个属性  是否存在值  最大值  最小值
        boolean[] hasNum = new boolean[len + 1];
        int[] maxs = new int[len + 1];
        int[] mins = new int[len + 1];

        int bid = 0;
        for (int i = 0; i < len; i++) {
            bid = bucket(nums[i], len, numMin, numMax);
            mins[bid] = hasNum[bid] ? Math.min(mins[bid], nums[i]) : nums[i];// 如果这个桶原来有最大值 比较 把最大值赋值 没有把nums的值放进去
            maxs[bid] = hasNum[bid] ? Math.max(maxs[bid], nums[i]) : nums[i];//同上
            hasNum[bid] = true;//赋值完成  标记已经赋值
        }

        // 最大差值必定是一个非空桶的小值 和前非空桶的差值最大的一个 找出最大的一个
        int res = 0;
        int lastMax = maxs[0];
        int i = 1;
        for (; i <= len; i++) {
            if (hasNum[i]) {

                res = Math.max(res, mins[i] - lastMax); // 比较原最大值差值和目前遍历的差值谁打 找出最大的
                lastMax = maxs[i];  // 开始下一个比较
            }
        }


        return res;
    }

    // 依据当前数值 桶大小-1（有一个桶 留空）  桶最大值 桶最小值  计算出  当前值应该放在哪一个桶
    //  这个方法 保证了 最小放第一个桶  最大放在最后一个桶
    public static int bucket(long num, long len, long min, long max) {
        return (int) ((num - min) * len / (max - min));
    }

    // for test
    public static int comparator(int[] nums) {
        if (nums == null || nums.length < 2) {
            return 0;
        }
        Arrays.sort(nums);
        int gap = Integer.MIN_VALUE;
        for (int i = 1; i < nums.length; i++) {
            gap = Math.max(nums[i] - nums[i - 1], gap);
        }
        return gap;
    }

    // for test
    public static int[] generateRandomArray(int maxSize, int maxValue) {
        int[] arr = new int[(int) ((maxSize + 1) * Math.random())];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (int) ((maxValue + 1) * Math.random()) - (int) (maxValue * Math.random());
        }
        return arr;
    }

    // for test
    public static int[] copyArray(int[] arr) {
        if (arr == null) {
            return null;
        }
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = arr[i];
        }
        return res;
    }

    // for test
    public static void main(String[] args) {
        int testTime = 500000;
        int maxSize = 100;
        int maxValue = 100;
        boolean succeed = true;
        for (int i = 0; i < testTime; i++) {
            /*   int[] arr1 = generateRandomArray(maxSize, maxValue);*/
            int[] arr1 = {1, 2, 3, 5};
            int[] arr2 = copyArray(arr1);
            int i1 = maxGap(arr1);
            int comparator = comparator(arr2);
            if (comparator != i1) {
                System.out.println("i1=" + i1 + "   comparator=" + comparator);
                succeed = false;
                break;
            }
        }
        System.out.println(succeed ? "Nice!" : "Fucking fucked!");
    }

}
