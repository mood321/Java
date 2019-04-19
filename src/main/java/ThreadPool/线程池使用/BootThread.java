package ThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BootThread {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(5);
        for (int i=0;i<5;i++){
            service.submit(new MyRunable3());
        }
        service.shutdown();
    }
}
