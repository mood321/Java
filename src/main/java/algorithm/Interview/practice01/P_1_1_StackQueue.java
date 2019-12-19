package algorithm.Interview.practice01;

import java.util.Stack;

/**
 * @author mood321
 * @date 2019/12/19 21:46
 * @email 371428187@qq.com
 */
public class P_1_1_StackQueue {
    public static  class StackQueue<T>{
        private Stack<T> stackPush;
        private Stack<T> stackPop;

        public StackQueue() {
            stackPush=new Stack<>();
            stackPop=new Stack<>();
        }
        public void push(T t){
            stackPush.push(t);
        }
        public T peek(){
            if(stackPush.isEmpty() && stackPop.isEmpty()){
                throw new RuntimeException("Your Queue is enpty..");
            }
            while (!stackPush.isEmpty()){
                stackPop.push(stackPush.pop());
            }
           return stackPop.peek();

        }
        public T pop (){
            if(stackPush.isEmpty() && stackPop.isEmpty()){
                throw new RuntimeException("Your Queue is enpty..");
            }
            while (!stackPush.isEmpty()){
                stackPop.push(stackPush.pop());
            }
            return stackPop.pop();
        }
        public boolean isEmtry(){
            return  stackPop.empty()&&stackPush.isEmpty();
        }
    }

    public static void main(String[] args) {
        StackQueue<Integer> queue = new StackQueue<>();
        queue.push(1);
        queue.push(2);
        queue.push(3);
         while (!queue.isEmtry()){
             System.out.println( queue.pop());;
         }
    }
}