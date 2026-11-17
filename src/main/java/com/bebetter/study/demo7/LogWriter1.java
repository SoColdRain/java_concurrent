package com.bebetter.study.demo7;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 不支持关闭的生产者 - 消费者日志服务 (不完美的设计)
 */
public class LogWriter1 {

    private final BlockingQueue<String> queue;
    private final LoggerThread logger;

    public LogWriter1(Writer writer) {
        this.queue = new LinkedBlockingDeque<>(5);
        this.logger = new LoggerThread(writer);
    }

    public void start() {
        logger.start();
    }

    public void log(String msg) throws InterruptedException {
        queue.put(msg);
    }

    public synchronized BlockingQueue<String> getQueue() {
        return new LinkedBlockingDeque<>(queue);
    }

    public void cancel() {
        logger.interrupt();
    }

    private class LoggerThread extends Thread {

        private final PrintWriter writer;

        public LoggerThread(Writer writer) {
            this.writer = (PrintWriter) writer;
        }

        /*
         * 尽管此处 queue 方法可以正常响应中断让消费者线程退出. 并将不可响应中断的输入流
         * 也通过关闭的方式强制使其响应中断. 但是如果仅仅只是使日志线程退出, 那么它还不是
         * 一种完善的关闭机制. 这会让所有已在队列中的消息失去被消费的机会, 相当于丢弃了这
         * 些任务. 如果当队列处于溢出状态时, 还可能会导致其生产者线程一直阻塞导致JVM无法
         * 退出. 所以在取消一个生产者 - 消费者操作时, 需要将二者同时取消. 但由于这个例子
         * 中的生产者并非是一个独立的线程, 因此想要取消它无疑是困难的.
         */
        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(100);
                    writer.println("log: " + queue.take());
                }
            } catch (InterruptedException e) {
                // 1. 此处发生打断, 退出循环
                System.out.println("消费者被打断");
            } finally {
                System.out.println("消费者已关闭");
                writer.close(); // 2. 直接关闭消费者
            }
        }
    }
}
