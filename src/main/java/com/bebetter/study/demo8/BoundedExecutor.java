package com.bebetter.study.demo8;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * 如果想使线程在资源耗尽时, 并非直接拒绝新任务, 而是使提交任务时阻塞.
 * 可以使信号量大小等同于最大线程数 + 队列最大任务数, 且使用有界队列.
 * 这样即可以确保任务不会丢失拒绝, 还限制任务的到达率. 使用有界队列以防
 * 止内存溢出.
 */
public class BoundedExecutor {

    private final Executor executor;
    private final Semaphore semaphore;

    public BoundedExecutor(Executor exec, int bound) {
        executor = exec;
        semaphore = new Semaphore(bound);
    }

    public void submitTask(Runnable task) throws InterruptedException {
        // 信号量阻塞
        semaphore.acquire();
        try {
            executor.execute(() -> {
                try {
                    task.run();
                } finally {
                    // 执行完毕释放信号
                    semaphore.release();
                }
            });
        } catch (RejectedExecutionException e) {
            // 线程池拒绝释放信号量
            semaphore.release();
        }
    }
}
