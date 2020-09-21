package algorithm.Interview.practice05;

/**
 * @author mood321
 * @date 2020/9/22 0:01
 * @email 371428187@qq.com
 */
public class P_5_7_Chapter {
    public String getCountString(String str) {
        if (str == null) {
            return null;
        }
        char[] chars = str.toCharArray();
        char cur = chars[0];
        StringBuilder res = new StringBuilder(cur + "_");
        int num = 1;

        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == chars[i - 1]) {
                num++;
            } else {
                res.append(num + "_" + chars[i] + "_");
                cur = chars[i];
                num = 1;
            }
        }
        res.append(num);
        return res.toString();
    }

    public char getCharAt(String str, int k) {
        if (str == null) {
            return 0;
        }
        char[] chars = str.toCharArray();
        char cur = 0;
        int num = 0;
        int sum = 0;
        boolean posi = true;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '_') {
                posi = !posi;
            } else if (posi) {
                sum += num;
                if (sum > k) {
                    return cur;
                }
                num = 0;
                cur = chars[i];
            } else {
                num = num * 10 + chars[i] - '0';
            }
        }
        return cur;
    }

    //测试
    public static void main(String[] args) {
        P_5_7_Chapter chapter = new P_5_7_Chapter();
        String str1 = "aaabbadddffc";
        String result = chapter.getCountString(str1);
        System.out.println("aaabbadddffc的统计字符串：" + result);
        char charAtK = chapter.getCharAt(result, 4);
        System.out.println(result + "第4位置的字符为：" + charAtK);

    }
}