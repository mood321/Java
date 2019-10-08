package algorithm.basic03;

import java.util.Stack;

/**
 * @Created by mood321
 * @Date 2019/10/9 0009
 * @Description TODO
 */
public class Code_02_GetMinStack {
    public static class MyStack1 {
        private Stack<Integer> stackData;
        private Stack<Integer> stackMin;

        public MyStack1() {
            this.stackData = new Stack<Integer>();
            this.stackMin = new Stack<Integer>();
        }

        public Integer peek() {

            return this.getMin();
        }

        public void push(Integer in) {
            if (stackMin.empty()) {
                stackMin.push(in);
            } else if (in <= getMin()) {
                stackMin.push(in);
            }
            stackData.push(in);
        }

        public Integer pop() {
            if (this.stackData == null || this.stackData.empty()) {
                throw new RuntimeException("当前栈为空");
            }
            Integer res = stackData.pop();
            if (res == getMin()) {
                stackMin.pop();
            }
            return res;
        }

        private Integer getMin() {
            if (stackMin == null || stackMin.empty()) {
                throw new RuntimeException("当前栈为空");
            }
            return stackMin.peek();
        }
    }

    public static void main(String[] args) {
        MyStack1 stack1 = new MyStack1();
        stack1.push(3);
        System.out.println(stack1.peek());
        stack1.push(4);
        System.out.println(stack1.peek());
        stack1.push(1);
        System.out.println(stack1.peek());
        System.out.println(stack1.pop());
        System.out.println(stack1.peek());
    }
}
