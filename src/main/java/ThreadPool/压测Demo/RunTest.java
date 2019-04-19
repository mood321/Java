package ThreadPool.压测Demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class RunTest implements Runnable {
    CountDownLatch countDownLatch;
    String url;
    public RunTest (CountDownLatch countDownLatch,String url){
        this.countDownLatch=countDownLatch;
        this.url=url;
    }

    @Override
    public void run() {

        URL url2=null;
        try {

                countDownLatch.await();

            System.out.println(11);
            url2 = new URL(this.url);
            InputStream inputStream = url2.openConnection().getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
