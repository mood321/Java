package algorithm.Interview.practice08;

import java.util.Scanner;

/**
 * 获取数组小和问题
 *      在一个数组中，每一个数左边比当前数小的数累加起来，叫做这个数组的小和。求一个数组的小和。
 *      例子：
 *      [1,3,4,2,5]
 *      1左边比1小的数，没有；
 *      3左边比3小的数，1；
 *      4左边比4小的数，1、3；
 *      2左边比2小的数，1；
 *      5左边比5小的数，1、3、4、2；
 *      所以小和为1+1+3+1+1+3+4+2=16
 */
public class P_8_12_SmallSum {


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] arr = new int[n];
        for(int i = 0; i <n; i++){
            arr[i] = scanner.nextInt();
        }
        long sum =  getsmallsum(arr);

        System.out.println(sum);

    }
    public static int getsmallsum(int[] arr){
        if(arr ==null || arr.length ==0){
            return 0;
        }
        return func(arr,0,arr.length-1);
    }

    private static int func(int[] arr, int l, int r) {

        if(r == l){
            return 0;
        }
        int mid = l+((r-l)>>1);
        return func(arr,l,mid)+func(arr,mid+1,r)+merger(arr,l,mid,r);
    }

    private static  int merger(int[] arr, int l, int mid, int r) {
        int[] help = new int[r - l+1];
        // 辅助数组排序 计算值
        int i=0;
        int p1=l;
        int p2=mid+1;
        int res=0;
        while (p1 <= mid && p2<= l){
            if(arr[p1]<=arr[p2]){//当左半区的数小于右半区的数此时产生小和
                res+=(r-p2+1)*arr[l];
                help[i++]=arr[p1++];
            }
            else{
                help[i++]=arr[p2++];
            }
        }

        while (p1 <= mid) {
            help[i++] = arr[p1++];
        }
        while (p2 <= r) {
            help[i++] = arr[p2++];
        }
        for (i = 0; i < help.length; i++) {
            arr[l + i] = help[i];
        }
        return res;
    }
}
