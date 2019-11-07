package algorithm.basic05;

/**
 * @Created by mood321
 * @Date 2019/11/7 0007
 * @Description TODO
 */
public class Code_03_Islands {

    public static void main(String[] args) {
        int[][] m1 = {{0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0, 1, 1, 1, 0},
                {0, 1, 1, 1, 0, 0, 0, 1, 0},
                {0, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 1, 0, 0},
                {0, 0, 0, 0, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},};
        System.out.println(countIslands(m1));

        int[][] m2 = {{0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 0, 0, 0, 1, 0},
                {0, 1, 1, 0, 0, 0, 1, 1, 0},
                {0, 0, 0, 0, 0, 1, 1, 0, 0},
                {0, 0, 0, 0, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},};
        System.out.println(countIslands(m2));

    }

    private static int countIslands(int[][] m1) {
        if (m1.length < 1 || m1[0].length < 0) {
            return 0;
        }
        int n = 0;
        for (int i = 0; i < m1.length; i++) {
            for (int i1 = 0; i1 < m1[0].length; i1++) {
                if (m1[i][i1] == 1) {
                    n++;
                    infect(m1, i, i1, m1.length, m1[0].length);
                }
            }
        }
return n;
    }

    public static void infect(int[][] m, int i, int j, int N, int M) {
        if(m[i][j]!=1 || j<0|| i<0|| i>=N || j>=M){
            return;
        }
        m[i][j]=2;
        infect(m,i-1,j,N,M);
        infect(m,i,j-1,N,M);
        infect(m,i+1,j,N,M);
        infect(m,i,j+1,N,M);
    }
}
