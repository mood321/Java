package ThreadPool.Test;

/**
 * Created by Aden on 2019/4/19 1:26
 */
public class Demo {
static  volatile    int count  = 100;
    public static void main(String[] args) {

        Piao p1 = new Piao();
        Piao p2 = new Piao();
        Piao p3 = new Piao();
        p1.start();
        p2.start();
        p3.start();
    }
}
