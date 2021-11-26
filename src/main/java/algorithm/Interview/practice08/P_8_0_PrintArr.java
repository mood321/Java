package algorithm.Interview.practice08;

/**
 * @author mood321
 * @date 2021/11/27 0:08
 * @email 371428187@qq.com
 */
public class P_8_0_PrintArr {
    public static void main(String[] args) {
        int[][] arr={
                {1, 2, 3, 4} ,
                {5 ,6, 7 ,8}    ,
                {9 ,10, 11, 12} ,
                {13 ,14, 15, 16} 
        }   ;
           print(arr);
    }
    /**
     *  1 2 3 4
     *  5 6 7 8
     *  9 10 11 12
     *  
     * @param arr
     */
    public  static void print(int[][] arr){
        //左上角 点的坐标 ,  右下角坐标
        if(arr == null || arr[0]== null){
            return;
        }
        int cC=0;
        int cR=0;
        int eC=arr[0].length-1;
        int eR=arr.length-1;

        while(cC<=eC && cR<= eR){
            print(arr,cC++,cR++,eC--,eR--);
        }

    }

    private  static  void print(int[][] arr, int cC, int cR, int eC, int eR) {
        if(cR == eR){  // 只有一行
            for (int i = 0; i < eC; i++) {
                System.out.println(arr[cR][i]);
            }
        }else  if(cC == eC){// 只有一列  一行一列不考虑
            for (int i = 0; i < eR; i++) {
                System.out.println(arr[i][cC]);
            }

        }else {
            // 所在位置
             int curC=cC;
             int curR=cR;

             while (curC != eC){
                 System.out.println(arr[cR][curC] + " ");
                 curC++;
             }
             while(curR != eR){
                 System.out.println(arr[curR][eC] + " ");
                 curR++;
             }
            while(curC != cC){
                System.out.println(arr[eR][curC] + " ");
                curC--;
            }
            while(curR != cR){
                System.out.println(arr[curR][cC] + " ");
                curR--;
            }
        }
        
    }
}