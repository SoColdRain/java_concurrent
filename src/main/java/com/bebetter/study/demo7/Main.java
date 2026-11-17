package com.bebetter.study.demo7;

import java.io.PrintWriter;

public class Main {

    static void main() {
        try {
            testLogWriter1();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 测试日志服务1
     */
    public static void testLogWriter1() throws InterruptedException {
        LogWriter1 logWriter1 = new LogWriter1(new PrintWriter(new NoCloseWriter()));
        logWriter1.start();
        int j = 0;
        for (int i = 1; i <= 100; i++) {
            j++;
            if (j > 5) {
                int size = logWriter1.getQueue().size();
                logWriter1.cancel();
                System.out.println("队列中剩余任务数: " + size);
                System.out.println("还剩" + (100 - size) + "阻塞");
            }
            logWriter1.log(Integer.toString(i));
        }
    }
}
