package algorithm.basic;

import java.util.Arrays;

/**
 * @Created by mood321
 * @Date 2019/10/6 0006
 * @Description 堆的操作 入堆 出堆 和堆 排序
 */
public class Code_03_HeapSort {
    /**
     * 堆排序 代码
     *
     * @param arr
     */
    public static void heapSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        //加入堆
        for (int i = 0; i < arr.length; i++) {
            heapInsert(arr, i);
        }

        int size = arr.length;
        swap(arr, 0, --size);// 把最大值放到最后  堆范围-1
        while (size > 0) {
            heapify(arr, 0, size);
            swap(arr, 0, --size);
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
        while (arr[index] > arr[(index - 1) / 2]) {
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
     * 此方法只做2-3
     *
     * @param arr
     * @param index
     * @param size
     */
    public static void heapify(int[] arr, int index, int size) {
        int left = index * 2 + 1; // 拿到左节点下标
        while (left < size) { // 规定范围 且让左节点必须存在
            int largest = left + 1 < size && arr[left + 1] > arr[left] ? left + 1 : left;// 有节点存在 且大于右节点 给右节点 不然返回左节点
            largest = arr[largest] > arr[index] ? largest : index;//
            if (largest == index) {
                break;
            }
            // 走到这  说明子节点较大
            swap(arr, index, largest);// 交换
            index = largest;// 当前节点来到交换后的节点
            left = index * 2 + 1;

        }

    }

    public static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    // for test
    public static void comparator(int[] arr) {
        Arrays.sort(arr);
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
    public static int[] copyArray(int[] arr) {
        if (arr == null) {
            return null;
        }
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = arr[i];
        }
        return res;
    }

    // for test
    public static boolean isEqual(int[] arr1, int[] arr2) {
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
            int[] arr2 = copyArray(arr1);
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
        heapSort(arr);
        printArray(arr);
    }

}
