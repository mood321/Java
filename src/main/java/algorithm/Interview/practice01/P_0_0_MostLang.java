package algorithm.Interview.practice01;

import java.util.HashMap;

/**
 * @author mood321
 * @date 2019/12/18 
 * @email 371428187@qq.com
 */
public class P_0_0_MostLang {
    public static void main(String[] args) {
        // 思路: 1 递归 实现最快
        // 2 记忆化搜索
        // 3   动态规划
       int [][] arr={{9,9,4},{6,6,8},{2,1,1}} ;
        System.out.println(getMostLang1(arr));     // 递归
        System.out.println(getMostLang2(arr));     //   
    }

    private static int  getMostLang2(int[][] arr) {
        if(arr==null || (arr.length==0 && arr[0].length==0))
            return 0;
        // 思路 hash表 有些行值是固定的 可以推出来  不用去算     
        HashMap<String, Integer> temp = new HashMap<>();
        // 边界值一定没有 先存起来  这用map的key对应 可以封对象
        for (int i = 0; i < arr.length; i++) {
            // 左右一定是0
            temp.put(i+"-"+0+"-left",0);
            temp.put(i+"-"+(arr[0].length-1)+"-right",0);
        }
        for (int i = 0; i < arr[0].length; i++) {
            // 上or下一定是0
            temp.put(0+"-"+i+"-top",0);
            temp.put((arr.length-1)+"-"+0+"-down",0);
        }
        int le=0;
        // 去缓存拿  没有就自己找
        // 把各个位置上下左右都填满 递增+1  不满足写0 四次for
        // 填的时候比较  比较 填完就能拿到最长

         // 无脑循环  
        // top
        for (int i = 1; i < arr.length; i++) {
            for (int y = 0; y < arr[0].length; y++) {
                if(arr[i][y]<arr[i-1][y]){
                    Integer len = temp.get(((i - 1) + "-" + y + "-top") )+1;
                    le=Math.max(le,len);
                    temp.put(i+"-"+y+"-top",len);
                }  else
                    temp.put(i+"-"+y+"-top",0);
            }
        }
         // left
        for (int i = 0; i < arr.length; i++) {
            for (int y = 1; y < arr[0].length; y++) {
                if(arr[i][y]<arr[i][y-1]){
                    Integer len = temp.get((i) + "-" + (y - 1) + "-top" )+1;
                    le=Math.max(le,len);
                    temp.put(i+"-"+y+"-left",len);
                }  else
                    temp.put(i+"-"+y+"-left",0);
            }
        }
        // down
        for (int length = arr.length-2; length > 0; length--) {
            for (int i = arr[0].length-1; i >=0; i--) {
               if(arr[length][i]<arr[length+1][i]){
                   Integer len = temp.get((length + 1) + "-" + i + "-top")+1;
                   le=Math.max(le,len);
                   temp.put(length+"-"+i+"-top",len);
               } else
                   temp.put(length+"-"+i+"-top",0);
            } 
        }
        // right
        for (int length = arr.length-1; length > 0; length--) {
            for (int i = arr[0].length-2; i >=0; i--) {
                if(arr[length][i]<arr[length][i+1]){
                    Integer len = temp.get(length + "-" + (i+1) + "-top")+1;
                    le=Math.max(le,len);
                    temp.put(length+"-"+i+"-top",len);
                } else
                    temp.put(length+"-"+i+"-top",0);
            }
        }
        // 找到最大
        for (int i = 0; i < arr.length; i++) {
            for (int i1 = 0; i1 < arr[0].length; i1++) {
                Integer top = temp.get((i - 1) + "-" + i1 + "-top")==null?0 :temp.get((i - 1) + "-" + i1 + "-top");
                Integer down = temp.get((i + 1) + "-" + i1 + "-down")==null?0 :temp.get((i + 1) + "-" + i1 + "-down");
                Integer left = temp.get(i  + "-" + (i1-1) + "-left")==null?0 :temp.get(i+ "-" + (i1-1) + "-left");
                Integer right = temp.get(i  + "-" + (i1+1) + "-right")==null?0 :temp.get(i+ "-" + (i1+1) + "-right");
                int len = Math.max(Math.max(top, down), Math.max(left, right)) ;
                Integer topself = temp.get(i + "-" + i1 + "-top")==null ?0 : temp.get(i + "-" + i1 + "-top");
                Integer downself = temp.get(i + "-" + i1 + "-down")==null ?0:  temp.get(i + "-" + i1 + "-down");
                Integer leftself = temp.get(i + "-" + i1 + "-left")==null ?0  :temp.get(i + "-" + i1 + "-left");
                Integer rightself = temp.get(i + "-" + i1 + "-right")==null ?0 : temp.get(i + "-" + i1 + "-right");
                int lenSelf = Math.max(Math.max(topself, downself), Math.max(leftself, rightself));
                le=Math.max(le,(len+leftself));
            }
        }
        return le;
    }



    private static int getMostLang1(int[][] arr) {
        if(arr==null || (arr.length==0 && arr[0].length==0))
            return 0;
        int le=0;
        int num=Integer.MIN_VALUE;
        for (int i = 0; i < arr.length; i++) {
            for (int i1 = 0; i1 < arr[0].length; i1++) {
               le=Math.max(rec(arr, i, i1, num, le),le);
            }
        }
            return le;
    }

    private static int rec(int[][] arr, int r, int c,int num,int l) {
        if(r<0 || c<0 || r>=arr.length || c>=arr[0].length || arr[r][c]<=num){
            return 0;
        }
        int top=rec(arr,r-1,c,arr[r][c],l);
        int down=rec(arr,r+1,c,arr[r][c],l);
        int left=rec(arr,r,c-1,arr[r][c],l);
        int right=rec(arr,r,c+1,arr[r][c],l);
        return  Math.max(Math.max(top,down),Math.max(left,right))+1  ;

    }


}