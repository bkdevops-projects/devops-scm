package com.tencent.devops.scm.sdk.common;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * 增强迭代器,支持foreach
 *
 * @param <T>
 */
public class PagedIterable<T> implements Iterable<T> {
    private final Iterator<T[]> iterator;

    public PagedIterable(Iterator<T[]> iterator) {
        this.iterator = iterator;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new PagedIterator<>(iterator);
    }
}
