package com.bebetter.study.demo3.third;

import com.bebetter.annotation.ThreadSafe;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 上下限范围数限制类 - 使用锁来实现线程安全
 * <p>
 * 出自《java并发编程实战》程序清单 4 - 10 (改编)
 */
@ThreadSafe
public class NumberRangeSafe {

    // 不变性条件 lower < upper
    private final AtomicInteger lower = new AtomicInteger();
    private final AtomicInteger upper = new AtomicInteger();

    public synchronized void setLower(int i) {
        // 先检查后执行 check - after -act, 典型的竞态条件
        if (i > upper.get())
            throw new IllegalArgumentException(
                    "cant set lower to " + i + "> upper"
            );
        lower.set(i);
    }

    public synchronized void setUpper(int i) {
        // 先检查后执行 check - after -act, 典型的竞态条件
        if (i < lower.get())
            throw new IllegalArgumentException(
                    "cant set lower to " + i + "< upper"
            );
        upper.set(i);
    }

    public synchronized boolean isInRange(int i) {
        return (i >= lower.get() && i <= upper.get());
    }
}
