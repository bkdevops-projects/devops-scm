package com.tencent.devops.scm.api.function;

/**
 * 接受两个输入参数但不返回任何结果的操作
 */
@FunctionalInterface
public interface PairConsumer<T, V> {
    void accept(T t, V v);
}
