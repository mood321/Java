package algorithm.Interview.practice05;

/**
 * @author mood321
 * @date 2021/4/12 23:53
 * @email 371428187@qq.com
 */
public class P_5_11_RotateWord {


    public void rotateWord(char[] str) {
        if (str == null || str.length == 0) {
            return ;
        }

        reverse(str ,0,str.length);
        int begin=0;
        int end=0;
        while(end< str.length){
            if(str[end]!=' '){
                if(end==str.length-1){ //最后一个单词无空格
                    reverse(str,begin,end);
                }
                end++;
            } else if(str[end]==' '){ //当遇到空格时，把空格之前的单词翻转，并且把start置为end
                reverse(str,begin,end-1);
                end++;
                begin=end; //下一个单词的起始位置
            }
        }

    }

    /**
     *  题目升级
     * @param str
     */
    public void rotateWord2(char[] str,int size) {
        if (str == null || str.length == 0 || size< 0 || size> str.length) {
            return ;
        }
        reverse(str,0,size-1);
        reverse(str,size,str.length);
        reverse(str,0,str.length);

    }

    /**
     *  逆序整个字符
     * @param chars
     * @param start
     * @param end
     */
    private void reverse(char[] chars, int start, int end) {
          char temp=0;
          while(start <end){
              temp=chars[start];
              chars[start]=chars[end];
              chars[end]=temp;
          }
    }
}