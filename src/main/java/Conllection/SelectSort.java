package Conllection;

import java.util.Arrays;

public class SelectSort {
    public static void main(String[] args) {
        int a[] = {5, 2, 8, 4, 1, 9, 16};
        System.out.println("冒泡排序法:" + Arrays.toString(bubble(a)));
        int b[] = {5, 2, 8, 4, 1, 9, 16};
          System.out.println("选择排序法"+ Arrays.toString(select(b)));
        int c[] = {5, 2, 8, 4, 1, 9, 16};
        System.out.println("插入排序法"+Arrays.toString(insert(c)));
        int d[] = {5, 2, 8, 4, 1, 9, 16};
        mergeSort(d);
        System.out.println("归并排序法"+Arrays.toString(d));

        System.out.println("二分 位置:"+find(bubble(a),8));


    }

    /**
     *  归并排序法 主函数
     * @param arr
     */
    public static void mergeSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        mergeSort(arr, 0, arr.length - 1);
    }

    /**
     *  归并排序法 分治 并合并拆开的结果集
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
     * @param a
     * @param i
     * @return
     */
    private static int find(int[] a, int i) {

        int beg=0,end=a.length;
        while(beg<=end){
            int mid=beg+(end-beg)/2;
            if(a[mid]==i){
                return mid;
            }else if(i<a[mid]){
                end=mid-1;
            }else{
                beg=mid+1;
            }
        }
        return a.length;
    }

    /**
     * 插入算法
     *  从 1 开始向前 检查  如果检查位置下标（work）大于0 而且比work-1 小  work-1的值给work  work向前继续检查
     *
     *  在计算时间复杂度时 有几种情况
     *  原数组已经拍好序 为(最好情况)  O(n)
     *  原数组顺序为倒叙 为(最坏情况)  O(n^2)
     *
     * @param c
     * @return
     */
    private static int[] insert(int[] c) {
        int work;
        for(int i=1;i<c.length;i++){
            int tem=c[i];
            work=i;
            while(work>0 && c[work-1]<tem){
                c[work]=c[work-1];
                work--;
            }

            c[work]=tem;
        }
        return c;
    }

    /**
     * 选择排序法
     *  核心算法: 找出起始位置（i）到最后位置 最小的数 和起初位置交换
     * @param b
     * @return
     */
    private static int[] select(int[] b) {

        for (int i=0;i<b.length-1;i++){
            int min=i;//默认最
            for(int j=i+1;j<b.length;j++){
                if(b[min]>b[j]){
                   min=j;
                }
            }
            if(min!=i){
                int tem=b[i];
                b[i]=b[min];
                b[min]=tem;
            }
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
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = 0; j < a.length - i - 1; j++) {
                if (a[j] > a[j + 1]) {
                    int tem = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = tem;

                }
            }
        }
        return a;
    }
}
