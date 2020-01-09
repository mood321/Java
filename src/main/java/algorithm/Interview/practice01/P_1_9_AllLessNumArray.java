package algorithm.Interview.practice01;

import java.util.LinkedList;

/**
 * @author mood321
 * @date 2020/1/8 21:52
 * @email 371428187@qq.com
 *  <p> 第一中解法就不写了 在原型里面有
 */
public class P_1_9_AllLessNumArray {
    public static  int getNum(int[] arr ,int num){
        if( arr ==null || arr.length<1){
            return  0;
        }
        LinkedList<Integer> min = new LinkedList<>();
        LinkedList<Integer> max = new LinkedList<>();
        int res=0,L=0,R=0;

        while (L< arr.length){
              // 事实上窗口两个动作
            // 1 进来 先把R 推到能到的最远位置
            // 2 R 推不动了  把L 想前推
            //
            while(R <arr.length){
                // 两单调队列  维持 现在最大 最小
                // 
                 while(!max.isEmpty() && arr[R]>=arr[max.peekLast()]){
                     max.pollLast();
                 }
                 max.addLast(R);
                 while(!min.isEmpty() && arr[R] <= arr[min.peekLast()]){
                     min.pollLast();
                 }
                min.addLast(R);
                 // 如果已经大于num   停止R
                if(arr[max.peekFirst()]-arr[min.peekFirst()]> num){
                    // 走到这 两种情况 1 是现在的R 比原来最大更大  已经不满足了 2 单数就 比num大 ( 这种L R 一定在一起 L会不断前进 R不变)
                    break;
                }

                R++;        // 走到这 最大最小相减还满足num 有三种各种可能  1 是比原来最小更小 后推 2 是比原来最大还大 后推 3 中间数 后推
            }


            if (max.peekFirst() !=min.peekFirst() ) {      // 去掉一些 单个数小于num 的情况
                res += R - L-1; // 加上 这时 以L 为起点满足条件的个数
            }
              // 这儿一定要处理过期值
            if(L== min.peekFirst()){
                min.pollFirst();

            }
            if(L== max.peekFirst()){
                max.pollFirst();

            }

          
            L++;
            
        }
        return res;

    }

    public static void main(String[] args) {
        int[] ints = {1,52,3,2,1};
        System.out.println(getNum(ints,2));
    }
}