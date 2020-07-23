package algorithm.Interview.practice04;

/**
 * @author mood321
 * @date 2020/7/24 0:35
 * @email 371428187@qq.com
 */
public class P_4_13_ExpreeNum {
    //判断是否是有效的表达式
    public static boolean isValid(char[] exp) {
        //表达式的长度必须是奇数
        if ((exp.length & 1) == 0) {
            return false;
        }
        //偶数位置必须为0或1
        for (int i = 0; i < exp.length; i += 2) {
            if ((exp[i] != '0') && (exp[i] != '1')) {
                return false;
            }
        }
        //奇数位置必须为& | ^
        for (int i = 1; i < exp.length; i += 2) {
            if ((exp[i] != '&') && (exp[i] != '|') && (exp[i] != '^')) {
                return false;
            }

        }


        return true;

    }


    //暴力递归法(总共分为6种情况(根据desired,每个分为三种情况))
    public static int num01(String express, boolean desired) {
        if (express == null || express.equals("")) {
            return 0;
        }
        //字符串转数组
        char[] exp = express.toCharArray();
        if (!isValid(exp)) {
            return 0;
        }
        //递归函数
        return p(exp, desired, 0, exp.length - 1);

    }

    //暴力递归的函数
    public static int p(char[] exp, boolean desired, int l, int r) {
        if (l == r) {
            if (exp[l] == '1') {
                return desired ? 1 : 0;
            } else {
                return desired ? 0 : 1;
            }
        }
        int res = 0;
        //desired为true分为三种情况
        if (desired) {
            for (int i = l + 1; i < r; i += 2) {
                switch (exp[i]) {
                    case '&':
                        res += p(exp, true, l, i - 1) * p(exp, true, i + 1, r);
                        break;
                    case '|':
                        res += p(exp, true, l, i - 1) * p(exp, false, i + 1, r);
                        res += p(exp, false, l, i - 1) * p(exp, true, i + 1, r);
                        res += p(exp, true, l, i - 1) * p(exp, true, i + 1, r);
                        break;
                    case '^':
                        res += p(exp, true, l, i - 1) * p(exp, false, i + 1, r);
                        res += p(exp, false, l, i - 1) * p(exp, true, i + 1, r);
                        break;
                }
            }

        }
        //desired为false分为三种情况
        else {
            for (int i = l + 1; i < r; i += 2) {
                switch (exp[i]) {
                    case '&':
                        res += p(exp, false, l, i - 1) * p(exp, true, i + 1, r);
                        res += p(exp, true, l, i - 1) * p(exp, false, i + 1, r);
                        res += p(exp, false, l, i - 1) * p(exp, false, i + 1, r);
                        break;
                    case '|':
                        res += p(exp, false, l, i - 1) * p(exp, false, i + 1, r);
                        break;
                    case '^':
                        res += p(exp, true, l, i - 1) * p(exp, true, i + 1, r);
                        res += p(exp, false, l, i - 1) * p(exp, false, i + 1, r);
                        break;
                }
            }

        }

        return res;

    }

    //动态规划法
    public static int num02(String express, boolean desired) {
        if (express == null || express.equals("")) {
            return 0;
        }
        //字符串转数组
        char[] exp = express.toCharArray();
        if (!isValid(exp)) {
            return 0;
        }
        //构造动态规划矩阵
        int[][] t = new int[exp.length][exp.length]; //true矩阵
        int[][] f = new int[exp.length][exp.length]; //false矩阵
        t[0][0] = exp[0] == '0' ? 0 : 1;
        f[0][0] = exp[0] == '1' ? 0 : 1;

        //偶数位置必须为数字
        for (int i = 2; i < exp.length; i += 2) {
            t[i][i] = exp[i] == '0' ? 0 : 1;
            f[i][i] = exp[i] == '1' ? 0 : 1;
            for (int j = i - 2; j >= 0; j -= 2) {
                for (int k = j; k < i; k += 2) {
                    //三种情况分开考虑
                    if (exp[k + 1] == '&') {
                        t[j][i] += t[j][k] * t[k + 2][i];
                        f[j][i] += (f[j][k] + t[j][k]) * f[k + 2][i] + f[j][k] * t[k + 2][i];
                    } else if (exp[k + 1] == '|') {
                        t[j][i] += (f[j][k] + t[j][k]) * t[k + 2][i] + t[j][k] * f[k + 2][i];
                        f[j][i] += f[j][k] * f[k + 2][i];
                    } else {
                        t[j][i] += f[j][k] * t[k + 2][i] + t[j][k] * f[k + 2][i];
                        f[j][i] += f[j][k] * f[k + 2][i] + t[j][k] * t[k + 2][i];
                    }
                }
            }
        }
        //返回右上角位置
        return desired ? t[0][t.length - 1] : f[0][f.length - 1];

    }

    public static void main(String[] args) {
        String express = "1^0|0|1";
        System.out.println(num01(express, false));
        System.out.println(num02(express, false));
    }

}