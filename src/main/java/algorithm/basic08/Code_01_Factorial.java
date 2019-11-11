package algorithm.basic08;

/**
 * @author mood321
 * @date 2019/11/12 1:09
 * @email 371428187@qq.com
 */
public class Code_01_Factorial {
    public static int getFactorial1(int n) {
        if (n == 1) {
            return 1;
        }
        return n *getFactorial1(n-1);
    }
    public static long getFactorial2(int n) {
        long result = 1L;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static void main(String[] args) {
        int n = 5;
        System.out.println(getFactorial1(n));
        System.out.println(getFactorial2(n));
    }
}