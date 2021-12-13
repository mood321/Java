package algorithm.Interview.practice08;

/**
 * @author mood321
 * @date 2021/12/13 23:52
 * @email 371428187@qq.com
 */
public class P_8_2_PringZag {
    public static void main(String[] args) {
        int[][] arr={
                {1, 2, 3, 4} ,
                {5 ,6, 7 ,8}    ,
                {9 ,10, 11, 12} ,
                {13 ,14, 15, 16}
        }   ;
        printZag(arr);
    }
    public  static  void printZag(int[][] arr){
        if(arr ==null || arr.length ==0 ){
            return;
        }
        // 起点和极点
        int tR=0;
        int tC=0;
        int dR=0;
        int dC=0;
        int endR= arr.length -1;
        int enfC= arr[0].length -1;
        boolean fromUp= false;
        
        while( tR <= endR){
            printZag(arr,tR,tC,dR,dC,fromUp);
            // 调准下标
             tR = tC == enfC ? tR+1  :tR ;
             tC = tC == enfC ? tC :tC +1;

            //
            dR = dR == endR  ? dR :dR +1;
            dC = dR == endR  ? dC+1 : dC;
            fromUp=!fromUp;
        }
    }

    private static void printZag(int[][] arr, int tR, int tC, int dR, int dC, boolean fromUp) {
        if(fromUp){
            while (tR != dR+1){
                System.out.println( arr[tR++][tC--] + " ");
            }
        } else {
            while (dR != tR-1){
                System.out.println( arr[dR--][dC++] + " ");
            }
        }
    }
}