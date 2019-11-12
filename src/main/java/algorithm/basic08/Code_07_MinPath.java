package algorithm.basic08;

/**
 * @author mood321
 * @date 2019/11/12 23:02
 * @email 371428187@qq.com
 */
public class Code_07_MinPath {

    public static int minPath(int[][] matrix) {
        if (matrix == null) {
            return 0;
        }
        return processl(matrix, matrix.length - 1, matrix[0].length - 1);
    }

    private static int processl(int[][] matrix, int i, int j) {
        int res = matrix[i][j];
        if (i == 0 && j == 0) {
            //System.out.println(res);
            return res;

        }
        if (i == 0 && j != 0) {
            return res + processl(matrix, i, j - 1);
        }
        if (i != 0 && j == 0)
            return res + processl(matrix, i - 1, j);

        int r = res + processl(matrix, i - 1, j);
        int c = res + processl(matrix, i, j - 1);
        return Math.min(r, c);
    }

    // for test
    public static int[][] generateRandomMatrix(int rowSize, int colSize) {
        if (rowSize < 0 || colSize < 0) {
            return null;
        }
        int[][] result = new int[rowSize][colSize];
        for (int i = 0; i != result.length; i++) {
            for (int j = 0; j != result[0].length; j++) {
                result[i][j] = (int) (Math.random() * 10);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[][] m = {{1, 3, 5, 9}, {8, 1, 3, 4}, {5, 0, 6, 1}, {8, 8, 4, 0}};
        System.out.println(minPath(m));
        System.out.println(minPath2(m));

        m = generateRandomMatrix(6, 7);
        System.out.println(minPath(m));
        System.out.println(minPath2(m));
    }

    // 改动态规划
    private static int minPath2(int[][] m) {
        if (m == null || m[0] == null || m.length == 0 || m[0].length == 0)
            return 0;// 二维数组 不成立   返回0
        int r = m.length;
        int c = m[0].length;
        int[][] dp = new int[r][c];
        dp[0][0]=m[0][0];
        for (int i = 1; i < c; i++) {
            dp[0][i] = m[0][i]+dp[0][i-1];
        }      // 初始化矩阵  第一列
        for (int i = 1; i < r; i++) {
            dp[i][0] = m[i][0]+dp[i-1][0];
        }      // 初始化矩阵 第一行
        for (int i = 1; i < r; i++) {
            for (int j = 1; j < c; j++) {
                int min = Math.min(dp[i - 1][j], dp[i][j - 1]);
                dp[i][j] = min + m[i][j];
            }
        }
        return dp[r - 1][c - 1] ;
    }
}