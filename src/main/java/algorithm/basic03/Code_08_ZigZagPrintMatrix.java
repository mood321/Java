package algorithm.basic03;

/**
 * @Created by mood321
 * @Date 2019/10/23 0023
 * @Description TODO
 * 思路：双指针  指定A、B两点位置 A 向右到最右向下 B向下   到底向右
 */
public class Code_08_ZigZagPrintMatrix {
    public static void main(String[] args) {
        int[][] matrix = { { 1, 2, 3, 4 }, { 5, 6, 7, 8 }, { 9, 10, 11, 12 } };
        printMatrixZi(matrix);

    }
       public static void printMatrixZi(int[][] matri){

            // A B 两点从 下标0,0 开始
           int AR=0;
           int AC=0;
           int BR=0;
           int BC=0;
           int maxR=matri.length-1;
           int maxC=matri[0].length-1;
           // 标记从上往下  还是从下往上
           boolean upOrDown=false;

           while(AR != maxR+1){
               printLeal(matri,AR,AC,BR,BC,upOrDown);// 先打印
               AR=AC==maxC?AR+1:AR;// A 点到最右边  下移
               AC=AC==maxC?AC:AC+1;// A 点没到最右  右移
               BC=BR==maxR?BC+1:BC;// B 点没到最下  下移  注意 B先判断 是否需要下移
               BR=BR==maxR?BR:BR+1;// B 点到最下层  右移

               upOrDown=!upOrDown; // 顺序相反
           }
       }

    private static void printLeal(int[][] matri,int ar, int ac, int br, int bc, boolean upOrDown) {
           if(upOrDown){ // 当true时  从A点向 B移动
                while (ar!=br+1){
                    System.out.print(matri[ar++][ac--]+"  ");
                }
           }else { // 当false时  从B点向 A点移动
               while(br!=ar-1){
                   System.out.print(matri[br--][bc++]+"  ");
               }
           }

    }
}
