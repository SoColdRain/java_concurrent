package com.bebetter.study.demo3.second;

import com.bebetter.annotation.ThreadSafe;
import com.bebetter.common.util.MathCalcUtil;

import java.math.BigInteger;

/**
 * 使用不可变容器的volatile类型引用已缓存最新的结果
 * <p>
 * 出自《java并发编程实战》程序清单 3 - 13
 */
@ThreadSafe
public class VolatileCachedFactorizer {

    private volatile OneValueCache cache =
            new OneValueCache(null, null);

    public BigInteger[] service(BigInteger integer) {
        BigInteger[] factors = cache.getFactors(integer);
        if (factors == null) {
            factors = MathCalcUtil.factory(integer);
        }
        return factors;
    }
}
