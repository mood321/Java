package algorithm.basic;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @Created by mood321
 * @Date 2019/10/6 0006
 * @Description 堆的操作 入堆 出堆 和堆 排序
 */
public class Code_03_MinHeapSort {
    /**
     * 堆排序 代码
     *
     * @param arr
     */
    public static void heapSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        // 思路 : 1 把所有元素放在堆
        // 2 依据堆的特性排序
        // 复杂度 O(N*Log2(N))       不稳定
        
        for (int i = 0; i < arr.length; i++) { // 可以重用数组空间  节省额外空间   从1开始
            heapInsert(arr, i);
        }
        // 出堆
        // 每次出堆  堆大小-1  拿出元素放在数组里
        for (int i = arr.length; i > 0; i--) {
            heapify(arr, 0, i);

        }

    }

    /**
     * <p> 1.把需要入堆的元素放在 heapsize+1 位置（最后一个节点）
     * <p> 2.去和父节点比较大小 如大顶堆 比父节点大就交换 （父节点index= (子节点index-1) /2） 然后继续比较
     *
     * @param arr
     * @param index
     */
    public static void heapInsert(int[] arr, int index) {
        // 入堆 思路, 放在最后节点 向上冒  和父节点比
        while (arr[index] < arr[(index - 1) / 2]) {
            swap(arr, index, (index - 1) / 2);
            index = (index - 1) / 2;
        }
    }

    /**
     * 出堆  操作
     * <p> 1.头结点和最后一个节点交换 交换后最后一个节点是需要弹出的值 堆大小-1
     *
     * <p> 2.继续堆化  此时顶点的数据 不一定是最大的 他需要和子节点大的比较 大于则不动 小于交换
     * <p> ps:（此处不是和每个节点比较 因为堆只要求父节点大于子节点 不要求左节点小于右节点）
     * <p> 3.交换后 继续2的操作
     * 此方法只做2-3   ps: 可以做
     *
     * @param arr
     * @param index
     * @param size
     */
    public static void heapify(int[] arr, int index, int size) {
        // 先交换
        swap(arr, index, --size);
        int left = index * 2 + 1;
        while (left < size) {
            // 主题逻辑  左右大小  然后大的和父 谁小
            int min = left + 1 < size && arr[left + 1] < arr[left] ? left + 1 : left;   // 取子节点 谁小
            min = arr[index] > arr[min] ? min : index;
            if (index == min) {
                break;
            }

            swap(arr, index, min);
            index=min;
            left=min*2+1;

        }


    }

    public static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    // for test
    public static void comparator(Integer[] arr) {
        Arrays.sort(arr,new MinComparator());

    }
    public  static  class MinComparator implements  Comparator<Integer>{
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2-o1;
        }
    }
    // for test
    public static int[] generateRandomArray(int maxSize, int maxValue) {
        int[] arr = new int[(int) ((maxSize + 1) * Math.random())];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (int) ((maxValue + 1) * Math.random()) - (int) (maxValue * Math.random());
        }
        return arr;
    }

    // for test
    public static Integer[] copyArray(int[] arr) {
        if (arr == null) {
            return null;
        }
        Integer[] res = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = arr[i];
        }
        return res;
    }

    // for test
    public static boolean isEqual(int[] arr1, Integer[] arr2) {
        if ((arr1 == null && arr2 != null) || (arr1 != null && arr2 == null)) {
            return false;
        }
        if (arr1 == null && arr2 == null) {
            return true;
        }
        if (arr1.length != arr2.length) {
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] != arr2[i]) {
                return false;
            }
        }
        return true;
    }

    // for test
    public static void printArray(int[] arr) {
        if (arr == null) {
            return;
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    // for test
    public static void main(String[] args) {
        int testTime = 500000;
        int maxSize = 100;
        int maxValue = 100;
        boolean succeed = true;
        for (int i = 0; i < testTime; i++) {
            int[] arr1 = generateRandomArray(maxSize, maxValue);
            Integer[] arr2 = copyArray(arr1);
            heapSort(arr1);
            comparator(arr2);
            if (!isEqual(arr1, arr2)) {
                succeed = false;
                break;
            }
        }
        System.out.println(succeed ? "Nice!" : "Fucking fucked!");

        int[] arr = generateRandomArray(maxSize, maxValue);
        printArray(arr);
        //arr =new  int[] {5, 2, 8, 4, 1, 9, 16};
        heapSort(arr);
        printArray(arr);
    }

}
