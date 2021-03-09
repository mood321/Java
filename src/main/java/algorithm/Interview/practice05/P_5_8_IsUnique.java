package algorithm.Interview.practice05;

import java.util.HashMap;

/**
 * @author mood321
 * @date 2021/3/9 23:16
 * @email 371428187@qq.com
 */
public class P_5_8_IsUnique {

    /**
     * 方法一 :map 存次数
     */
    public static boolean once(String s) {
        int len = s.length();

        if (len == 0 || len == 1)
            return true;

        HashMap<Character, Integer> map = new HashMap<Character, Integer>();

        for (int i = 0; i < len; i++) {
            if (map.containsKey(s.charAt(i)))
                return false;
            else
                map.put(s.charAt(i), 1);
        }

        return true;

    }

    /**
     * 方法二: 排序 ,比较前后数值是否一致 ,外空间复杂度为O(1) 用堆排
     */
    public static boolean isUnique(String s) {
        int len = s.length();
        if (len == 0 || len == 1)
            return true;

        char[] c = s.toCharArray();
        heapSort(c);
        System.out.println(String.valueOf(c));
        for (int i = 1; i < len; i++) {
            if (c[i] == c[i - 1])
                return false;

        }
        return true;
    }

    //非递归方式实现堆排序
    public static void heapSort(char[] c){
        int len = c.length;
        //构造大顶堆
        for(int i = len/2;i>-1;i--){
            adjustHeap(c,i,len);
            System.out.println(String.valueOf(c));
        }
        //交换根节点和叶子节点，并调整大顶堆
        for(int i = len-1;i>-1;i--){
            swap(c,i,0);
            adjustHeap(c,0,i);
        }
    }
    //不断将父亲节点和其子节点进行比较，将大的节点和父亲节点交换
    private static void adjustHeap(char[] c,int p, int size) {
        for(int i = p;i<size;i++){
            int left = i*2 + 1;
            int right = i*2 + 2;
            int max = i;
            if(left < size){
                if(c[i]<c[left])
                    max = left;
                if(right<size && c[right] > c[max])
                    max = right;
                if(max!=i)
                    swap(c,i,max);
            }else
                break;
        }

    }

    private static void swap(char[] c, int i, int j) {
        char temp;
        temp = c[i];
        c[i] = c[j];
        c[j] = temp;
    }
}