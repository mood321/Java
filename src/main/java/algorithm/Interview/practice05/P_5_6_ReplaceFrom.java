package algorithm.Interview.practice05;

/**
 * @author mood321
 * @date 2020/9/4 0:14
 * @email 371428187@qq.com
 */
public class P_5_6_ReplaceFrom {
    //替换字符串中连续出现的指定字符串
    public static void clear(char[] chas,int end,int len){
        //把找到的字串赋0
        while(len--!=0){
            chas[end--]=0;
        }
    }

    public static String replace(String s,String from,String to){
        if(s==null||from==null||s.equals("")||from.equals("")){
            return s;
        }
        char[] chas=s.toCharArray();
        char[] chaf=from.toCharArray();
        int match=0;
        for(int i=0;i<chas.length;i++){
            if(chas[i]==chaf[match++]){//如果第一个字符相同，依次看后面的字符
                if(match==chaf.length){//指导相同的字符数等于from的长度
                    clear(chas,i,match);//将原字串中的from清零
                    match=0;//重新开始寻找
                }
            }
            else{//没有完全相同，即当前字符不匹配from
                if(chas[i]==chaf[0]){//判断当前字符字符是否同from首字符
                    i--;//是的话，从当前位置开始重新匹配from
                }
                match=0;
            }
        }

        String res="";
        String cur="";
        for(int i=0;i<chas.length;i++){
            if(chas[i]!=0){//如果不是from字符
                cur+=String.valueOf(chas[i]);//累加到cur
            }
            if(chas[i]==0&&(i==0||chas[i-1]!=0)){//001200，两种情况
                res=res+cur+to;
                cur="";//看后面还有没有不为from的字符
            }
        }
        if(!cur.equals("")){//没找到from，只有存到cur的字符
            res=res+cur;
        }
        return res;
    }
}