package com.bebetter.study.demo3.third;

import com.bebetter.annotation.ThreadSafe;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 上下限范围数限制类 - 使用不可变类来实现并发安全
 * <p>
 * 出自《java并发编程实战》程序清单 4 - 10 (改编)
 */
@ThreadSafe
public class NumberRangeImmutable {

    // 不变性条件 lower < upper
    private volatile MaxRange maxRange = new MaxRange(0, 0);

    public synchronized void setLower(int i) {
        // 先检查后执行 check - after -act, 典型的竞态条件
        if (i > maxRange.getUpper())
            throw new IllegalArgumentException(
                    "cant set lower to " + i + "> upper"
            );
        maxRange = new MaxRange(maxRange.getLower(), i);
    }

    public synchronized void setUpper(int i) {
        // 先检查后执行 check - after -act, 典型的竞态条件
        if (i < maxRange.getLower())
            throw new IllegalArgumentException(
                    "cant set lower to " + i + "< upper"
            );
        maxRange = new MaxRange(i, maxRange.getUpper());
    }

    public boolean isInRange(int i) {
        /*
         * 此处为快照读, 避免连续两次访问期间, maxRange被其他线程替换为
         * 新的范围. 导致两次操作读取到非法范围, 或者无效的范围. 但此类读
         * 法并不能确保每次读到最新的范围. 真难搞确保读取到的结果是正确性,
         * 但不保证每次读取都是最新的结果.
         * 如不采用快照读的方式, 可以为此方法直接加锁, 确保每次都能获取到
         * 最新的结果.
         */
        MaxRange snapshot = maxRange;
        return (i >= snapshot.getLower() && i <= snapshot.getUpper());
    }
}
