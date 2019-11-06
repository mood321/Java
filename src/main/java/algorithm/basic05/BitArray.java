package algorithm.basic05;

/**
 * @Created by mood321
 * @Date 2019/11/6 0006
 * @Description TODO
 */
public class BitArray {
    public static void main(String[] args) {
        int[] array = new int[1000];// 一个int 32位 可代表32个bit 一共32000

        //  给一位置复制
        int num=2132;

        int index = num / 32; //在数组内的位置
        System.out.println(index);
        int bitIndex = num % 32;//在 这个int 多少位
        System.out.println(bitIndex);
        array[index]=(array[index] | (1<< bitIndex));// 赋值

        // 检查是否赋值
        int bitValue =1 << bitIndex;
        System.out.println(Integer.toBinaryString(bitValue));
        boolean b = (bitValue & array[index]) == bitValue;
        System.out.println(b);
    }

}
