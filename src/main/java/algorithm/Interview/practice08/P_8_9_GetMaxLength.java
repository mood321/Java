package algorithm.Interview.practice08;

/**
 * 未排序正数数组和为定值的最长子数组长度
 *
 * @author he
 */
public class P_8_9_GetMaxLength {

    public int getmaxLength(int[] arr, int k) {
        if (arr == null || arr.length == 0 || k < 0) {
            return 0;
        }
        int left = 0;
        int right = 0;
        int sum = arr[0];
        int len = 0;

        while (left <= right) {
            if (sum == k) {
                len = Math.max(len, right - left + 1);
                sum -= arr[left++];
            } else if (sum < k) {
                right++;
                if (right == arr.length) {
                    break;
                }
                sum += arr[right];
            } else {
                sum -= arr[left++];
            }
        }
        return len;
    }
}
