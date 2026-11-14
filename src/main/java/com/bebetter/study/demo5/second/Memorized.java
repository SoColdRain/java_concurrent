package com.bebetter.study.demo5.second;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 记忆实现方式2 - 非线程安全(重复计算开销)
 * <p>
 * 对于第二种实现方式, 仅仅将HashMap替换为线程安全的ConcurrentHashMap
 * 并且移除了方法级锁. 此时虽然实现了并行, 但存在如下的情况: 假设a, b线程
 * 同时进入了缓存未命中的逻辑, 如果计算同一个数且其开销很大. 那么就会存在获
 * 取同一个结果的多重开销问题. 其由于计算结果与写入缓存间并非原子操作. 可能
 * 存在尽管a已经计算出了结果, 但还未写入缓存. 但其他线程未知从而反复计算的情
 * 况. 所以对于这种实现, 尽管他确保了结果的正确性, 也依托ConcurrentHashMap
 * 展现出良好的并发性, 但这无异于是一种资源的浪费.
 */
public class Memorized<A, V> implements Computable<A, V> {

    private final Map<A, V> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;
    private final AtomicInteger total = new AtomicInteger();
    private final AtomicInteger shotCache = new AtomicInteger();

    public Memorized(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(A arg) {
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
