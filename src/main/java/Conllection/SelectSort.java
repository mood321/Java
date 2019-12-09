package Conllection;

import java.util.Arrays;

public class SelectSort {
    public static void main(String[] args) {
        int a[] = {5, 2, 8, 4, 1, 9, 16};
        System.out.println("冒泡排序法:" + Arrays.toString(bubble(a)));
        int b[] = {5, 2, 8, 4, 1, 9, 16};
        System.out.println("选择排序法" + Arrays.toString(select(b)));
        int c[] = {5, 2, 8, 4, 1, 9, 16};
        System.out.println("插入排序法" + Arrays.toString(insert(c)));
        int d[] = {5, 2, 8, 4, 1, 9, 16};
        mergeSort(d);
        System.out.println("归并排序法" + Arrays.toString(d));

        System.out.println("二分 位置:" + find(bubble(a), 8));


    }

    /**
     * 归并排序法 主函数
     *
     * @param arr
     */
    public static void mergeSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        mergeSort(arr, 0, arr.length - 1);
    }

    /**
     * 归并排序法 分治 并合并拆开的结果集
     *
     * @param arr
     * @param l
     * @param r
     */
    public static void mergeSort(int[] arr, int l, int r) {
        if (l == r) {
            return;
        }
        int mid = l + ((r - l) >> 1); //正中间一个元素下标
        mergeSort(arr, l, mid);
        mergeSort(arr, mid + 1, r);
        merge(arr, l, mid, r);
    }

    /**
     * 归并排序法  对数组 起始位置l 结束r 中间点m 两段进行合并排序
     *
     * @param arr
     * @param l
     * @param m
     * @param r
     */
    public static void merge(int[] arr, int l, int m, int r) {
        int[] help = new int[r - l + 1]; //辅助数组
        int i = 0;
        int p1 = l;
        int p2 = m + 1;
        while (p1 <= m && p2 <= r) {
            help[i++] = arr[p1] < arr[p2] ? arr[p1++] : arr[p2++];
        }
        while (p1 <= m) {
            help[i++] = arr[p1++];
        }
        while (p2 <= r) {
            help[i++] = arr[p2++];
        }
        for (i = 0; i < help.length; i++) {
            arr[l + i] = help[i];
        }
    }

    /**
     * 二分查找
     *
     * @param a
     * @param i
     * @return
     */
    private static int find(int[] arr, int i) {

        int L = 0, R = arr.length;     //  最左最右

        while (L <= R) {         // 边界问题
            int mid = L + (R - L) / 2;   // 中位数
            if (i < arr[mid]) {
                R = mid - 1;              // 来到左边
            } else if (i > arr[mid]) {
                L = mid + 1;              // 来到右边
            } else {
                return mid;
            }

        }

        return arr.length;
    }

    /**
     * 插入算法
     * 从 1 开始向前 检查  如果检查位置下标（work）大于0 而且比work-1 小  work-1的值给work  work向前继续检查
     * <p>
     * 在计算时间复杂度时 有几种情况
     * 原数组已经拍好序 为(最好情况)  O(n)
     * 原数组顺序为倒叙 为(最坏情况)  O(n^2)
     *
     * @param arr
     * @return
     */
    private static int[] insert(int[] arr) {
        if (arr == null || arr.length < 2) {
            return arr;
        }
        // 思路： 找到从 1 开始找work 应该在的位置   比work小的 放在前面
        //待排元素小于有序序列的最后一个元素时，向前插入
        // 插入 值判断 小于 稳定
        for (int i = 1; i < arr.length; i++) {
            int work = i;
            while (work > 0 && arr[work] < arr[work - 1]) {
                swarp(arr, work, work - 1);
                work--;
            }
        }
        return arr;
    }

    /**
     * 选择排序法
     * 核心算法: 找出起始位置（i）到最后位置 最小的数 和起初位置交换
     *
     * @param b
     * @return
     */
    private static int[] select(int[] b) {
        if (b == null || b.length < 2) {
            return b;
        }
        // 选择 排序 每次选择一个最小 放在当前位置   两次for   O(N^2)
        // 因为 要找到 最小值   这个最小值不一定是第一个最小值   所以不稳定
        for (int i = 0; i < b.length - 1; i++) {      // 最后一次 循环  自己一定是最小 可不用计算
            int min = i;
            for (int j = i + 1; j < b.length; j++) {     //
                min = b[j] < b[min] ? j : min;
            }
            if (b[i] != b[min])      // 最小不是 本身 交换
                swarp(b, i, min);
        }

        return b;
    }

    /**
     * 冒泡排序法
     */
    private static int[] bubble(int[] a) {
        if (a.length == 0) {
            return null;
        }
        // 思路 ， i 与i+1 比较   大的放后面   两次for  O(n^2)  稳定
        for (int i = 0; i < a.length - 1; i++) {   // 最后一个值不参与 循环
            for (int i1 = 0; i1 < a.length - 1; i1++) {     // 与i 循环次数一致

                if (a[i1] > a[i1 + 1]) {
                    swarp(a, i1, i1 + 1);
                }
            }

        }

        return a;
    }

    private static void swarp(int[] a, int j, int i) {
        int tem = a[j];
        a[j] = a[i];
        a[i] = tem;
    }
}
