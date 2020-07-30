package algorithm.Interview.practice04;

/**
 * @author mood321
 * @date 2020/7/31 0:42
 * @email 371428187@qq.com
 */
public class P_4_15_Jump {

    public  int  jump(int[] arr){
        if(arr==null || arr.length==0){
            return 0;
        }
         int jump=0;
        int cur=0;
        int  next=0;
        for (int i = 0; i < arr.length ; i++) {
              if(cur<i){   // 找可跳范围的 所有数
                  jump++;
                  cur=next;
              }
              next=Math.max(next,i+arr[i]);   // 可跳范围谁 最大 跳的最远
        }
        return jump;
    }
}