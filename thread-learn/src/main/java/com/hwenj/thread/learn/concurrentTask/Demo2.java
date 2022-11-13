package com.hwenj.thread.learn.concurrentTask;

import lombok.val;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * @author hwenj
 * @since 2022/11/1
 */
public class Demo2 {

    private static Map<Integer, Function<String, String>> map = new HashMap<>();
    final static CyclicBarrier begin = new CyclicBarrier(3);

    static {
        map.put(1, Demo2::getA);
        map.put(2, Demo2::getB);
        map.put(3, Demo2::getC);
    }

    public static void main(String[] args) throws InterruptedException {
        task2();
    }

    private static void task() throws InterruptedException {
        final long timeBegin = System.currentTimeMillis();
        final CountDownLatch end = new CountDownLatch(3);
        Set<String> result = new HashSet<>();
        for (int i = 1; i <= 3; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    begin.await();
                    result.addAll(getSet(finalI, String.valueOf(finalI)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        System.out.println(result);
    }

    private static void task2() throws InterruptedException {
        final long timeBegin = System.currentTimeMillis();
        final CountDownLatch begin = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(3);
        Set<String> result = new HashSet<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        for (int i = 1; i <= 3; i++) {
            int finalI = i;
            threadPool.execute(() -> {
                try {
//                    begin.await();
                    final val strings = getSet(finalI, String.valueOf(finalI));
                    System.out.println("strings " + strings);
                    result.addAll(strings);
                    System.out.println("result" + result);
                } catch (Exception e) {

                } finally {
                    end.countDown();
                }
            });
        }
//        begin.countDown();
        end.await();
        threadPool.shutdown();
        System.out.println(result);
    }

    private static Set<String> getSet(Integer i, String s) throws InterruptedException {
        String result = map.get(i).apply(s);
        HashSet<String> set = new HashSet<>();
        set.add(result);
        System.out.println("线程 " + i);
        return set;
    }


    private static String getA(String a) {
        return "A" + a;
    }


    private static String getB(String b) {
        return "B" + b;
    }


    private static String getC(String c) {
        return "C" + c;
    }

}
