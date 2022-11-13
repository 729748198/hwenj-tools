package com.hwenj.thread.learn.atomic;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hwenj
 * @since 2022/7/25
 */
public class ThreadPoolExecutorAtomic {

    private static final int CORE_POOL_SIZE = 100;
    private static final int MAX_POOL_SIZE = 1000;
    private static final int QUEUE_CAPACITY = 1000;
    private static final Long KEEP_ALIVE_TIME = 1L;
    private AtomicInteger countAtomic = new AtomicInteger(0);
    private volatile int countInt = 0;

    public AtomicInteger getCountAtomic() {
        return countAtomic;
    }

    public void setCountAtomic(AtomicInteger countAtomic) {
        this.countAtomic = countAtomic;
    }

    public int getCountInt() {
        return countInt;
    }

    public  void incrInt() {
        countInt++;
    }

    public void setCountInt(int countInt) {
        this.countInt = countInt;
    }

    public static void main(String[] args) {

        //使用阿里巴巴推荐的创建线程池的方式
        //通过ThreadPoolExecutor构造函数自定义参数创建
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());
        final CountDownLatch countDownLatch = new CountDownLatch(100);
        ThreadPoolExecutorAtomic atomic = new ThreadPoolExecutorAtomic();
        for (int i = 0; i < 100; i++) {
            //执行Runnable
            executor.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " 到达战场");
                try {
                    countDownLatch.countDown();
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " 开始工作");
//                atomic.setCountInt(atomic.getCountInt() + 1);
//                int count = atomic.getCountInt();
//                count++;
//                System.out.println(Thread.currentThread().getName() + ": 增加后" + count);
                atomic.incrInt();
                System.out.println(Thread.currentThread().getName() + ": 增加后" + atomic.getCountInt());
            });
        }
        //终止线程池
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println(atomic.getCountInt());
    }
}
