package algorithm.Interview.practice08;

/**
 * 自然数组排序
 */
public class P_8_13_Sort {

    public  void  sort(int[] arr){
        if(arr ==null || arr.length ==0){
            return;
        }
        int tem =0;
        int next=0;
        for (int i = 0; i < arr.length; i++) {
             tem=arr[i];
             while (arr[i]!= i+1){
                 next=arr[tem-1];
                 arr[tem-1]=tem;
                 tem=next;
             }
        }
    }
}
