package Conllection;

import java.util.Arrays;

public class SelectSort {
    public static void main(String[] args) {
        int a[] = {5, 2, 8, 4, 1, 9, 16};
        System.out.println("冒泡排序法:" + Arrays.toString(bubble(a)));
        int b[] = {5, 2, 8, 4, 1, 9, 16};
          System.out.println("选择排序法"+ Arrays.toString(select(b)));
        int c[] = {5, 2, 8, 4, 1, 9, 16};
        //   System.out.println("插入排序法"+Arrays.toString(insert(c)));

        //    System.out.println("二分 位置:"+find(a,8));


    }

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
