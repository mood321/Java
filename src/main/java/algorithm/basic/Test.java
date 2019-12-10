package algorithm.basic;

import org.omg.PortableInterceptor.INACTIVE;

import java.io.Console;

/**
 * @Created by mood321
 * @Date 2019/10/8 0008
 * @Description TODO
 */
public class Test {
    public static  boolean isSum(int[] arr ,int sum,int cur,int i){
        if(i==arr.length){
            return sum==cur;
        }
        return isSum(arr,sum,cur,i+1)||isSum(arr,sum,arr[i]+cur,i+1);

    }

   /* public static void main(String[] args) {
      *//*  int[] ints = {2, 3, 4, 5, 7};
        System.out.println(isSum(ints,11,0,0));*//*
      printNum(1,3,9);
    }*/

    /**
     9*1=9  8*1=8  7*1=7
     9*2=18  8*2=16  7*2=14
     9*3=27  8*3=24  7*3=21
     9*4=36  8*4=32  7*4=28
     9*5=45  8*5=40  7*5=35
     9*6=54  8*6=48  7*6=42
     9*7=63  8*7=56  7*7=49
     9*8=72  8*8=64  7*8=56
     9*9=81  8*9=72  7*9=63
     6*1=6  5*1=5  4*1=4
     6*2=12  5*2=10  4*2=8
     6*3=18  5*3=15  4*3=12
     6*4=24  5*4=20  4*4=16
     6*5=30  5*5=25  4*5=20
     6*6=36  5*6=30  4*6=24
     6*7=42  5*7=35  4*7=28
     6*8=48  5*8=40  4*8=32
     6*9=54  5*9=45  4*9=36
     3*1=3  2*1=2  1*1=1
     3*2=6  2*2=4  1*2=2
     3*3=9  2*3=6  1*3=3
     3*4=12  2*4=8  1*4=4
     3*5=15  2*5=10  1*5=5
     3*6=18  2*6=12  1*6=6
     3*7=21  2*7=14  1*7=7
     3*8=24  2*8=16  1*8=8
     3*9=27  2*9=18  1*9=9
     */
    public static void main(String[] args)
    {
        /*for (int count=9;count>0;count--){
            if(count%3==0){
                echo(count);
            }
        }*/

echo1();
        System.out.println(3|2);
        System.out.println(4&3);
    }

    private static void echo1() {
        for (int s=9;s>0;){
            er(s);
            s-=3;
        }
    }

    private static void er(int s) {
        for (int e=1;e<=9;e++){
            System.out.print(s + "*" + e + "="+s*e+ "  ");
            System.out.print(s-1 + "*" + e + "="+(s-1*e)+ "  ");
            System.out.print(s-2 + "*" + e + "="+(s-1*e)+ "  ");
            System.out.println();
        }
        System.out.println();
    }

    static void echo(int row) {
        int len=1,a=row;
        for (int i = 9; i >= 1; i--) {
            for (int j = 1; j <= 3; j++) {
                System.out.print(row + "*" + len + "="+row*len+ "  ");
                row--;
            }
            len++;
            row = a;
            System.out.print("\n");
        }
    }
}
