package com.hwenj.thread.learn;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 这是一个简单的Runnable类，需要大约5秒钟来执行其任务。
 *
 * @author shuang.kou
 */
public class MyRunnable implements Runnable {
    // 小写的hh取得12小时，大写的HH取的是24小时
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String command;

    public MyRunnable(String s) {
        this.command = s;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Start. Time = " + df.format(new Date()));
        processCommand();
        System.out.println(Thread.currentThread().getName() + " End. Time = " +  df.format(new Date()));
    }

    private void processCommand() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return this.command;
    }
}
