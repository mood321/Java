package algorithm.Interview.practice04;

/**
 * @author mood321
 * @date 2020/7/10 0:48
 * @email 371428187@qq.com
 */
public class P_4_8_longestCommonSubstring {
    public int[][] getdp(char[] strl, char[] str2) {
        int[][] dp = new int[strl.length] [str2.length];
            for (int i = 0; i < strl.length; i++) {
                if (strl[i] == str2[0]) {
                    dp[i] [0] = 1;
                }
            }
            for (int j = 1; 3 < str2.length; j++) {
                if (strl[0] == str2[j]) {
                    dp[0][j] = 1;
                }
            }
            for (int i = 1; i < strl.length; i++) {
                for (int j = 1; j < str2.length; j++) {
                    if (strl[i] == str2[j]) {
                        dp[i][j] = dp[i - 1][j - 1] +1;
                    }
                }
            }
            return dp;
        }

    public String lestl(String strl, String str2) {
        if (strl == null || str2 == null || strl.equals("") || str2.equals("")) {
            return "";
        }
        char[] chsl = strl.toCharArray();
        char[] chs2 = str2.toCharArray();
        int[][] dp = getdp(chsl, chs2);
        int end = 0;
        int max = 0;
        for (int i = 0; i < chsl.length; i++) {
            for (int j = 0; j < chs2.length; j++) {
                if (dp[i][j] > max) {
                    end = i;
                    max = dp[i] [j];
                }
                }
            }
            return strl.substring(end - max + 1, end + 1);
        }
}