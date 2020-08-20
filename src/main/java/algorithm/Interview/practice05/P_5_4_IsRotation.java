package algorithm.Interview.practice05;

/**
 * @author mood321
 * @date 2020/8/21 0:26
 * @email 371428187@qq.com
 */
public class P_5_4_IsRotation {

    public  boolean isRotation(String s1,String s2){
        if(s1==null || s2==null || s1.length()!= s2.length()){
            return false;
        }
        String s = s1 + s1;
        return s.contains(s2);// java 的 contains 是蛮力算法，时间复杂度为O(M*N)。 可以尝试 KMP O(M+N)
    }
}