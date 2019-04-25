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

            System.out.println("二分 位置:"+find(bubble(a),8));


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

    private static int[] insert(int[] c) {
        int work;
        for(int i=1;i<c.length;i++){
            int tem=c[i];
            work=i;
            while(work>0&&c[work-1]<tem){
                c[work]=c[work-1];
                work--;
            }

            c[work]=tem;
        }
        return c;
    }

    /**
     * 选择排序法
     * @param b
     * @return
     */
    private static int[] select(int[] b) {

        for (int i=0;i<b.length-1;i++){
            int min=i;
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
