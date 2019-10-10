package algorithm.basic;

/**
 * @Created by mood321
 * @Date 2019/10/8 0008
 * @Description TODO
 */
public class Test {
    public static  boolean isSum(int[] arr ,int sum,int cur,int i){
        if(i==arr.length){
            return sum==cur;
        }
        return isSum(arr,sum,cur,i+1)||isSum(arr,sum,arr[i]+cur,i+1);

    }

    public static void main(String[] args) {
        int[] ints = {2, 3, 4, 5, 7};
        System.out.println(isSum(ints,11,0,0));
    }
}
