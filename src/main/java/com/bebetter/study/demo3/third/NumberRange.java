package com.bebetter.study.demo3.third;

import com.bebetter.annotation.NotThreadSafe;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 上下限范围数限制类
 * <p>
 * 出自《java并发编程实战》程序清单 4 - 10
 */
@NotThreadSafe
public class NumberRange {

    // 不变性条件 lower < upper
    private final AtomicInteger lower = new AtomicInteger();
    private final AtomicInteger upper = new AtomicInteger();

    public void setLower(int i) {
        // 先检查后执行 check - after -act, 典型的竞态条件
        if (i > upper.get())
            throw new IllegalArgumentException(
                    "cant set lower to " + i + "> upper"
            );
        lower.set(i);
    }

    public void setUpper(int i) {
        // 先检查后执行 check - after -act, 典型的竞态条件
        if (i < lower.get())
            throw new IllegalArgumentException(
                    "cant set lower to " + i + "< upper"
            );
        upper.set(i);
    }

    public boolean isInRange(int i) {
        /*
         * 注意此方法也并非线程安全的, 因为此处尝试获取了两次
         * 域的值. 这两次获得操作并非的原子的, 所以可能导致读
         * 取到结果可能不是一个有效的不变性条件, 或者违反不变
         * 性条件.
         */
        return (i >= lower.get() && i <= upper.get());
    }
}
