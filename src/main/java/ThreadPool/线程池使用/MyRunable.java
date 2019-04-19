package ThreadPool;

import java.util.concurrent.locks.ReentrantLock;

/**
 *synchronized 互斥锁
 */
public class MyRunable implements  Runnable {
     static  volatile Integer num=0;
    @Override
    public void run() {
        sumNum();

    }
    public synchronized void sumNum(){
        System.out.println(num);
        num++;

    }
}

/**
 * lock 锁
 */
 class MyRunable2 implements  Runnable {
    private static volatile Integer num=0;
    @Override
    public void run() {
        sumNum();

    }
    public  void sumNum(){
        ReentrantLock lock = new ReentrantLock();
        try{
            System.out.println(num);
            lock.lock();
            num++;
        }finally {
            lock.unlock();
        }


    }
}

/**
 * 每个线程独享对象
 */
 class MyRunable3 implements  Runnable {
     private static final ThreadLocal<Object> threadLocal = new ThreadLocal<Object>(){
         /**
          * ThreadLocal没有被当前线程赋值时或当前线程刚调用remove方法后调用get方法，返回此方法值
          */
         @Override
         protected Object initialValue()
         {
             System.out.println("调用get方法时，当前线程共享变量没有设置，调用initialValue获取默认值！");
             return 0;
         }
     };

     @Override
    public void run() {
        sumNum();

    }
    public  void sumNum(){
        System.out.println(threadLocal.get());

    }
}
