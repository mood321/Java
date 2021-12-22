package algorithm.Interview.practice08;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author mood321
 * @date 2021/12/22 23:51
 * @email 371428187@qq.com
 */
public class P_8_7_GetLiL {
    /**
     * 双层for 确定每个子数组
     *
     * @param arr
     * @return
     */
    public int getLil1(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        int len = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = i; j < arr.length; j++) {
                // 是否符合条件
                if (isIntegrated(arr, i, j)) {
                    len = Math.max(len, j - i + 1);
                }
            }
        }
        return len;
    }

    private boolean isIntegrated(int[] arr, int i, int j) {
        int[] ints = Arrays.copyOfRange(arr, i, j + 1);
        Arrays.sort(ints);// O(N*LogN)
        for (int k = 1; k < ints.length; k++) {
            // 两数相差1
            if (ints[k - 1] != ints[k] - 1) {
                return false;
            }
        }
        return true;
    }
    /**
     * 核心思路 ,确定一个子数组 的方法,  两数差值为1  [2,3,4]  长度必为  最大减最小+1 (4-2+1 =3 )
     *
     * @param arr
     * @return
     */
    public int getLil2(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        int len = 0;
        int max =0;
        int min =0;
        Set<Integer> integers = new HashSet<>();
        for (int i = 0; i < arr.length; i++) {
            max=Integer.MIN_VALUE;
            min= Integer.MAX_VALUE;
            for (int j = i; j < arr.length; j++) {
                if(integers.contains(arr[j])){
                    break;
                }
                integers.add(arr[j]);
                max=Math.max(arr[j],max);
                min =Math.min(arr[j],min);
                if(max-min == j-i){ // 从核心思路逆推  长度不一样, 中间值必不连续
                    len= Math.max(len,j-i+1);
                }
            }
            integers.clear();
        }
        return len;
    }
}