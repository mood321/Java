package offer;

public class 字符替换 {
    public static void main(String[] args) {
        StringBuffer we_are_happy = new StringBuffer("We Are Happy");
        System.out.println(replaceSpaceOne(we_are_happy));

    }

    /**
     * 运算速度慢  运算空间少
     * @param str
     * @return
     */
    public static  String replaceSpaceOne(StringBuffer str) {
        StringBuffer sf=new StringBuffer();
        for(int i=0;i<str.length();i++){
            if(str.charAt(i)==' '){
                sf.append("%20");
            }else{
                sf.append(str.charAt(i));
            }

        }
        return sf.toString();
    }

    /**
     * 运算速度快  运算空间多
     * @param str
     * @return
     */
    public static  String replaceSpace(StringBuffer str) {
        return str.toString().replaceAll(" ","%20");
    }
}
