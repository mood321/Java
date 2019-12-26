package algorithm.Interview.practice01;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Executors;

/**
 * @author mood321
 * @date 2019/12/26 23:15
 * @email 371428187@qq.com
 */
public class P_1_6_WindowMax {

    public static  int[] getWindowMax(int[] arr,int num ){
        if(arr ==null || num< 1 || arr.length<num){
            return new int[]{0};
        }
        // 用来存
        LinkedList<Integer> qmax = new LinkedList<>();// frist 存最大  last 小
        int res[] =new int[arr.length-num+1];
           int index=0;
        for (int i = 0; i <arr.length; i++) {

            while(!qmax.isEmpty() && arr[qmax.peekLast()]<=arr[i]){
                qmax.pollLast();
            }
            qmax.addLast(i);

            // 判断 现在最大是否出窗口  一次出一个  值判断一次
            if(qmax.peekFirst()== i-num){
                qmax.pollFirst();
            }
            // 已经构建一个窗口
            if(i>= num-1){
               res[index++]=arr[qmax.peekFirst()];
            }
        }
        return res;

    }

    public static void main(String[] args) {
        int[] ints = {4, 3, 5, 5, 3, 3, 6, 7};
        
        Arrays.stream( getWindowMax(ints,3)).forEach(i->{
            System.out.println(i);
        });
    }
}