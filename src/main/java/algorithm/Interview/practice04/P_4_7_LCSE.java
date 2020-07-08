package algorithm.Interview.practice04;

/**
 * @author mood321
 * @date 2020/7/9 0:45
 * @email 371428187@qq.com
 */
public class P_4_7_LCSE {

    public static void main(String[] args) {
        String s1 = "1A2C3D4B56";
        String s2 = "B1D23CA45B6A";
        System.out.println(lcse(s1, s2));
    }

    public static String lcse(String str1, String str2){
        String res = "";
        if (str1 == null || str2 == null || str1.length() == 0 || str2.length() == 0){
            return res;
        }
        int[][] dp = new int[str2.length()][str1.length()];
        char[] chars1 = str1.toCharArray();
        char[] chars2 = str2.toCharArray();
        for (int i = 0; i < str1.length(); i++) {
            if (chars1[i] == chars2[0]){
                for (; i< str1.length() ; i++) {
                    dp[0][i] = 1;
                }
            }
            else{
                dp[0][i] = 0;
            }
        }
        for (int i = 0; i < str2.length(); i++) {
            if (chars2[i] == chars1[0]){
                for (;i<str2.length();i++){
                    dp[i][0] = 1;
                }
            }
            else {
                dp[i][0] = 0;
            }
        }

        for (int i = 1; i < str2.length(); i++) {
            for (int j = 1; j < str1.length(); j++) {
                if (chars1[j] == chars2[i]){
                    dp[i][j] = dp[i-1][j-1] + 1;
                }
                dp[i][j] = Math.max(dp[i][j], Math.max(dp[i-1][j], dp[i][j-1]));
            }
        }
        //根据dp记录的值求string
        int row = str2.length() -1, col = str1.length() -1;
        while (row >= 1 && col >= 1) {
            if (dp[row - 1][col] == dp[row][col]) {
                --row;
            } else if (dp[row][col - 1] == dp[row][col]) {
                --col;
            } else {
                res = chars1[col] + res;
                --row;
                --col;
            }
        }
        if (dp[row][col] == 1){
            if (row == 0){
                res = chars2[0] + res;
            }
            else {
                res = chars1[0] + res;
            }
        }
        return res;
    }
}