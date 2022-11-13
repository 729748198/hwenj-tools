package com.hwenj.thread.learn.volatiletest;

/**
 * @author hwenj
 * @since 2022/8/3
 */
public class VolatileTest {
    private volatile int num = 0;

    public void add() {
        num++;
    }

    public static void main(String[] args) {
        VolatileTest volatileTest = new VolatileTest();

    }
}
