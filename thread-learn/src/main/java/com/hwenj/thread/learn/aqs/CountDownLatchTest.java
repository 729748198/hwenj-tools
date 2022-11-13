package com.hwenj.thread.learn.aqs;

import java.util.concurrent.CountDownLatch;

/**
 * @author hwenj
 * @since 2022/7/26
 */
public class CountDownLatchTest {
    // 同时并发执行的线程数
    public static int threadTotal = 10;

    /**
     * 加上countDownLatch后，主线程才不会中间执行。
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(threadTotal);
        for (int i = 0; i < threadTotal; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName()+"号员工下班啦");
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        System.out.println("关灯");

    }
}
