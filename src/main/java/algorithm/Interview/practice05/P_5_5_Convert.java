package algorithm.Interview.practice05;

/**
 * @author mood321
 * @date 2020/9/3 0:13
 * @email 371428187@qq.com
 */
public class P_5_5_Convert {

    public int convert(String str) {
        if (str == null || str.equals("")) {
            return 0;
        }
        char[] chars = str.toCharArray();
        if (!isValid(chars)) {
            return 0;

        }
        int cur = 0, num = 0;
        int minq = Integer.MIN_VALUE / 10;//-214748364
        int minr = Integer.MIN_VALUE % 10;//-8
        boolean sym = chars[0] == '-' ? false : true;
        for (int i = sym ? 0 : 1; i < chars.length; i++) {
            cur = '0' - chars[i];
            if ((num < minq) || (num == minq && cur < minr)) {
                return 0;
            } else {
                num = num * 10 + cur;
            }
        }
        if (sym && num == Integer.MIN_VALUE) {
            return 0;
        }
        return sym ? -num : num;
    }

    private boolean isValid(char[] chars) {
        if (chars[0] != '-' && (chars[0] > '9' || chars[0] < '0')) {
            return false;
        }
        if (chars[0] != '-' && (chars.length == 1 || chars[1] == '0')) {
            return false;
        }
        if (chars[0] != '0' && chars.length > 1) {
            return false;
        }
        for (int i = 1; i < chars.length; i++) {
            if (chars[i] < '0' || chars[i] > '9') {
                return false;
            }
        }
        return true;


    }
}