package algorithm.basic08;

/**
 * @author mood321
 * @date 2019/11/13 22:13
 * @email 371428187@qq.com
 */
public class Code_09_Knapsack {


    public static void main(String[] args) {
        int[] c = {3, 2, 4, 7};
        int[] p = {5, 6, 3, 19};
        int bag = 11;
        System.out.println(maxValue1(c, p, bag));
        System.out.println(maxValue2(c, p, bag));
    }


    private static int maxValue1(int[] c, int[] p, int bag) {
        return process(c, p, 0, 0, bag);

    }

    private static int process(int[] c, int[] p, int i, int ps, int bag) {
        if (ps > bag)                // 边界  商品重量超过
            return 0;
        if (i == c.length)          // 边界 商品用完了
            return 0;

        return Math.max(process(c, p, i + 1, ps, bag), p[i] + process(c, p, i + 1, ps + c[i], bag));   // 决策 拿出 还是不拿 哪个收益高
    }

    /**
     * 动态规划
     * 边界是 商品个数 和  商品重量
     * 初始化值是 超出的0
     * 关系是 拿出商品从栈拿出的值 还是不拿商品 从栈拿出的值  这时谁大选择谁
     */
    private static int maxValue2(int[] c, int[] p, int bag) {
        int[][] dp = new int[c.length + 1][bag + 1];
        // 不初始化  java 特性 int=0
        for (int i = c.length - 1; i >= 0; i--) {
            for (int j = bag; j >= 0; j--) {   // bag 不能减1  相等也成立
                dp[i][j] = dp[i + 1][j];
                if (c[i] + j <= bag) // 规定 边界   节省 dp空间
                    dp[i][j] = Math.max(/*不拿*/ dp[i][j],/*拿*/ p[i] + dp[i + 1][j + c[i]]);
            }
        }
        return dp[0][0];
    }
}