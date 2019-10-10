package algorithm.basic03;

/**
 * @Created by mood321
 * @Date 2019/10/11 0011
 * @Description 给定一个整型矩阵matrix，请按照转圈的方式打印它。
 *          <p>例如： 1 2 3 4  |  5 6 7 8 |  9 10 11 12  | 13 14 15 16
 *          <p>打印结果为：1，2，3，4，8，12，16，15，14，13，9，5，6，7，11， 10
 *          <p>【要求】 额外空间复杂度为O(1)
 */
public class Code_05_RotateMatrix {


    /**
     * 主函数
     * @param matrix
     */
    public static void rotate(int[][] matrix) {
        int tR=0,tC=0;
        int dR=matrix.length-1;
        int dC=matrix.length-1;
        // 因为是顺序输入 不适合递归 适合遍历
        while (tR<=dR && tC<=dC){
            rotateEdge(matrix,tR++,tC++,dR--,dC--);
        }
    }

    public static void rotateEdge(int[][] m, int tR, int tC, int dR, int dC) {
        // 设定界限
        // 一次只输出最外层
        if(tR==dR){
            for (int i=0;i<= dC;i++){
                System.out.print(m[tR][i] +" ");
            }
        }else if (tC == dC) {
            for (int i = 0; i <= dR; i++) {
                System.out.print(m[i][tC] + " ");
            }
        } else {
            int curC = tC;
            int curR = tR;
            while (dC !=curC){
                System.out.print(m[curR][curC]+" ");
                curC++;
            }

            while (dR != curR){
                System.out.print(m[curR][curC]+" ");
                curR++;
            }
            while (tC != curC){
                System.out.print(m[curR][curC]+" ");
                curC--;
            }
            while (tR != curR){
                System.out.print(m[curR][curC]+" ");
                curR--;
            }

        }

    }

    public static void printMatrix(int[][] matrix) {
        for (int i = 0; i != matrix.length; i++) {
            for (int j = 0; j != matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int[][] matrix = { { 1, 2, 3, 4 }, { 5, 6, 7, 8 }, { 9, 10, 11, 12 },
                { 13, 14, 15, 16 } };
        printMatrix(matrix);
        System.out.println();
        rotate(matrix);


    }
}
