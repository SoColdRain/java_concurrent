package com.bebetter.study.demo3.second;

import com.bebetter.annotation.Immutable;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * 因数分解结果进行缓存的不可变容器类
 * <p>
 * 出自《java并发编程实战》程序清单 3 - 12
 */
@Immutable
public class OneValueCache {

    private final BigInteger lastNumber;
    private final BigInteger[] lastFactors;

    public OneValueCache(BigInteger lastNumber, BigInteger[] lastFactors) {
        this.lastNumber = lastNumber;
        this.lastFactors =
                (lastFactors == null) ?
                        null : Arrays.copyOf(lastFactors, lastFactors.length);
    }

    public BigInteger[] getFactors(BigInteger number) {
        if (lastNumber == null || !lastNumber.equals(number))
            return null;
        else
            return Arrays.copyOf(lastFactors, lastFactors.length);
    }
}
