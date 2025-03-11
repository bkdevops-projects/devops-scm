package com.tencent.devops.scm.api.function;

/**
 * 接受三个输入参数但不返回任何结果的操作
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}
