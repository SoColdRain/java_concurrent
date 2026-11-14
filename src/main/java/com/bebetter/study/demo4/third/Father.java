package com.bebetter.study.demo4.third;

public class Father {

    public Father() {
        /*
         * 此处 this 逸出了, 此时 ThisEscape3 还未构造完全但以可以访问.
         * 注意: 并非只有启动线程才可能导致 this 逸出. 只是此类型比较明显.
         * 包括但不限于: 1. 另一个线程在构造中启动了
         *             2. 存放于一个静态变量
         *             3. 添加到一个共享集合
         *             4. 赋值给一个外部对象的字段
         *             5. 被一个监听器列表持有 (更常见地应该是被匿名内部类持有)
         */
        new Thread(this::doSomething).start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void doSomething() {
        System.out.println("父亲调用");
    }
}
