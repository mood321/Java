package algorithm.Interview.practice04;

/**
 * @author mood321
 * @date 2020/8/5 0:46
 * @email 371428187@qq.com
 */
public class P_4_17_NQueens {
    //Solution: 定义一个全局变量sum作为最后的返回值
    //定义一个int[] cols数组用来记录已经访问过列
    //然后逐列进行查找，递归和backtrack。
    public static int sum;
    public int nQueens(int n)
    {
        sum=0;
        int[] cols=new int[n];
        //cols用来定义已访问过的列
        helper(cols,n,0);

        return sum;
    }

    private void helper(int[] cols, int n, int row){
        if(row==n){
            sum++;
            return;
        }

        for(int i=0;i<n;i++){
            if(isValid(cols,row,i))
            {
                //如果合法，那么继续做下一行
                cols[row]=i;
                helper(cols,n,row+1);
            }
        }
    }

    private boolean isValid(int[] cols, int row, int col)
    {
        //定义  isValid 来check是否合法
        for(int i=0;i<row;i++){
            if(cols[i]==col){
                return false;
            }

            if((row-i)==Math.abs(col-cols[i])){
                return false;
            }
        }
        return true;
    }

}