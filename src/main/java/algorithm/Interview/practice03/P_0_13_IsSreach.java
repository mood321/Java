package algorithm.Interview.practice03;

/**
 * @author mood321
 * @date 2020/5/14 0:14
 * @email 371428187@qq.com
 */
public class P_0_13_IsSreach {
    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    public static boolean isPosArray(int[] arr){
        if(arr ==null || arr.length ==0){
            return false;
        }
        return isPos(arr ,0,arr.length-1);
    }

    private static boolean isPos(int[] arr, int start, int end) {
        if(start == end){
            return  true;// 循环完
        }
        int less =-1;
        int more=end;
        for (int i = start; i < end; i++) { // 找到分隔

            if(arr[end]>arr[i]){
                less=i;
            }else {
                more =more==end?i:more; // 第一次遇到 大的 的范围;
            }
            if(less ==-1 || end==more){
                return isPos(arr,start,end-1);
            }
            if(less != more-1){// 这说明  遇到比root 大的之后  ,又遇到比他小的

                return  false;
            }
        }
        return isPos(arr,start,less) && isPos(arr, more, end-1);
    }

    /**
     *  重建 和 判断思路 一毛一样 就不写注释了
     * @param arr
     * @return
     */
    public static Node posArrayToBST(int[] arr) {
        if (arr == null) {
            return null;
        }
        return posToArray(arr, 0, arr.length - 1);
    }

    public static Node posToArray(int[] arr, int start, int end) {
        if (start > end) {
            return null;
        }
        Node head = new Node(arr[end]);
        int lessRight = -1;
        int moreLeft = end;
        for (int i = start; i < end; i++) {
            if (arr[i] < arr[end]) {
                lessRight = i;
            } else {
                moreLeft = moreLeft == end ? i : moreLeft;
            }
        }
        head.left = posToArray(arr, start, lessRight);
        head.right = posToArray(arr, moreLeft, end - 1);
        return head;
    }
}