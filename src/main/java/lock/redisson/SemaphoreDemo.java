package lock.redisson;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * @author mood321
 * @date 2021/5/3 14:58
 * @email 371428187@qq.com
 */
public class SemaphoreDemo {
    public static void main(String[] args) throws Exception{
        final java.util.concurrent.Semaphore semp = new java.util.concurrent.Semaphore(3);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("线程1 开始执行...");
                    Thread.sleep(1000);

                    System.out.println("线程1  执行完毕...");
                    semp.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        })  .start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("线程2 开始执行...");
                    Thread.sleep(1000);
                    System.out.println("线程2 执行完毕.....");
                    semp.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        })  .start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("线程3 开始执行...");
                    Thread.sleep(1000);
                    System.out.println("线程3 执行完毕.....");
                    semp.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        })  .start();

        System.out.println("主线程 创建子线程完毕..");
        semp.acquire(1);
        System.out.println("主线程 执行完毕..");
    }
}