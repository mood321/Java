package algorithm.advanced06;

import java.util.HashMap;

/**
 * @author mood321
 * @date 2019/12/4 22:40
 * @email 371428187@qq.com
 */
public class Code_05_LongestSubarrayLessSumAwesomeSolution {
    public static int maxLengthAwesome(int[] arr, int aim) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        int[] sums = new int[arr.length];
        
        HashMap<Integer, Integer> ends = new HashMap<Integer, Integer>();  // map 用来记录下标最小值到 哪一个下标   可以用数组记录
        sums[arr.length - 1] = arr[arr.length - 1];
        ends.put(arr.length - 1, arr.length - 1);
        for (int i = arr.length - 2; i >= 0; i--) {    // 倒序 到每个位置的最小累加
            if (sums[i + 1] < 0) {
                sums[i] = arr[i] + sums[i + 1];
                ends.put(i, ends.get(i + 1));
            } else {
                sums[i] = arr[i];
                ends.put(i, i);
            }
        }
        int K = 0;
        int sum = 0;
        int res = 0;
        for (int i = 0; i < arr.length; i++) {
            while (K < arr.length && sum + sums[K] <= aim) {      // 这是K 还能扩的情况
                sum += sums[K];                             //  和累加
                K = ends.get(K) + 1;                        // K 来到 
            }
            sum -= K > i ? arr[i] : 0;                 // K 不能扩的时候 左指针右滑 sum减去 i的值  计算i+1     //  窗口只有它本身  有大于aim 是减0 
            res = Math.max(res, K - i);              // 比较本次长度和 已计算最长长度
            K = Math.max(K, i + 1);              // 处理窗口内 只有一个值  但这个值又比aim大的情况  窗口 左右指针同时右滑
        }
        return res;
    }

    public static int maxLength(int[] arr, int k) {
        int[] h = new int[arr.length + 1];
        int sum = 0;
        h[0] = sum;
        for (int i = 0; i != arr.length; i++) {
            sum += arr[i];
            h[i + 1] = Math.max(sum, h[i]);
        }
        sum = 0;
        int res = 0;
        int pre = 0;
        int len = 0;
        for (int i = 0; i != arr.length; i++) {
            sum += arr[i];
            pre = getLessIndex(h, sum - k);
            len = pre == -1 ? 0 : i - pre + 1;
            res = Math.max(res, len);
        }
        return res;
    }

    public static int getLessIndex(int[] arr, int num) {
        int low = 0;
        int high = arr.length - 1;
        int mid = 0;
        int res = -1;
        while (low <= high) {
            mid = (low + high) / 2;
            if (arr[mid] >= num) {
                res = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return res;
    }

    // for test
    public static int[] generateRandomArray(int len, int maxValue) {
        int[] res = new int[len];
        for (int i = 0; i != res.length; i++) {
            res[i] = (int) (Math.random() * maxValue) - (maxValue / 3);
        }
        return res;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000000; i++) {
            int[] arr = generateRandomArray(10, 20);
            int k = (int) (Math.random() * 20) - 5;
            if (maxLengthAwesome(arr, k) != maxLength(arr, k)) {
                System.out.println("oops!");
            }
        }

    }
}