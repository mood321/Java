package algorithm.Interview.practice01;

import java.util.Stack;

/**
 * @author mood321
 * @date 2019/12/24 22:40
 * @email 371428187@qq.com
 */
public class P_1_4_StackSort {
    // 利用 一个栈给一个栈排序

    public static  void sort(Stack<Integer> stack){
        Stack<Integer> help = new Stack<>();
        while (!stack.isEmpty()){
            Integer cur = stack.pop();
             while (  !help.isEmpty() && cur>help.peek()){
                 stack.push(help.pop());
             }
            help.push(cur);

        }
         while(!help.isEmpty()){
             stack.push(help.pop());
         }
    }


    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>();
        stack.push(1);
        stack.push(3);
        stack.push(2);
        sort(stack);

        while (!stack.isEmpty()){
            System.out.println(stack.pop());
        }
    }
}