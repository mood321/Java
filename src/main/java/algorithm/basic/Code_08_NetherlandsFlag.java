package algorithm.basic;

/**
 * @Created by mood321
 * @Date 2019/10/4
 * @Description TODO
 * （荷兰国旗问题）
 * 给定一个数组arr，和一个数num，请把小于num的数放在数组的左边，等于num的数放在数组的中间，大于num的数放在数组的右边。
 * 要求额外空间复杂度O(1)，时间复杂度O(N)
 * 思路:
 * 1. 数组应该分成三部分 第一部分小于num 范围是0～less(less 默认-1 不存在 包含less)、
 * 等于num的范围less+1～more-1 (more默认位置长度+1)
 * 大于num的范围more～数组长度（包含more）
 * 2.流程:
 * 2.1 在小于more的范围内遍历数组 用cur表示当前数下标
 * 2.2 如果cur所在的值小于num  cur和less+1 交换 ,cur++
 * 2.3 如果等于  cur++
 * 2.4 如果大于 cur所在值与more-1的值交换 ，进行下一次循环
 */
public class Code_08_NetherlandsFlag {

    public static int[] partition(int[] arr, int l, int r, int num) {
        int less = l - 1, more = r + 1, cur = 0;

        while (cur < more) {
            if (arr[cur] < num) {
                swap(arr, ++less, cur++);
            } else if (arr[cur] > num) {
                swap(arr, --more ,cur);
            } else {
                cur++;
            }
        }
        return new int[]{less + 1, more - 1};
    }

    public static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
    // for test
    public static int[] generateArray() {
        int[] arr = new int[10];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (int) (Math.random() * 3);
        }
        return arr;
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

    public static void main(String[] args) {
        int[] test = generateArray();

        printArray(test);
        int[] res = partition(test, 0, test.length - 1, 1);
        printArray(test);
        System.out.println(res[0]);
        System.out.println(res[1]);

    }
}
