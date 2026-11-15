package com.bebetter.study.demo6;

import java.util.concurrent.*;

import static com.bebetter.common.util.GeneralMethod.launderThrowable;

/**
 * 打断线程类 -- 用于特殊的打断
 */
public class BreakThread2 {

    private static final ScheduledExecutorService cancel = Executors.newScheduledThreadPool(5);

    public static void run(final Runnable r,
                           long timeout,
                           TimeUnit unit) throws InterruptedException {
        class RethrowableTask implements Runnable {
            private volatile Throwable t;
            @Override
            public void run() {
                try {
                    r.run();
                } catch (Throwable t) {
                    this.t = t;
                }
            }

            void rethrow() {
                if (t != null)
                    throw launderThrowable(t);
            }
        }

        RethrowableTask task = new RethrowableTask();
        final Thread taskThread = new Thread(task);
        taskThread.start();
        cancel.schedule(taskThread::interrupt, timeout, unit);
        taskThread.join(unit.toMillis(timeout));
        task.rethrow();
    }
}
