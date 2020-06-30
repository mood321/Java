package algorithm.Interview.practice04;

/**
 * @author mood321
 * @date 2020/7/1 0:45
 * @email 371428187@qq.com
 * <p>3 换钱的最小货币数
 * <p> 给定数组arr, arr中所有的值都为正数且不重复。每个值代表一种面值的货币，每种面值的货币可以使用任意张，再给定一个整数aim，代表要找的钱数，求组成aim的最少货币数。
 * <p> 进阶: 每种面值一张
 */
public class P_4_3_MinCoins {

    public int minCoins(int[] arr, int aim) {
        if (arr == null || arr.length == 0 || aim < 0) {
            return -1;
        }
        int[][] dp = new int[arr.length][aim + 1];
        int max = Integer.MAX_VALUE;
        //设置第一行
        for (int j = 1; j <= aim; j++) {
            dp[0][j] = max;
            if (j - arr[0] >= 0 && dp[0][j - arr[0]] != max) {
                dp[0][j] = dp[0][j - arr[0]] + 1;
            }
        }
        int left = 0;
        for (int i = 1; i < arr.length; i++) {
            for (int j = 1; j <= aim; j++) {
                left = max;
                if (j - arr[i] >= 0 && dp[i][j - arr[i]] != max) {
                    left = dp[i][j - arr[i]] + 1;
                }
                dp[i][j] = Math.min(left, dp[i - 1][j]);
            }
        }
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j <= aim; j++) {
                System.out.print(dp[i][j] + " ");
            }
            System.out.println();
        }
        return dp[arr.length - 1][aim] != max ? dp[arr.length - 1][aim] : -1;

    }

    //换钱的最小货币数_补充题目
    public static int minCoins3(int[] arr,int aim){
        if(arr==null||arr.length==0||aim<0){
            return -1;
        }
        int n=arr.length;
        int[][] dp=new int[n][aim+1];
        int max=Integer.MAX_VALUE;
        //dp第一行，只使用一张arr[0]，有dp[0][arr[0]]=1,其他为max
        for(int j=1;j<=aim;j++){
            dp[0][j]=max;
        }
        if(arr[0]<=aim){
            dp[0][arr[0]]=1;
        }
        int leftup=0;//左上角某个位置的值
        for(int i=1;i<n;i++){
            for(int j=1;j<=aim;j++){
                leftup=max;
                if(j-arr[i]>=0 && dp[i-1][j-arr[i]]!=max){
                    leftup=dp[i-1][j-arr[i]]+1;
                }
                dp[i][j]=Math.min(leftup, dp[i-1][j]);
            }
        }
        return dp[n-1][aim]!=max?dp[n-1][aim]:-1;
    }

    //方法2
    public static int minCoins4(int[] arr,int aim){
        if(arr==null||arr.length==0||aim<0){
            return -1;
        }
        int n=arr.length;
        int max=Integer.MAX_VALUE;
        int[] dp=new int[aim+1];
        for(int j=1;j<=aim;j++){
            dp[j]=max;
        }
        if(arr[0]<=aim){
            dp[arr[0]]=1;
        }
        int leftup=0;
        for(int i=1;i<n;i++){
            for(int j=aim;j>0;j--){
                //for(int j=1;j<=aim;j++){ 就不对！
                leftup=max;
                if(j-arr[i]>=0 && dp[j-arr[i]]!=max){
                    leftup=dp[j-arr[i]]+1;
                }
                dp[j]=Math.min(leftup, dp[j]);
            }
        }
        return dp[aim]!=max?dp[aim]:-1;
    }
}