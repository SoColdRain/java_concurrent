package com.bebetter.study.demo1.first;

import com.bebetter.annotation.NotThreadSafe;

@NotThreadSafe
public class Sequence {

    private int value;

    public int getNext() {
        return value++;
    }

    public int getValue() {
        return value;
    }
}
