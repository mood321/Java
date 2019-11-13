package algorithm.basic08;

/**
 * @author mood321
 * @date 2019/11/13 20:23
 * @email 371428187@qq.com
 */
public class Code_08_Money_Problem {
    public static void main(String[] args) {
        int[] arr = {1, 4, 8};
        int aim = 15;
        System.out.println(money1(arr, aim));
        System.out.println(money2(arr, aim));
    }

    // 动态规划
    private static boolean money2(int[] arr, int aim) {
        boolean[][] dp = new boolean[arr.length+1][aim + 1];

        for (int i = 0; i < dp.length; i++) {
            dp[i][aim] = true;
        }
        for (int length = dp.length - 2; length >= 0; length--) {       // 最后一行是预留 上面已经初始化
            for (int num = dp[0].length - 2; num >= 0; num--) {         // 最后一列 全true
                 dp[length][num]=dp[length+1][num];                      // 
                 if(num+arr[length]<= aim)
                     dp[length][num]=dp[length][num]|| dp[length+1][num+arr[length]];
            }
        }
        return dp[0][0];

    }

    // 递归
    private static boolean money1(int[] arr, int aim) {
        if (arr == null) {
            return false;
        }
        return process1(arr, 0, 0, aim);
    }

    private static boolean process1(int[] arr, int i, int sum, int aim) {
        if (sum == aim)
            return true;
        if (i == arr.length)
            return false;
        return process1(arr, i + 1, sum + arr[i], aim) || process1(arr, i + 1, sum, aim);
    }

}