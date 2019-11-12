package algorithm.basic08;

/**
 * @author mood321
 * @date 2019/11/12 21:31
 * @email 371428187@qq.com
 * @desc 字符的子序列
 */
public class Code_03_Print_All_Subsquences {

    public static void printAllSubsquence(String str) {
        if (str == null)
            System.out.printf("");
        char[] chs = str.toCharArray();
        process(chs, 0);
    }

    public static void process(char[] chs, int i) {
        if (i == chs.length - 1) {
            System.out.println(new String(chs));
            return;
        }
        process(chs, i + 1);
        char ch = chs[i];
        chs[i] = Character.MIN_VALUE;
        process(chs, i + 1);
        chs[i] = ch;
        ;
    }

    public static void main(String[] args) {
        String s = "abc";
        printAllSubsquence(s);
    }
}