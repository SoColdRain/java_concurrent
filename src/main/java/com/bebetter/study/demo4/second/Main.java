package com.bebetter.study.demo4.second;

public class Main {

    static void main() {
        ThisEscape2 thisEscape2 = new ThisEscape2();
        for (int i = 1; i < 1000; i++) {
            thisEscape2.list.add(i);
        }
        System.out.println(thisEscape2.list.size());
    }
}
