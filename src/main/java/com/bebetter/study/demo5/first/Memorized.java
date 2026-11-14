package com.bebetter.study.demo5.first;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 记忆实现方式1 - 线程安全(串行化)
 * <p>
 * 此类中直接通过方法级锁, 强制使所有线程排队, 导致其退化为串行.
 * 如果多个线程都等待正在进行计算的线程的结果, 即使它本应命中缓存
 * 也不得不排队等待计算完成. 那么这种方式甚至比无"记忆"操作的计算
 * 时间更长. --- 见Main2的计算方式.
 */
public class Memorized<A, V> implements Computable<A, V> {

    private final Map<A, V> cache = new HashMap<>();
    private final Computable<A, V> c;
    private final AtomicInteger total = new AtomicInteger();
    private final AtomicInteger shotCache = new AtomicInteger();

    public Memorized(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public synchronized V compute(A arg) {
        total.addAndGet(1);
        // 先尝试从缓存获取
        V result = cache.get(arg);
        if (result == null) {
            // 未命中缓存, 计算结果
            result = c.compute(arg);
            // 写入缓存
            cache.put(arg, result);
        } else
            shotCache.addAndGet(1);
        return result;
    }

    /**
     * 查看缓存命中率
     * <p>
     * 注意: 此方法非线程安全, 但是此处我在调用时不会在并发环境调用, 所以无需关系结果正确性
     *
     * @return 缓存命中率
     */
    public double show() {
        return (double) shotCache.get() / (double) total.get();
    }
}
