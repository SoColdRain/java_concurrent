package com.bebetter.study.demo4.first;

/**
 * this溢出导致的线程安全情况1
 * 对象还未完全初始化, 线程已经启动了
 */
public class ThisEscape1 {

    /*
     * 此处就算将 age, name 字段修改为不可变域, 也无法改变非线程安全的事实.
     * 因为只有在对象完全构造完成后, 其他线程通过正确发布的引用看到 final 域,
     * 才能保证看到正确值. 而此处的构造函数中 this 已经逸出了, 即使是不可变
     * 域也会导致其观测到错误的结果. 虽然可以看到不可变域的具体值而且是正
     * 确的(注意: 此处仅对于 Hotspot 即使 this 逸出，final 字段通常也能看
     * 到正确值，但这是 JVM 的超额实现，JLS 规范不保证), 但是对于其他的可变域,
     * 如 num. 如果它是一个长耗时的初始化且发生在子线程 start 之后. 那么此时,
     * 主线程与子线程既不构成 hb 关系, 可见性被影响. 且 num 本身也不是不可变域
     * 无法确认其在读取前就被写入了有效的值. 就会导致 num 还是可以被观测到错误的
     * 结果. 甚至因为 this 逸出而被子线程修改.
     */
    private int age;
    private String name;
    private long num;

    public ThisEscape1(int age, String name, long num) {

        /*
         * 注意此处必须通过显示的this指针来范围对象的可变域,
         * 否则JVM很可能通过编译器直接将方法入参内联到代码中,
         * 或者仅限别的激进优化导致一直读取到正确的结果(注意
         * 此处说的正确的结果并非完全正确, 只是在这个场景以及
         * 编译器JVM等的优化下很难看到期望的错误结果). 所以显
         * 式的加上this来保证JVM每次都从堆上取值. 来观测到期
         * 望的错误结果.
         */
        new Thread(() -> {
            System.out.println("age = " + this.age);
            System.out.println("name = " + this.name);
        }).start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        /*
         * 注意: 此处如果将两处域的赋值操作优先级提到最高, 子线程就存在 hb
         * 关系了, 此时虽然子线程与主线程是可见的, 而且也一定总是能输出正
         * 确的 age, name的值. 但是如果类中还存在重量级的域的初始化在子线
         * 程启动之后, 那么还是可能会导致跟此次观测到的错误的结果相同情况,
         * 即看到默认的初始值.
         */
        this.age = age;
        this.name = name;
    }
}
