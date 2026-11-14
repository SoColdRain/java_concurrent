package com.bebetter.study.demo5.forth;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

public class Main1 {

    static void main() throws InterruptedException {
        // 生成随机数
        Random random = new Random();
        Memorized<String, BigInteger> memorized = new Memorized<>(new ExpensiveComputable());
        // 开启子线程
        Thread thread = new Thread(() -> {
            randomCount(random, memorized);
        });
        // 开始时间
        LocalDateTime start = LocalDateTime.now();
        thread.start();
        // 主线程计算
        randomCount(random, memorized);
        // 等待子线程计算完成
        thread.join();
        // 结束时间
        LocalDateTime end = LocalDateTime.now();
        System.out.println("计算耗时为: " + Duration.between(start, end).toNanos() / 1_000_000_000.0);
        System.out.println("缓存命中率为: " + (memorized.show() * 100) + "%");
    }

    private static void randomCount(Random random, Memorized<String, BigInteger> memorized) {
        for (int i = 0; i < 5000; i++) {
            int num = random.nextInt(0, 1000);
            memorized.compute(String.valueOf(num));
        }
    }

    /*
     * 计算耗时为: 26.570939
     * 缓存命中率为: 90.0%
     */
}
