package com.bebetter.study.demo7;

import java.io.IOException;
import java.io.Writer;

/**
 * 因为 System.out 是 JVM 唯一实例, 如果关闭可能导致输出丢失, 使用此类.
 * 模拟关闭写日志流, 避免丢失输出.
 */
public class NoCloseWriter extends Writer {
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        System.out.println(new String(cbuf, off, len));
    }

    @Override
    public void flush() throws IOException {
        System.out.flush();
    }

    @Override
    public void close() throws IOException {
        flush();
    }
}
