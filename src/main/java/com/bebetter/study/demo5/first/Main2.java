package com.bebetter.study.demo5.first;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main2 {

    static void main() {
        // 生成随机数
        Random random = new Random();
        ExpensiveComputable expensiveComputable = new ExpensiveComputable();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 500; j++) {
                    /*
                     * 对于compute方法, 因为其中不存在竞态条件且是一个纯粹的栈封闭方法.
                     * 所以在多线程并发执行时, 其是线程安全的.
                     */
                    expensiveComputable.compute(String.valueOf(random.nextInt(0, 1000)));
                }
            });
            threads.add(thread);
        }
        // 开始时间
        LocalDateTime start = LocalDateTime.now();
        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        // 结束时间
        LocalDateTime end = LocalDateTime.now();
        System.out.println("计算耗时为: " + Duration.between(start, end).toNanos() / 1_000_000_000.0);
    }

    /*
     * 10线程时: 计算耗时为: 52.772868
     * -- 可以看到Main1的执行时间几乎接近Mian2的执行时间, 但这是由于存在良好的缓存命中率
     *    如果将随机数的方法放开, 使其更少的缓存. 即使Main2不使用10线程并发, 时间也会远
     *    低于Mian存在记忆的方式
     *
     * 20线程时: 计算耗时为: 26.458337
     */
}
