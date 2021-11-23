package algorithm.Interview.practice06;

/**
 * @author mood321
 * @date 2021/10/28 23:54
 * @email 371428187@qq.com
 */
public class P_5_2_GetMax {
    public int getMax(int m, int n){
         int c=  m-n;
         // m 的征服
        int sm = singe(m);
        //n 的正負
        int sn = singe(n);
        // c 的正負
        int sc = singe(c);
        // m,n 符號
        int dif = sm ^ sn;
        // 
        int sd = flip(dif);
        int returnA = dif * sm + sd * sc;
        int returnB = flip(returnA);

        return returnA*m+returnB*n;
    }

    /**
     * 与1 异或
     * @param i
     * @return
     */
    public int flip(int i){
        return i^1;
    }

    /**
     *  为0 或者正数  返回1 
     * @param i
     * @return
     */
    public  int singe(int i){
        return  flip(i>>31)&1;
    }
}