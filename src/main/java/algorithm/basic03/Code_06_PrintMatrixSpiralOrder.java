package algorithm.basic03;

/**
 * @Created by mood321
 * @Date 2019/10/15 0015
 * @Description 给定一个整型正方形矩阵matrix，请把该矩阵调整成顺时针旋转90度的样子。
 */
public class Code_06_PrintMatrixSpiralOrder {


    public static void spiralOrderPrint(int[][] arr) {
        int tR = 0, tC = 0;
        int dR = arr.length - 1, dC = arr.length - 1;
        while (tR <= dR ) {
            printEdge(arr, tR++, tC++, dR--, dC--);
        }
    }

    private static void printEdge(int[][] m, int tR, int tC, int dR, int dC) {
        int times = dC - tC;
        int tmp = 0;
        for (int i = 0; i != times; i++) {
            tmp = m[tR][tC + i];
            m[tR][tC + i] = m[dR - i][tC];
            m[dR - i][tC] = m[dR][dC - i];
            m[dR][dC - i] = m[tR + i][dC];
            m[tR + i][dC] = tmp;
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
        spiralOrderPrint(matrix);
        System.out.println("=========");
        printMatrix(matrix);

    }
}
