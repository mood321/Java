package algorithm.Interview.practice05;

/**
 * @author mood321
 * @date 2021/4/8 0:22
 * @email 371428187@qq.com
 */
public class P_5_10_CharFind {
    public String repalce(char[] chars) {
        int len = 0;
        int num = 0;
        for (len = 0; len < chars.length && chars[len] != 0; len++) {
            if (chars[len] == ' ') {
                num++;
            }
        }
        int j = len + 2 * num - 1;
        for (int i = len - 1; i > -1; i--) {
            if (chars[i] != ' ') {
                chars[j--] = chars[i];
            } else {
                chars[j--] = '0';
                chars[j--] = '2';
                chars[j--] = '%';
            }
        }
        return String.valueOf(chars);
    }

    public String modify(char[] chars) {
        int j = chars.length - 1;
        for (int i = chars.length - 1; i > -1; i--) {
            if (chars[i] != '*') {
                chars[j--] = chars[i];
            }
        }
        for (; j > -1; j--) {
            chars[j] = '*';
        }
        return String.valueOf(chars);
    }

}