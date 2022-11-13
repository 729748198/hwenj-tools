package com.hwenj.thread.learn.hashMap;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @author hwenj
 * @since 2022/7/25
 */
public class HashMapThread {
    // 请求总数
    public static int clientTotal = 5000;
    // 同时并发执行的线程数
    public static int threadTotal = 200;
    public static int count = 0;
  //  public static HashMap<Integer, Integer> hashMap = new HashMap<>(10000);
    public static ConcurrentHashMap<Integer, Integer> hashMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        //信号量，此处用于控制并发的线程数
        final Semaphore semaphore = new Semaphore(threadTotal);
        //闭锁，可实现计数器递减
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
        for (int i = 0; i < clientTotal; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    //执行此方法用于获取执行许可，当总计未释放的许可数不超过200时，
                    //允许通行，否则线程阻塞等待，直到获取到许可。
                    semaphore.acquire();
                    put(finalI);
                    //释放许可
                    semaphore.release();
                } catch (Exception e) {
                    //log.error("exception", e);
                    e.printStackTrace();
                }
                //闭锁减一
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();//线程阻塞，直到闭锁值为0时，阻塞才释放，继续往下执行
        executorService.shutdown();
        for (int i = 0; i < clientTotal; i++) {
            Integer integer = hashMap.get(i);
            if (integer == null || i != integer) {
                System.out.println(i + "不等于" + integer);
            }else {
               // System.out.println(i + "等于" + hashMap.get(i));
            }
        }
    }

    private static void put(Integer integer) {
        hashMap.put(integer, integer);
    }
}
