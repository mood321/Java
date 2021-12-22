package algorithm.Interview.practice08;

/**
 * @author mood321
 * @date 2021/12/21 23:51
 * @email 371428187@qq.com
 */
public class P_8_6_IsContains {

    public boolean isContains(int[][] arr, int K){
        if(arr ==null || arr.length == 0||  arr[0].length ==0){
            return false;
        }
        int row=0;
        int col =arr[0].length -1;
        while(col >-1 && row< arr.length){
            if(arr[row][col] == K){
                return true;
            }else  if(K < arr[row][col]){
                col--;
            }else  {
                row++;
            }
        }
        return false;
    }
}