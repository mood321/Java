package algorithm.basic;

import java.util.Arrays;

/**
 * @Created by mood321
 * @Date 2019/10/5 0005
 * @Description 荷兰国旗问题来改进快速排序
 */
public class Code_04_QuickSort {

    public static void quickSort(int[] arr) {
        if(arr==null || arr.length<2){
            return;
        }
        quickSort(arr,0,arr.length-1);
    }
    public static void quickSort(int[] arr, int l, int r) {
        if(l<r){ //只有数组 有多个元素才排序  这也是递归结束条件
            // 在普通快速排序中  排序时间复杂度 受数据状况有关  如果最后一个数据项正好是整个数组的等于区域的数 是O(N*log(N))
            // 改进 不固定用最后一个  书籍用一个数放在最后 去拍
            swap(arr, l + (int) (Math.random() * (r - l + 1)), r);
            int[] p = partition(arr, l, r); // 递归 分治
            quickSort(arr,l,p[0]-1);// 小于区域 在排序
            quickSort(arr,p[1]+1,r);//  大于
        }
    }
    public static int[] partition(int[] arr, int l, int r) {
        int less = l - 1, more = r;//less 和花旗国问题一致 more 向前一个 用最后一个作为num 划分区域
        while (l < more) {
            if (arr[l] < arr[r]) {
                swap(arr, l++, ++less); // 小于的时候 小于区域范围+1（++less）  把当前l 的值和（++less）的值交换  这里（++less）的值要要么是l 要么是等于范围的值 所以交换不会出错
            }else if(arr[l]>arr[r]){
                swap(arr,l,--more); // 和 小于逻辑一样  但交换玩l不++ 因为小于的交换回来的数要么是小于的（没找到等于的） 要么是等于（找到等于的） 而大于的交换回来的就不一定了
            }else {
                l++;
            }

        }
        swap(arr,more,r);// r位置必定是 等于r的 交换到等于区域
        return new int[]{less+1,more}; // 返回等于区域的下标
    }

    public static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
    // for test
    public static void comparator(int[] arr) {
        Arrays.sort(arr);
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
    public static boolean isEqual(int[] arr1, int[] arr2) {
        if ((arr1 == null && arr2 != null) || (arr1 != null && arr2 == null)) {
            return false;
        }
        if (arr1 == null && arr2 == null) {
            return true;
        }
        if (arr1.length != arr2.length) {
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] != arr2[i]) {
                return false;
            }
        }
        return true;
    }

    // for test
    public static void printArray(int[] arr) {
        if (arr == null) {
            return;
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    // for test
    public static void main(String[] args) {
        int testTime = 500000;
        int maxSize = 100;
        int maxValue = 100;
        boolean succeed = true;
        for (int i = 0; i < testTime; i++) {
            int[] arr1 = generateRandomArray(maxSize, maxValue);
            int[] arr2 = copyArray(arr1);
            quickSort(arr1);
            comparator(arr2);
            if (!isEqual(arr1, arr2)) {
                succeed = false;
                printArray(arr1);
                printArray(arr2);
                break;
            }
        }
        System.out.println(succeed ? "Nice!" : "Fucking fucked!");

        int[] arr = generateRandomArray(maxSize, maxValue);
        printArray(arr);
        quickSort(arr);
        printArray(arr);

    }

}
