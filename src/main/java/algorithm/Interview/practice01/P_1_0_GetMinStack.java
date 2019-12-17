package algorithm.Interview.practice01;

import algorithm.basic03.Code_02_GetMinStack;

import java.util.Stack;

/**
 * @author mood321
 * @date 2019/12/9 1:39
 * @email 371428187@qq.com
 */
public class P_1_0_GetMinStack {
      // code 实现思路的第二种
    public static class  MinStack{
        private Stack<Integer> stack;
        private  Stack<Integer> minStack;

        public MinStack() {
            this.stack = new Stack<>();
            this.minStack=new Stack<>();
        }

        public void push(Integer integer){
            if(stack.isEmpty()){
                minStack.push(integer);
            }else if(integer< this.getMin()){
                minStack.push(integer);
            } else {
                minStack.push(this.getMin());    //  全部代码只有这绕点
            }
            stack.push(integer);
        }

        public Integer pop(){
            if(stack.isEmpty()){
                throw  new RuntimeException("Your stack is empty");
            }
            minStack.pop();
            return  stack.pop();
        }
        public Integer peek(){
            if(stack.isEmpty()){
                throw  new RuntimeException("Your stack is empty");
            }
            return  stack.peek();
        }


          private Integer getMin() {
            if(minStack.isEmpty()){
                throw  new RuntimeException("Your stack is empty");
            }
            return  minStack.peek();
          }
      }
    public static void main(String[] args) {
        MinStack stack1 = new MinStack();
        stack1.push(3);
        System.out.println(stack1.getMin());
        stack1.push(4);
        System.out.println(stack1.getMin());
        stack1.push(1);
        System.out.println(stack1.getMin());
        System.out.println(stack1.pop());
        System.out.println(stack1.getMin());
    }
}