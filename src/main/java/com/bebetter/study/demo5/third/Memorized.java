package com.bebetter.study.demo5.third;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 记忆实现方式3 - 非线程安全(概率较小重复计算开销)
 * <p>
 * 对于第三种实现方式, 相较于第二种实现. 它实现的优化路径为给予了线程更多的一种选择.
 * 试想: a, b线程或更多的线程. 对于第二种实现而言, 他只存在两种选择, 要么发现此结果
 * 在缓存中存在, 直接返回. 要么发现其在缓存不存在, 重新计算写入缓存. 但对于第三种实现
 * 而言, 在将FutureTask放入缓存的那一刻起, 尽管当前在计算相同的结果的线程还未完成耗时
 * 计算. 但给予后来者线程选择的机会, 如是非相同计算, 直接进行计算. 如是相同计算直接获
 * 取同一个FutureTask等待线程a完成计算(此处仅针对还未进入if块的的线程而言, 如果已经进入
 * , 那么将是跟第二种相同的情况, 同一个计算被计算了两次. 此出不存在a线程的FutureTask
 * 被覆盖导致a线程get是报错, 因为f是局部变量. 存在在栈中), 而不是自己在进行一次相同计算.
 * 但是由于跟第二种实现相同的原因, 将FutureTask放入缓存的动作并非原子操作. 也会导致同一
 * FutureTask被重复计算. 但此种概率实际上较小, 对于第三种实现而言, 其以达到了伪线程安全
 * 的境界. 拥有近乎完美的并发性, 以及正确性, 较少的资源损耗性. (仅对于简易代码模型而言,
 * 不涉及其他多重因素考量)
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
            f = ft;
            // 写入缓存
            cache.put(arg, f);
            // 进行运算
            ft.run();
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
