package com.bebetter.study.demo4.first;

public class Main {

    static void main() {
        for (int i = 0; i < 100; i++) {
            new ThisEscape1(i, "jie" + i, i);
        }
    }
}
