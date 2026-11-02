package com.bebetter.study.demo1.second;

import com.bebetter.annotation.ThreadSafe;

@ThreadSafe
public class Sequence {

    private int value;

    public synchronized int getNext() {
        return value++;
    }

    public int getValue() {
        return value;
    }
}
