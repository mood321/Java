package algorithm.Interview.practice06;

/**
 * @author mood321
 * @date 2021/11/23 23:51
 * @email 371428187@qq.com
 */
public class P_5_3_Count {

    /**
     *  无符号右移  计算
     * @param i
     * @return
     */
    public  int  count(int i){

        int count=0;
        while (i != 0){
            i+=i&1;
            i>>>=1;
        }
        return  count;
    }

    /**
     *  利用算法
     * @param n
     * @return
     */
    public  int count2(int   n){
        int  count=0;

        while(n!=0){
            // “a”的值是129，转换成二进制就是10000001，而“b”的值是128，转换成二进制就是10000000。
            // 根据与运算符的运算规律，只有两个位都是1，结果才是1，可以知道结果就是10000000，即128。
            n&=(n-1);
            count++;
        }

        return count;
    }

    public static void main(String[] args) {

        int n=4;
        String b = Integer.toBinaryString(n);
        System.out.println(""+b);
         b = Integer.toBinaryString(n-1);
        System.out.println(""+b);
        System.out.printf( (n&=(n-1)) +"");

    }
}