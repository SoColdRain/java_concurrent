package com.bebetter.study.demo2;

/**
 * 可见性带来的影响
 */
public class NoVisibility {

    private static int number;

    private static boolean ready;

    static void main() throws InterruptedException {

        new Thread(() -> {
            while (!ready) {
                Thread.yield();
                System.out.println(number);
            }
        }).start();
        Thread.sleep(1000);
        number = 42;
        ready = true;
    }
    /*
     * 对于此方法可能输出42, 也可能输出0, 还有可能陷入死循环
     */
}
