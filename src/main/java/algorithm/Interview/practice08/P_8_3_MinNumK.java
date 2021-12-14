package algorithm.Interview.practice08;

/**
 * @author mood321
 * @date 2021/12/14 23:59
 * @email 371428187@qq.com
 */
public class P_8_3_MinNumK {

    public static void main(String[] args) {
        int[] ints={1,2,3,100,2,4,42,44,455};

        getNumK(ints,2);
    }

    /**
     *  BFPRT 算法 未实现
     * @param arr
     * @param k
     */
    public static void getNumK2(int[] arr, int k) {
        
    }
    /**
     * 堆排序 大根堆
     * 堆:  1 子节点比父节点大/小
     * 2  从头取 从尾插平衡
     */
    public static void getNumK(int[] arr, int k) {
        if (arr == null || arr.length == 0 || arr.length <= k) {
            return;
        }
        int[] result = new int[k];
        // 构建大根堆  节省空间
        for (int i = 0; i < k; i++) {
            heapInsert(arr[i], result, i);
        }
        for (int i = k; i < arr.length; i++) {
            if (result[0] > arr[i]) {
                result[0] = arr[i];
                // 重平衡堆
                heapif(result, 0, k);

            }
        }
        for (int i = 0; i < result.length; i++) {
            System.out.println(result[i] + "  ");
        }

    }

    /**
     * 从头开始
     *
     * @param result
     * @param i
     * @param k
     */
    private static void heapif(int[] result, int i, int k) {
        int left = i * 2 + 1;
        int right = i * 2 + 2;

        int cru = i;
        while (left < k) {
            if (result[i] < result[left]) {
                cru = left;
            }
            if (right < k && result[right] > result[i]) {
                cru = right;
            }
            if (cru != i) {
                swap(result, cru, i);
            } else {
                break;
            }
            i = cru;
            left = i * 2 + 1;
            right = i * 2 + 2;
        }
    }

    private static void heapInsert(int num, int[] result, int i) {
        result[i] = num;
        while (i != 0) {
            int parent = (i - 1) / 2;
            if (result[parent] < result[i]) {
                swap(result, i, parent);
                i = parent;
            } else {
                break;
            }
        }
    }

    private static void swap(int[] arr, int i, int parent) {
        int tem = arr[i];
        arr[i] = arr[parent];
        arr[parent] = tem;
    }
}