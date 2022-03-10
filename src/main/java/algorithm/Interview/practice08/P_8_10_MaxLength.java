package algorithm.Interview.practice08;


import java.util.HashMap;

/**
 * <p></> 未排序数组和为定值的最长子数组长度
 *   子数组必定是连续的
 * @author mood321
 */
public class P_8_10_MaxLength {
    public int getManLenght(int[] arr, int k) {
        if (arr == null || arr.length == 0) {
            return 0;

        }
        int len = 0;

        HashMap<Integer, Integer> map = new HashMap<>();
        int sum =0;
        map.put(sum,-1);
        for (int i = 0; i < arr.length; i++) {
            sum+= arr[i];
            if(map.containsKey(sum- k)){
                len = Math.max(len,i-map.get(sum-k));

            }
            if(!map.containsKey(sum)){
                map.put(sum,i);
            }
        }
        return len;
    }
}
