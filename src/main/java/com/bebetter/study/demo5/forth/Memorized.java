package com.bebetter.study.demo5.forth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 记忆实现方式4 - 线程安全(存在可优化的缓存逾期, 缓存清理问题)
 * <p>
 * 这种实现是进化完美的版本, 它完美解决了并发安全问题. 但存在可优化的点, 此处先按下不表.
 * 它通过ConcurrentHashMap的原子操作"若没有则添加". 避免了由于写入缓存操作非原子性所
 * 带来的问题. 当多个线程尝试计算同一个结果时, 只有一个线程a可以添加成功, 使putIfAbsent
 * 操作返回null, 进行计算. 其他线程都只能拿到线程a的FutureTask阻塞等待线程a计算完毕.
 */
public class Memorized<A, V> implements Computable<A, V> {

    private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;
    private final AtomicInteger total = new AtomicInteger();
    private final AtomicInteger shotCache = new AtomicInteger();

    public Memorized(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(final A arg) {
        total.addAndGet(1);
        // 先尝试从缓存获取
        Future<V> f = cache.get(arg);
        if (f == null) {
            FutureTask<V> ft = new FutureTask<>(() -> c.compute(arg));
            // 原子的写入缓存
            f = cache.putIfAbsent(arg, ft);
            if (f == null) {
                // 缓存中不存在
                f = ft;
                ft.run();
            }
        } else
            shotCache.addAndGet(1);
        try {
            return f.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
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
