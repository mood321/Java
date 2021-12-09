package algorithm.Interview.practice08;

/**
 * @author mood321
 * @date 2021/12/10 0:14
 * @email 371428187@qq.com
 * 【题目】
 *  1 2 3 4
 *  5 6 7 8
 *  9 10 11 12
 *  13 14 15 16
 *  打印
 *
 *  13 9 5 1
 *  14 10 6 2
 *  15 11 7 3
 *  16 12 8 4
 *
 * 【要求】
 * 　 空间复杂度(1)。
 *
 * 【基本思路】
 *
 *  思路:  一层一层的处理 1,4,16,13 应该是一组  1 占4 ,4占 16 ,16占13  依次下去  ,下层也是
 */
public class P_8_1_Rotate {
    public  void rotate(int[][] arr){
        if(arr ==null || arr.length ==0 ){
            return;
        }
        // 起点和极点
        int tR=0;
        int tC=0;
        int dR=arr.length-1;
        int dC=arr[0].length-1;

        while(tR < dR){
            rotateDege(arr ,tC++,tR++ ,dC--,dR--);
        }
    }

    private void rotateDege(int[][] arr, int tC, int tR, int dC, int dR) {
        // 多少组
        int times =dC-tC;
        int tem=0;

        for (int i = 0; i < times; i++) {    // 交换数据
            tem = arr[tR][tC+i];   // 1
            arr[tR][tC+i]=arr[dR-i][tC];  //13 到1 
            arr[dR-i][tC]=arr[dR][dC-i];
            arr[dR][dC-i]=arr[tR+i][dC];
            arr[tR+i][dC]=tem;
            
            

        }

    }

}