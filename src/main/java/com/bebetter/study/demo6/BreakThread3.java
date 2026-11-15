package com.bebetter.study.demo6;

import java.util.concurrent.*;

import static com.bebetter.common.util.GeneralMethod.launderThrowable;

/**
 * 打断线程类 -- 用于特殊的打断
 */
public class BreakThread3 {

    private static ExecutorService taskExec = Executors.newFixedThreadPool(5);

    public static void timeRun(Runnable r,
                               long timeout,
                               TimeUnit unit) throws InterruptedException {
        Future<?> task = taskExec.submit(r);
        try {
            task.get(timeout, unit);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw launderThrowable(e.getCause());
        } finally {
            task.cancel(true);
        }
    }
}
