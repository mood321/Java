package algorithm.basic08;

/**
 * @author mood321
 * @date 2019/11/12 22:16
 * @email 371428187@qq.com
 */
public class Code_05_Cow {
    /**
     *  1 2 3 4 5 6
     *  1 2 3 4 6 9
     *  思路: 计算N年的牛  N-1的牛不会死 f(n-1)  +  三年钱生产的牛 f(n-3)
     *
     */
    public static int cowNumber1(int n) {
        if(n<=0)
            return 0;
        if(n==1 || n==2 || n==3)
            return n;
        return  cowNumber1(n-1)+cowNumber1(n-3);
        
    }

    public static void main(String[] args) {
        System.out.println(cowNumber1(6)); ;
    }
}