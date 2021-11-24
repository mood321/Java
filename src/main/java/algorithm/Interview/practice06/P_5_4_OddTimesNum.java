package algorithm.Interview.practice06;

/**
 * @author mood321
 * @date 2021/11/25 0:02
 * @email 371428187@qq.com
 */
public class P_5_4_OddTimesNum {

    /**
     * 思路必然是异或  , 偶数次是能异或回来的  留下一个就是奇数
     *
     * @param arr
     * @return
     */
    public int getOddTimesNum(int[] arr) {
        int t = 0;
        for (int i : arr) {
            t ^= i;
        }
        return t;
    }

    /**
     *  晋级题:  如果有两个数出现奇数次
     *   思路答题还是异或  两个数 得到具有两个数特性的 异或值,  然后在循环一次  找到其中一个   异或得到另一个
     * @param arr
     * @return
     */
    public void getOddTimesNum2(int[] arr) {
        int e = 0,eOther=0;
        for (int i : arr) {
            e ^= i;
        }
        // 拿到的是最右边的  1
        int intRight = e & (~e - 1);

        for (int cur:arr){
              if((e & intRight) != 0) {  // 因为两个奇数
                  eOther^=cur;
              }
        }
        System.out.printf(eOther +" , " + (e^eOther));
    }

    public static void main(String[] args) {
        
    }
}