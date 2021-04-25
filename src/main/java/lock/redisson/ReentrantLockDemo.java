package lock.redisson;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author mood321
 * @date 2021/4/25 23:37
 * @email 371428187@qq.com
 * @Des ReentrantLock demo 方便看源码
 */
public class ReentrantLockDemo {
    public static void main(String[] args) {
        ReentrantLock reentrantLock = new ReentrantLock();

        reentrantLock.lock();
        reentrantLock.unlock();
    }
}