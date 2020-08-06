package algorithm.Interview.practice05;

/**
 * @author mood321
 * @date 2020/8/7 0:34
 * @email 371428187@qq.com
 */
public class P_5_1_IsDeformation {

    /**
     * 如果字符的类型很多，可以采用哈希表代替长度为256的整型数组，
     * 字符串的种类为 M，字符串的长度为 N，那么 时间复杂度是 O(N), 空间复杂度是O(M)
     * @param str1
     * @param str2
     * @return
     */
    public boolean isDeformation(String str1, String str2){

        if (str1 == null || str2 == null || str1.length() != str2.length()){
            return false;
        }

        char[] chars1 = str1.toCharArray();
        char[] chars2 = str2.toCharArray();
        int[] map = new int[256];

        for (int i = 0; i < chars1.length; i++) {
            map[chars1[i]]++;
        }

        for (int i = 0; i < chars2.length; i++) {
            if (map[chars2[i]]-- == 0){
                return false;
            }
        }
        return true;
    }
}