package com.hwenj.thread.learn.concurrentTask;

import cn.hutool.core.date.DateUtil;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author hwenj
 * @since 2022/10/31
 */
public class Demo1 {


    public static void main(String[] args) throws InterruptedException {
        testCount();
        task();
    }

    private static void testCount() {
        long begin = System.currentTimeMillis();
        long temp = 0;
        // 累加30亿
        for (long i = 0; i < 3000000000L; i++) {
            temp++;
        }
        long end = System.currentTimeMillis();
        System.out.println("for循环耗时 " + (end - begin));
        System.out.println(temp);
    }

    private static void task() throws InterruptedException {
        final long timeBegin = System.currentTimeMillis();
        final CountDownLatch begin = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(3);
        long[] array = new long[3];
        for (int i = 0; i < 3; i++) {
            // 开启3个线程，每个线程累加10亿次
            int finalI = i;
            new Thread(() -> {
                try {
                    begin.await();
                    int temp = 0;
                    for (int j = 0; j < 1000000000; j++) {
                        temp++;
                    }
                    array[finalI] = temp;
                } catch (InterruptedException ignored) {
                } finally {
                    end.countDown();
                }
            }).start();
        }
        begin.countDown();
        end.await();
        long timeEnd = System.currentTimeMillis();
        System.out.println("CountDownLatch耗时 " + (timeEnd - timeBegin));
        System.out.println(array[0] + array[1] + array[2]);

    }

    private static void task2() throws InterruptedException {
        final long timeBegin = System.currentTimeMillis();
        final CountDownLatch begin = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(3);
        AtomicLong num = new AtomicLong(0);
        for (int i = 0; i < 3; i++) {
            // 开启3个线程，每个线程累加一千万次
            new Thread(() -> {
                try {
                    begin.await();
                    System.out.println(DateUtil.now());
                    long time1 = System.currentTimeMillis();
                    Thread.sleep(new Random().nextInt(1000));
                    long time2 = System.currentTimeMillis();
                    System.out.println("任务耗时 " + (time2 - time1));
                } catch (InterruptedException ignored) {
                } finally {
                    end.countDown();
                }
            }).start();
        }
        begin.countDown();
        end.await();
        long timeEnd = System.currentTimeMillis();
        System.out.println("CountDownLatch耗时 " + (timeEnd - timeBegin));
        System.out.println(num);

    }
}
