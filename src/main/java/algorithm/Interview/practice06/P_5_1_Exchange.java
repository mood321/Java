package algorithm.Interview.practice06;

/**
 * @author mood321
 * @date 2021/10/27 23:48
 * @email 371428187@qq.com
 */
public class P_5_1_Exchange {
    /**
     *  相加  和3,5 毫升水瓶 求4一样
     * @param m
     * @param n
     */
    public  void exchange01(int m, int n){

        // 求和
        m=m+n;
        // 此时 m为和  减去原来的n 为n1  ,得到n2 为原来m的值
        n=m-n;
        // 此时m还是和   减去n2  的到 原来n的值
        m=m-n;
    }
    /**
     *  异或  记录两数的特征
     * @param m
     * @param n
     */
    public  void exchange02(int m, int n){

        // 求和 011和 100  结果000
        m=m^n;
        // 此时 m为00  通过n1 的特征  ,拿到m的特征
        n=m^n;
        // 此时m为00  通过m 的特征  ,拿到n的特征
        m=m^n;
    }
}