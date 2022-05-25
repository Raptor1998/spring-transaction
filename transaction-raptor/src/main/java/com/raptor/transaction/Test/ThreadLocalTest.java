package com.raptor.transaction.Test;

import lombok.SneakyThrows;

import java.util.UUID;

/**
 * @author raptor
 * @description ThreadLocalTest
 * @date 2022/4/28 15:13
 */
public class ThreadLocalTest {
    static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    threadLocal.set(UUID.randomUUID().toString());
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName()+" "+threadLocal.get());
                    System.out.println(Thread.currentThread().getName()+" "+threadLocal.get());
                }
            }, String.valueOf(i)).start();
        }
        Thread.sleep(5000);

        System.out.println("master");

        System.out.println("dev");

    }
}
