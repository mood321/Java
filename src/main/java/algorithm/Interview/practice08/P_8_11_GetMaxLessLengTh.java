package algorithm.Interview.practice08;

public class P_8_11_GetMaxLessLengTh {

    public static int maxLength(int[] arr, int k) {
        int[] helpArr = new int[arr.length + 1];
        int sum = 0;
        helpArr[0] = sum;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
            helpArr[i + 1] = Math.max(sum, helpArr[i]);
        }
        sum = 0;
        int pre = 0;
        int len = 0;
        int res = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
            pre = getLessIndex(helpArr, sum - k);
            len = pre == -1 ? 0 : i - pre + 1;
            res = Math.max(len, res);
        }
        return res;
    }

    private static int getLessIndex(int[] arr, int num) {
        int left = 0;
        int right = arr.length - 1;
        int mid = 0;
        int res = -1;
        while (left <= right) {
            mid = (left + right) / 2;
            if (num > arr[mid]) {
                left = mid + 1;
            } else {
                res = mid;
                right = mid - 1;
            }
        }
        return res;
    }
}

