package com.bebetter.study.demo1.first;

/**
 * 测试线程不安全的类
 */
public class Demo1 {

    static void main() throws InterruptedException {

        Sequence sequence = new Sequence();
        // 开启线程同样执行10000次
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                sequence.getNext();
            }
        });
        // 启动线程
        thread.start();
        // 主线程执行10000次
        for (int i = 0; i < 10000; i++) {
            sequence.getNext();
        }
        // 等待所有线程结束
        thread.join();
        // 获取结果, 且一定等于或小于20000. 大部分情况下都是小于
        System.out.println(sequence.getValue());
    }
}
