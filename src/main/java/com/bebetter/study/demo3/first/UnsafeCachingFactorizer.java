package com.bebetter.study.demo3.first;

import com.bebetter.annotation.NotThreadSafe;
import com.bebetter.common.util.MathCalcUtil;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 非线程安全的带缓存的因数分解类
 * <p>
 * 出自《java并发编程实战》程序清单 2-5
 */
@NotThreadSafe
public class UnsafeCachingFactorizer {

    private final AtomicReference<BigInteger> lastNumber = new AtomicReference<>();
    private final AtomicReference<BigInteger[]> lastFactors = new AtomicReference<>();

    public BigInteger[] service(BigInteger number) {
        /*
         * 此处也是一个经典的竞态条件, "先检查后执行"
         */
        if (number.equals(lastNumber.get())) {
            return lastFactors.get();
        }
        else {
            BigInteger[] factors = MathCalcUtil.factory(number);
            /*
             * 注意此处下方的两行set行为的代码, 虽然单论而言其是原子操作, 但其单独设置时
             * 这两步操作是相互独立的, 但对于这两个共享变量而言, 我们仍无法同时更新这两个
             * 变量, 它们存在互相依赖的关系,如果其中一个被更新吗那么相应的另一个也应该被更
             * 新, 因此这两步组合在一起并不原子.
             */
            lastNumber.set(number);
            lastFactors.set(factors);
            return factors;
        }
    }
}
