package com.bebetter.study.demo6;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.bebetter.common.util.GeneralMethod.launderThrowable;

/**
 * 打断线程类 -- 用于特殊的打断
 */
public class BreakThread1 {

    private static final ScheduledExecutorService cancel = Executors.newScheduledThreadPool(5);

    public static void run(final Runnable r,
                           long timeout,
                           TimeUnit unit) throws InterruptedException {
        final Thread currentThread = Thread.currentThread();
        cancel.schedule(currentThread::interrupt, timeout, unit);
        r.run();
    }
}
