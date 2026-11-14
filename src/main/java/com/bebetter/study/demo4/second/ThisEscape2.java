package com.bebetter.study.demo4.second;

import java.util.ArrayList;
import java.util.List;

/**
 * this溢出导致的线程安全情况2
 * 内部类隐式持有 this 引用
 */
public class ThisEscape2 {

    public final List<Integer> list = new ArrayList<>();

    public ThisEscape2() {
        /*
         * 注意此处内部类隐式持有外部类的 this 引用, 即 set.add("1"); => ThisEscape2.this.set.add("1");
         * 所以此处可能导致隐藏的线程安全问题. 并且由于 HashSet 本身并非线程安全的容器, 当 this 逸出后, 并发的
         * 加入数据可能会导致其大小小于实际添加的数据量. 这是因为其存在哈希冲突, 导致同一个下标的值为后来的线程覆
         * 盖. 但如果是 List 容器则可能会出现, 大于或小于实际数据量的情况. 大于是因为 size++ 并非原子操作, 可
         * 能元素少于实际的 size. 也可能两个线程间的 size++ 操作丢失导致少算. 甚至可能因为指令重排等原因导致的
         * size++ 先于赋值导致元素被覆盖, 对应索引下的值为 null
         */
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                list.add(i);
            }
        });
        thread.start();
    }
}
