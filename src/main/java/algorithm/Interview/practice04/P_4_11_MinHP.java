package algorithm.Interview.practice04;

/**
 * @author mood321
 * @date 2020/7/22 0:21
 * @email 371428187@qq.com
 */
public class P_4_11_MinHP {

    public int minHP(int[][] arr) {
        if (arr == null || arr.length == 0 || arr[0] == null || arr[0].length == 0) {
            return 1;
        }
        int row = arr.length;
        int col = arr[0].length;

        int[][] dp = new int[row--][col--];
        dp[row][col] = arr[row][col] > 0 ? 1 : -arr[row][col] + 1;

        for (int i = col-1; i >=0 ; i--) {
             dp[row][i]=Math.max(dp[row][i+1]-arr[row][i],1) ;
        }
        int right=0;
        int down=0;

        for (int i = row-1; i >=0 ; i--) {
            dp[i][col]=Math.max(dp[i+1][col]=arr[i][col],1) ;
            for (int j = col-1; j <=0 ; j--) {
                right=Math.max(dp[i][j+1]-arr[i][j],1);

                down=Math.max(dp[i+1][j]-arr[i][j],1);
                dp[i][j]=Math.min(right,down);
            }

        }

        return dp[0][0];
    }
}