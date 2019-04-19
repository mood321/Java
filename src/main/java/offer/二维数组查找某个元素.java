package offer;

public class 二维数组查找某个元素 {
    public static void main(String[] args) {
        int[][] arr={{1,2,8,9},{2,4,9,12},{4,7,10,13},{6,8,11,15}};
        System.out.println(Find(16,arr)); ;
    }
    public static boolean Find(int target, int [][] array) {
        int i=0;
        int len=array.length-1;
        while((len>0)&&(i<array[0].length)){
            if(target>array[len][i]){
                i++;
            }else if(target<array[len][i]){
                len--;
            }else{
                System.out.println(i+"   "+len);
                return true;
            }
        }
        return false;
    }
}
