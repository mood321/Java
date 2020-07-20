package algorithm.Interview.practice04;

/**
 * @author mood321
 * @date 2020/7/21 0:41
 * @email 371428187@qq.com
 */
public class P_4_10_IsCorss {
        public static boolean chkMixture(String A, int n, String B, int m, String C, int v) {
            // d[i][j]表示当A在i位处是交错的同时s2在j位处是交错的s3在i+j处是否是交错的。
            // 如果A和B在当前位置是空，C也是空，为true
            // 如果A为空，B之前的位置是交错的而且s2在当前位置和s3的当前位置字符是一样的，则视为true；反之s2为空时情况是一样的。
            // A和B都不为空，从i-1,j到达i,j处时，如果i-1,j处是交错的而i处与当前的s3一致，则视为true;
            // 当我们从i,j-1到达i,j处时，如果i,j-1处是交错的而j处与当前的s3一致，则视为true;
            boolean[][] d = new boolean[n + 1][m + 1];
            for (int i = 0; i <= n; i++) {
                for (int j = 0; j <= m; j++) {
                    if (i == 0 && j == 0) {
                        d[i][j] = true;
                    } else if (i == 0) {
                        d[i][j] = (d[i][j - 1] && B.charAt(j - 1) == C.charAt(i + j - 1));
                    } else if (j == 0) {
                        d[i][j] = (d[i - 1][j] && A.charAt(i - 1) == C.charAt(i + j - 1));
                    } else {
                        d[i][j] = (d[i - 1][j] && A.charAt(i - 1) == C.charAt(i + j - 1))
                                || (d[i][j - 1] && B.charAt(j - 1) == C.charAt(i + j - 1));
                    }
                }
            }
            return d[n][m];
        }

    public boolean isInterleave(String s1, String s2, String s3) {
        if(s1.length()+s2.length()!=s3.length())
            return false;

        //dp[i][j]表示s1长为i，s2长为j时与s3的匹配情况。
        boolean[][] dp=new boolean[s1.length()+1][s2.length()+1];        //若新建矩阵dp[s1.length()][s2.length()],则当s1.length() == 0||s2.length() == 0时，对dp[0][0]赋值会越界。

        dp[0][0]=true;
        //s1中取0个s2中取i个 去和s3中0+i 个匹配</code>
        for(int j=1;j<=s2.length();j++)
        {
            if(s2.charAt(j-1) == s3.charAt(j-1)&&dp[0][j-1] == true)
                dp[0][j]=true;
        }
        //s2中取0个s1中取i个 去和s3中0+i 个匹配</code>
        for(int i=1;i<=s1.length();i++)
        {
            if(s1.charAt(i-1) == s3.charAt(i-1)&&dp[i-1][0] == true)
                dp[i][0]=true;
        }
        for(int i=1;i<=s1.length();i++)
        {
            for(int j=1;j<=s2.length();j++)
            {
                dp[i][j]=dp[i-1][j]&&(s3.charAt(i+j-1) == s1.charAt(i-1))
                        ||dp[i][j-1]&&(s3.charAt(i+j-1) == s2.charAt(j-1));
            }
        }
        return dp[s1.length()][s2.length()];
    }
}