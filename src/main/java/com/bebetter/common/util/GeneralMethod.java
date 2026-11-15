package com.bebetter.common.util;

public class GeneralMethod {

    private GeneralMethod() {}

    /**
     * 异常包装处理方法
     * @param t 原始异常
     * @return 包装异常
     */
    public static RuntimeException launderThrowable(Throwable t) {
        if (t instanceof RuntimeException)
            return (RuntimeException) t;
        else if (t instanceof Error)
            throw (Error) t;
        else
            throw new IllegalStateException("Not unchecked", t);
    }
}
