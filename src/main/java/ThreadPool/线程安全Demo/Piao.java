package ThreadPool.线程安全Demo;

/**
 * Created by Aden on 2019/4/19 1:20
 */
public class Piao extends Thread {


   static Object obj = new Object();

    @Override
    public void run() {
        while (true) {
            synchronized (obj) {
                if (Demo.count > 0) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "正在卖票..." + Demo.count);
                    Demo.count--;
                }
            }
        }
    }
}
