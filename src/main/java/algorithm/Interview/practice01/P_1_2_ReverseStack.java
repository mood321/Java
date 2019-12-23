package algorithm.Interview.practice01;

import java.util.Stack;

/**
 * @author mood321
 * @date 2019/12/23 21:14
 * @email 371428187@qq.com
 * @desc 利用递归逆序 栈
 */
public class P_1_2_ReverseStack {

    /**
     *  分两个递归  1 个取出栈底 然后把其他数放回去   1 个把取出来的数放在栈里
     *
     */

    public  static  Integer getAndRemoveLast(Stack<Integer> stack){
        Integer result = stack.pop();
        if(stack.isEmpty()){
            return  result;      // 走到这儿  栈就已经完了   取一个值只会来一次
        }    else {
            Integer last = getAndRemoveLast(stack);       //  这是正式递归
            stack.push(result);//   这儿是已经拿到最底  把其他值还原回去
            return  last;
        }
    }

    public  static void reverse(Stack<Integer> stack){
            if(stack.isEmpty()){    // 递归 basse case
                return;
            }
        Integer pop = getAndRemoveLast(stack); // 取出最底的值 准备逆序
        reverse(stack);// 正式递归  利用 递归栈记录中间数据
        stack.push(pop); // 复用原栈  因为原来的被递归去程空的了

    }

    public static void main(String[] args) {
        Stack<Integer> s = new Stack<>();
        s.push(1);
        s.push(2);
        s.push(3);
        reverse(s);
        while (!s.isEmpty())
            System.out.println(s.pop());
    }
}