package algorithm.advanced01;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author mood321
 * @date 2019/11/19 21:07
 * @email 371428187@qq.com
 * @desc 实时从窗口中取出
 */
public class WindowMaxNun {

    public static int[] getWindowsMaxnum(int[] arr, int w) {
        if (arr == null && arr.length == 0)
            return new int[] {};

        int[] res = new int[arr.length - w+1];
        int index = 0;
        LinkedList<Integer> mQeue = new LinkedList<>();
        for (int i = 0; i < arr.length; i++) {
            while (!mQeue.isEmpty() && mQeue.peekLast() <= arr[i]) {
                mQeue.pollLast();
            }
            mQeue.addLast(i);
            if (mQeue.getFirst() == i - w)
                mQeue.pollFirst();

            if (w - 1 <= i)
                res[index++]=mQeue.peekFirst();

        }
        return  res;

    }

    public static void main(String[] args) {
        int[] ints = {0, 1, 2, 3, 4, 5};
        Arrays.stream(getWindowsMaxnum(ints,3)).forEach(i-> System.out.println(i));
    }
}