package algorithm.Interview.practice04;

/**
 * @author mood321
 * @date 2020/7/7 0:44
 * @email 371428187@qq.com
 */
public class P_4_4_MaxLength {

    /**
     * 最长递增子序列 很有意义
     * @param arr 4,-2,3,-4,6,7
     * @return
     */
    public static int[] getdp1(int[] arr){
        int[] dp = new int[arr.length];
        int[] end = new int[arr.length];
        end[0] = arr[0];
        dp[0] = 1;
        int bianjie = 0; //end数组的边界
        for(int i = 1;i<arr.length;i++){
            int index = method(end, arr[i]);
            index = index > bianjie ? ++bianjie : index; //对于越界的处理
            end[index] = arr[i];
            int length = index + 1;
            dp[i] = length;
        }

//        return dp;
        return generateLIS(arr, dp);
    }
    /**
     * 一个递增数列 获取第一个大于等于k的index 比所有的都大的话 会返回数组的长度
     * @param array
     * @param k
     * @return
     */
    public static int method(int[] array, int k){
        int mid = -1;
        int left = 0;
        int right = array.length - 1;
        while(left <= right){
            mid = (left + right) / 2;
            if(array[mid] >= k){
                right = mid - 1;
            }else{
                left = mid + 1;
            }
        }
        return left;
    }
    public static int[] generateLIS(int[] arr, int[] dp){
        int len = Integer.MIN_VALUE;
        int index = 0;
        for(int i = 0;i<dp.length;i++){
            if(dp[i] > len){
                len = dp[i];
                index = i;
            }
        }
        int[] lis = new int[len];
        lis[--len] = arr[index];
        for(int i = index;i>=0;i--){
            if(arr[i] < arr[index] && dp[i] == dp[index] - 1){
                lis[--len] = arr[i];
                index = i;
            }
        }
        return lis;
    }

    public static void main(String[] args) {
        int[] a = {4,-2,3,-4,6,7};
        int[] dp = getdp1(a);
        for(int i : dp){
            System.out.println(i);
        }
    }
}