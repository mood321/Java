package algorithm.Interview.practice08;

import java.util.*;

/**
 * @author mood321
 * @date 2021/12/17 0:02
 * @email 371428187@qq.com
 * <p>
 * 这题可以用map借  比较简单  复杂度高  ,这里不用这种实现
 */
public class P_8_5_GetCountNk {
    /**
     * 出现次数答案与一般
     *
     * @param arr
     */
    public static void printHalf(int[] arr) {
        if (arr == null || arr.length == 0) {
            System.out.println(" nothing print ...");
        }
        int scan = 0;
        int times = 0;
        for (int i = 0; i < arr.length; i++) {
            if (times == 0) {
                scan = arr[i];
                times = 1;
            } else if (scan == arr[i]) {
                times++;
            } else {
                times--;
            }
        }
        times = 0;
        for (int i = 0; i < arr.length; i++) {
            if (scan == arr[i]) {
                times++;
            }
        }
        if (times > arr.length / 2) {
            System.out.println(" num is : " + scan);
        } else {
            System.out.println(" no such num .. ");
        }
    }

    //每次删除k个最后剩下的数可能就是结果
    public static String getRes(int[] arr, int n, int k) {
        int c = n / k;
        if (arr == null || arr.length == 0 || c < 0) {
            return "-1";
        }
        
        Map<Integer, Integer> cands = new HashMap();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (cands.containsKey(arr[i])) {
                cands.put(arr[i], cands.get(arr[i]) + 1);
            } else {
                if (cands.size() == k - 1) {
                    allMinus(cands);
                } else {
                    cands.put(arr[i], 1);
                }
            }
        }
        HashMap<Integer, Integer> realRes = getReal(arr, cands);
        for (Map.Entry<Integer, Integer> set : realRes.entrySet()) {
            int key = set.getKey();
            int value = set.getValue();
            if (value > c) {
                sb.append(key + " ");
            }
        }
        return sb.length() == 0 ? "-1" : sb.toString().trim();
    }

    public static HashMap<Integer, Integer> getReal(int[] arr, Map<Integer, Integer> cands) {
        HashMap<Integer, Integer> real = new HashMap();
        for (int i = 0; i < arr.length; i++) {
            if (cands.containsKey(arr[i])) {
                if (real.containsKey(arr[i])) {
                    real.put(arr[i], real.get(arr[i]) + 1);
                } else {
                    real.put(arr[i], 1);
                }
            }
        }
        return real;
    }

    public static void allMinus(Map<Integer, Integer> map) {
        Iterator<Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> next = iterator.next();
            if (next.getValue() == 1) {
                iterator.remove();
            } else {
                next.setValue(next.getValue() - 1);
            }
        }
    }
}