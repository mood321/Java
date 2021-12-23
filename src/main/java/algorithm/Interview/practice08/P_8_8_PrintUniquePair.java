package algorithm.Interview.practice08;

/**
 * @author mood321
 * @date 2021/12/23 23:50
 * @email 371428187@qq.com
 */
public class P_8_8_PrintUniquePair {
    //不重复打印排序数组中相加和为给定值的所有二元组
    public void printUniquePair(int[] arr, int k) {// 有序数组  可能重复
        if (arr == null || arr.length < 2) {
            System.out.println("Invalid parameter ..");
            return;
        }
        int left = 0;
        int rigth = arr.length - 1;
        while (left < rigth) {
            if (arr[left] + arr[rigth] < k) {

                left++;
            } else if (arr[left] + arr[rigth] > k) {
                rigth--;
            } else {
                if (left == 0 || (arr[left] != arr[left - 1])) {
                    System.out.println(arr[left] + " , " + arr[rigth]);
                }
                left++;
                rigth--;
            }
        }

    }
    // 打印不重复的三元组  一个逻辑
    public void printUniquePair3(int[] arr, int k) {// 有序数组  可能重复
        if (arr == null || arr.length < 3) {
            System.out.println("Invalid parameter ..");
            return;
        }
        for (int i = 0; i < arr.length-2; i++) {
            if (i == 0 || (arr[i] != arr[i - 1])) {
                print(arr,i,i+1,arr.length-1,k-arr[i]);
            }
        }

    }

    private void print(int[] arr, int i, int left, int rigth, int k) {
        while (left < rigth) {
            if (arr[left] + arr[rigth] < k) {

                left++;
            } else if (arr[left] + arr[rigth] > k) {
                rigth--;
            } else {
                if (left == 0 || (arr[left] != arr[left - 1])) {
                    System.out.println(arr[i]+" , "+arr[left] + " , " + arr[rigth]);
                }
                left++;
                rigth--;
            }
        }
    }
}