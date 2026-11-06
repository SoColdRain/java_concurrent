package com.bebetter.common.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用的数学计算工具类
 */
public class MathCalcUtil {

    /**
     * 私有化构造方法
     */
    private MathCalcUtil() {}

    /**
     * 简单的因数分解(试除法)
     *
     * @param number 待分解的参数
     * @return 已分解完成的质数数组
     */
    public static BigInteger[] factory(BigInteger number) {
        // 1. 边界校验
        if (number == null || number.compareTo(BigInteger.ONE) <= 0) {
            throw new IllegalArgumentException("必须是大于0的正整数: " + number);
        }

        List<BigInteger> factors = new ArrayList<>();
        BigInteger two = BigInteger.valueOf(2);

        // 2. 先把所有的 2 除干净
        while (number.mod(two).equals(BigInteger.ZERO)) {
            factors.add(two);
            number = number.divide(two);
        }

        // 3. 从 3 开始试除，只试奇数 (3, 5, 7, ...)
        BigInteger i = BigInteger.valueOf(3);
        // 这里的 i * i <= number 也可以用 i.compareTo(number.divide(i)) <= 0，后者效率更高
        while (i.multiply(i).compareTo(number) <= 0) {
            if (number.mod(i).equals(BigInteger.ZERO)) {
                factors.add(i);
                number = number.divide(i); // 除尽后 number 变小，i 不加，继续试当前 i
            } else {
                i = i.add(two);            // 除不尽，试下一个奇数
            }
        }

        // 4. 如果最后剩下的 number 大于 1，说明它自身也是一个质数
        if (number.compareTo(BigInteger.ONE) > 0) {
            factors.add(number);
        }

        // 5. 转成数组返回
        return factors.toArray(new BigInteger[0]);
    }
}
