package algorithm.Interview.practice04;

/**
 * @author mood321
 * @date 2020/7/15 0:31
 * @email 371428187@qq.com
 */
public class P_4_9_MinCost {
    public int findMinCost(String A, int n, String B, int m, int ic, int dc, int rc) {
        if(A==null||B==null)
            return 0;
        if(n==0) return m*ic;
        if(m==0) return n*ic;
        int[][] dp=new int[n+1][m+1];
        dp[0][0]=0;
        for(int i=1;i<m+1;++i){
            dp[0][i]=ic*i;
        }
        for(int i=1;i<n+1;++i){
            dp[i][0]=dc*i;
        }
        for(int i=1;i<n+1;++i){
            for(int j=1;j<m+1;++j){
                if(A.charAt(i-1)==B.charAt(j-1)){
                    dp[i][j]=dp[i-1][j-1];
                }
                else{
                    dp[i][j]=dp[i-1][j-1]+rc;
                }
                dp[i][j]=Math.min(dp[i][j],dp[i-1][j]+dc);
                dp[i][j]=Math.min(dp[i][j],dp[i][j-1]+ic);
            }
        }
        return dp[n][m];
    }
}