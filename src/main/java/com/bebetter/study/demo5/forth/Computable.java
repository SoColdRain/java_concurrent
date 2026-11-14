package com.bebetter.study.demo5.forth;

/**
 * 计算接口
 * @param <A> 参数类型
 * @param <V> 返回类型
 */
public interface Computable<A, V> {

    /**
     * 计算
     * @param arg 待计算参数
     * @return 计算结果
     */
    V compute(A arg);
}
