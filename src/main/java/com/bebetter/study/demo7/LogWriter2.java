package com.bebetter.study.demo7;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 可靠关闭的生产者 - 消费者日志服务 (建议这么做)
 */
public class LogWriter2 {

    private final BlockingQueue<String> queue;
    private final LoggerThread logger;
    private final PrintWriter writer;
    private boolean isShutdown;
    private int reservations;

    public LogWriter2(Writer writer) {
        this.writer = (PrintWriter) writer;
        this.queue = new LinkedBlockingDeque<>(5);
        this.logger = new LoggerThread();
    }

    public void log(String msg) throws InterruptedException {
        synchronized (this) {
            if (isShutdown)
                throw new IllegalArgumentException();
            ++reservations;
        }
        queue.put(msg);
    }

    public void start() {
        logger.start();
    }

    public void stop() {
        synchronized (this) {
            isShutdown = true;
        }
        logger.interrupt();
    }

    private class LoggerThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (LogWriter2.this) {
                        if (isShutdown && reservations == 0) {
                            break;
                        }
                    }
                    String msg = queue.take();
                    synchronized (LogWriter2.this) {
                        --reservations;
                    }
                    writer.println(msg);
                }
            } catch (InterruptedException e) {
            } finally {
                writer.close();
            }
        }
    }
}
