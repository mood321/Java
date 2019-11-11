package algorithm.basic07;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author mood321
 * @date 2019/11/12 0:12
 * @email 371428187@qq.com
 */
public class Code_05_LowestLexicography {

    public  static class MyComprator implements Comparator<String>{
        @Override
        public int compare(String o1, String o2) {
            return (o1+o2).compareTo(o2+o1);
        }
    }
    public static String lowestString(String[] strs) {
        if(strs==null && strs.length==0){
            return  "";
        }
        Arrays.sort(strs,new MyComprator());
        StringBuilder sb=new StringBuilder();
        for (String str : strs) {
            sb.append(str);
        }
        return sb.toString();
    }
    public static void main(String[] args) {
        String[] strs1 = { "jibw", "ji", "jp", "bw", "jibw" };
        System.out.println(lowestString(strs1));

        String[] strs2 = { "ba", "b" };
        System.out.println(lowestString(strs2));

    }
}