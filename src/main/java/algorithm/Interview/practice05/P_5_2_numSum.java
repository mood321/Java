package algorithm.Interview.practice05;

/**
 * @author mood321
 * @date 2020/8/18 0:27
 * @email 371428187@qq.com
 */
public class P_5_2_numSum {

    public static int numSum(String str) {
        //如果字符串为null或者字符串的长度为零，返回0；
        if (str == null || str.length() == 0)
            return 0;
        char[] chars = str.toCharArray();
        int res = 0;
        int num = 0;
        boolean posi = true;
        int cur = 0;
        for (int i = 0; i < chars.length; i++) {
            cur = chars[i] - '0';
            //判断当前字符是否为数字
            if (cur >= 0 && cur <= 9) {
                //数字增加一位
                num = num * 10 + (posi ? cur : -cur);
            } else {
                //一旦遇到了非数字，那么就将当前数字加到结果res上
                res += num;
                num = 0;
                //如果不是负号
                if (chars[i] != '-') {
                    posi = true;
                } else {
                    //是负号，但是仍要判断当前字符是否是第一个字符||前一个字符是否是负号
                    if (i == 0 || chars[i - 1] != '-') {
                        posi = false;
                    } else
                        posi = !posi;
                }
            }
        }
        return res;
    }

}