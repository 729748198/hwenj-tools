package com.hwenj.thread.learn.atomic;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

/**
 * @author hwenj
 * @since 2022/7/25
 */
public class CyclicBarrierAtomic {

    public static final int currency = 5000;

    // 同时并发执行的线程数
    public static int threadTotal = 200;

    public static int count = 0;

    public static void main(String[] args) throws InterruptedException {
        //currency:线程数
//创建一个CyclicBarrier对象，下面调用他的方法，让线程等待，直到所有线程开启后一起执行方法，从而造成高并发。
        CyclicBarrier cb = new CyclicBarrier(currency);
        //信号量，此处用于控制并发的线程数
        final Semaphore semaphore = new Semaphore(threadTotal);
        for (int i = 0; i < currency; i++) {
            //开启线程后执行的run（）方法
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "------我准备好了------");
                //等待一起出发
                try {
                    //CyckicBarrier等待一起出发
                    cb.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println("开始干活");
                //调用业务
                //干什么活
                add();
            }).start();//开启线程
        }
        System.out.println(count);


    }

    public static void add() {
        count++;
    }
}
