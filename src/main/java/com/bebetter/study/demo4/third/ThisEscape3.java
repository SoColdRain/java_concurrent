package com.bebetter.study.demo4.third;

/**
 * this逸出的线程安全情况3
 * 子类覆写导致this逸出
 */
public class ThisEscape3 extends Father {

    /*
     * 注意此处即便 name 是不可变域, 也无法保证其能看到完整状态. 因为
     * 父类的 this 逸出导致实际上 ThisEscape3 的构造方法还未执行完成
     * 就可以被外部行为访问到了.
     */
    private final String name;

    public ThisEscape3(String name) {
        this.name = name;
    }

    @Override
    public void doSomething() {
        System.out.println("儿子调用: " + name);
    }
}

