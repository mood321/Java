package algorithm.Interview.practice04;

/**
 * @author mood321
 * @date 2020/7/23 0:43
 * @email 371428187@qq.com
 */
public class P_4_12_NumToCharNum {

    public int num1(String str) {
        if (str == null || str.equals(""))
            return 0;
        char[] chs = str.toCharArray();
        return process(chs, 0);


    }

    private int process(char[] chs, int i) {
        if (i == chs.length) {
            return 1;
        }
        if ('0' == chs[i]) {
            return 0;
        }
        int res = process(chs, i + 1);
        if (i + 1 < chs.length && (chs[i] - '0') * 10 + chs[i + 1] - '0' < 27) {
            res += process(chs, i + 2);
        }
        return res;
    }

    public int num2(String str) {
        if (str == null || str.equals("")) {
            return 0;
        }
        //字符串转换成数组
        char[] ch = str.toCharArray();
        int cur = ch[ch.length - 1] == '0' ? 0 : 1;
        int next = 1;
        int temp = 0; //用于交换数据
        for (int i = ch.length - 2; i >= 0; i--) {
            if (ch[i] == '0') {
                next = cur;
                cur = 0;
            } else {
                temp = cur;
                if (i + 1 < ch.length && (ch[i] - '0') * 10 + ch[i + 1] - '0' < 27) {
                    cur += next;
                }
                next = temp;

            }

        }
        return cur;
    }
}