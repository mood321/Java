package offer;

/**
 * 大家都知道斐波那契数列，现在要求输入一个整数n，请你输出斐波那契数列的第n项（从0开始，第0项为0）。
 * n<=39
 */
public class 斐波那契数列 {
    public static void main(String[] args) {
        Fibonacci(4);
    }

    /**
     * 递归次数太多会
     *  Exception in thread "main" java.lang.StackOverflowError
     * @param n
     * @return
     */
    public static  int Fibonacci(int n) {

        return Fibonacci(n,0,1);
    }

    public static int Fibonacci2(int n) {
        int one=0,two=1;
        if(n<=0){
            return one;
        }
        if(n==1){
            return two;
        }
        int i=2,sum=0;
        while(i<=n){

            sum=one+two;
            one=two;
            two=sum;
            i++;
        }
        return sum;
    }

    private static int Fibonacci(int n,int acc1,int acc2){
        if(n==0) return 0;
        if(n==1) return acc2;
        else     return Fibonacci(n - 1, acc2, acc1 + acc2);

    }
}
