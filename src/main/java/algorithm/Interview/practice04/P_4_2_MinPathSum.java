package algorithm.Interview.practice04;

/**
 * @author mood321
 * @date 2020/6/29 0:34
 * @email 371428187@qq.com
 */
public class P_4_2_MinPathSum {


    // code
    //动态规划思想，用dp[ i ] [ j ]表示从左上角到坐标为（i,j）元素的最小路径和。
    //
    //    则 dp[i] [j]=min(dp[i-1] [j],dp[i] [ j-1])+grid[i] [j]
    public int minPathSum(int[][] grid) {
        if (grid.length == 0 || grid == null)
            return 0;
        int[][] dp = new int[grid.length][grid[0].length];
        dp[0][0] = grid[0][0];
        for (int i = 1; i < grid.length; i++) {
            dp[i][0] = dp[i - 1][0] + grid[i][0];
        }
        for (int j = 1; j < grid[0].length; j++) {
            dp[0][j] = dp[0][j - 1] + grid[0][j];
        }
        for (int i = 1; i < grid.length; i++) {
            for (int j = 1; j < grid[0].length; j++) {
                dp[i][j] = Math.min(dp[i - 1][j], dp[i][j - 1]) + grid[i][j];
            }
        }
        return dp[grid.length - 1][grid[0].length - 1];
    }
        // 递归
    public static int minPath1(int[][] matrix) {
        return minPath1(matrix, 0, 0);
    }

    public static int minPath1(int[][] matrix, int i, int j) {
        if (i == matrix.length - 1 && j == matrix[0].length - 1) {
            return matrix[i][j];
        }
        if (i == matrix.length - 1 && j != matrix[0].length - 1) {
            return matrix[i][j] + minPath1(matrix, i, j + 1);
        }
        if (j == matrix[0].length - 1 && i != matrix.length - 1) {
            return matrix[i][j] + minPath1(matrix, i + 1, j);
        }
        int right = minPath1(matrix, i, j + 1);
        int down =  minPath1(matrix, i + 1, j);
        return matrix[i][j] + Math.min(right, down);
    }
}