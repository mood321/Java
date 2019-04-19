package ThreadPool.压测Demo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    static  final  int MAX_NUM=300;
    static final  String  URL="http://localhost:8083/index.do";
    static final  String  URL2="http://localhost:8081/";

    public static void main(String[] args) {

        init();
    }

    private static void init() {
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_NUM);
        CountDownLatch countDownLatch = new CountDownLatch(MAX_NUM);
        for (int i = 0; i < MAX_NUM; i++) {

            executorService.execute(new RunTest(countDownLatch,URL2));
            countDownLatch.countDown();
        }

            System.out.println(12);

            executorService.shutdown();

    }

}
