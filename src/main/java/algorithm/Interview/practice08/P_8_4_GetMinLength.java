package algorithm.Interview.practice08;

/**
 * @author mood321
 * @date 2021/12/16 0:04
 * @email 371428187@qq.com
 */
public class P_8_4_GetMinLength {
    public static void main(String[] args) {
        int[] ints={1,5,3,4,2,6,7};
        int minlength = getMinlength(ints);
        System.out.println(minlength);
    }
    public static int getMinlength(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        //
        int min = arr[arr.length - 1];
        int minIdx = -1;
        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] < min) {
                min = Math.min(arr[i], min);
            } else {
                minIdx = i;
            }

        }
        if (minIdx == -1) {
            return 0;
        }
        int max = arr[0];
        int maxIdx = -1;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < max) {
                maxIdx = i;
            } else {
                max=Math.max(arr[i],max);
            }
        }
        return  maxIdx-minIdx +1;
    }
}