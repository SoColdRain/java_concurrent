package com.bebetter.study.demo3.third;

import com.bebetter.annotation.Immutable;

import java.util.concurrent.atomic.AtomicInteger;

@Immutable
public class MaxRange {

    // 不变性条件 lower < upper
    private final AtomicInteger lower;
    private final AtomicInteger upper;

    public MaxRange(int l, int u) {
        lower = new AtomicInteger(l);
        upper = new AtomicInteger(u);
    }

    public int getLower() {
        return lower.get();
    }

    public int getUpper() {
        return upper.get();
    }
}
