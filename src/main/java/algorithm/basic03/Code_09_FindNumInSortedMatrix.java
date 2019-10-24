package algorithm.basic03;

/**
 * @Created by mood321
 * @Date 2019/10/24 0024
 * @Description TODO
 */
public class Code_09_FindNumInSortedMatrix {


    public static void main(String[] args) {
        int[][] matrix = new int[][] { { 0, 1, 2, 3, 4, 5, 6 },// 0
                { 10, 12, 13, 15, 16, 17, 18 },// 1
                { 23, 24, 25, 26, 27, 28, 29 },// 2
                { 44, 45, 46, 47, 48, 49, 50 },// 3
                { 65, 66, 67, 68, 69, 70, 71 },// 4
                { 96, 97, 98, 99, 100, 111, 122 },// 5
                { 166, 176, 186, 187, 190, 195, 200 },// 6
                { 233, 243, 321, 341, 356, 370, 380 } // 7
        };
        int K = 288;
        System.out.println(isContains(matrix, K));
    }

    private static boolean isContains(int[][] matrix, int k) {
        if(matrix==null || matrix.length<1){
            return false;
        }
        int c=0;
        int r=matrix.length-1;
        // 以左下 为起点
        while (r>=0 && c<matrix[0].length){
            if (matrix[r][c]==k) {
                return true;
            }else if(matrix[r][c]>k){ // 向上走
                r--;
            }else if(matrix[r][c]<k){ //向右走
                c++;
            }
        }
        return  false;//没找到

    }
}
