package algorithm.Interview.practice05;

/**
 * @author mood321
 * @date 2020/8/20 0:27
 * @email 371428187@qq.com
 */
public class P_5_3_RemoveKZeros {

    public static String removeKZeros(String str,int k){
        if(str==null  || str.length() <k){
            return str;
        }

        char[] chars = str.toCharArray();

        int start=-1;
        int count=0;
        for (int i = 0; i < chars.length; i++) {

            if(chars[i]=='0'){
                count++;
                start= start==-1?i:start;
            }else {
                if(count==k){
                    while(count-- !=0){
                        chars[start++]='@';
                    }
                }else {
                    start=-1;
                    count=0;
                }

            }

        }
        return  String.valueOf(chars).replaceAll("\\@+","");
    }
}