package algorithm.Interview.practice04;

/**
 * @author mood321
 * @date 2020/6/17 0:39
 * @email 371428187@qq.com
 * @Des  斐波那契系列问题
 */
public class P_4_1_Fibonacci {
    /**
     * 1 斐波那契数列
     * 递归 O(2^N)
     */
    public static  int f1(int n){
        if(n< 1){
            return 0;
        }
        if(n==1 || n== 2){
            return 1;
        }
        return f1(n-1)+f1(n-2);
    }

    /**
     * 2 推导
     * O(n)
     */
    public static  int f2(int n){
        if(n< 1){
            return 0;
        }
        if(n==1 || n== 2){
            return 1;
        }
       int  pre=1;
       int  res=1;
       int  tem=1;
        for (int i = 3; i <=n ; i++) {
            tem=res;
            res=pre+res;
            pre=tem;
        }
        return res;
    }
    /**
     *  3 动态规划
     *  O(Log N)
     */
    public static  int f3(int n){
        if(n< 1){
            return 0;
        }
        if(n==1 || n== 2){
            return 1;
        }
        int  pre=1;
        int  res=1;
        int  tem=1;
        for (int i = 3; i <=n ; i++) {
            tem=res;
            res=pre+res;
            pre=tem;
        }
        return res;
    }
}