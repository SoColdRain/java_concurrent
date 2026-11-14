package com.bebetter.study.demo5.first;

import java.math.BigInteger;

/**
 * 耗时计算类
 */
public class ExpensiveComputable implements Computable<String, BigInteger> {

    @Override
    public BigInteger compute(String arg) {
        // 模拟耗时计算
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new BigInteger(arg);
    }
}
