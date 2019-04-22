package offer;

/**
 * 题目描述
 * 把一个数组最开始的若干个元素搬到数组的末尾，我们称之为数组的旋转。
 * 输入一个非减排序的数组的一个旋转，输出旋转数组的最小元素。
 * 例如数组{3,4,5,1,2}为{1,2,3,4,5}的一个旋转，该数组的最小值为1。
 * NOTE：给出的所有元素都大于0，若数组大小为0，请返回0。
 */
public class 旋转数组的最小 {
    public static void main(String[] args) {
        int a[]={3,4,5,1,2};
        System.out.println(minNumberInRotateArray(a));
    }

    public static int minNumberInRotateArray(int [] array) {

        if(array.length==0){
            return 0;
        }
        int beg=0,end=array.length-1;
        while(beg<end){
            int min=beg+(end-beg)/2;
            if(array[min]>array[end]){
                beg=min+1;
            }else if(array[min]==array[end]){
                end=end-1;
            }else{
                end=min;
            }
        }
        return array[beg];
    }
}
