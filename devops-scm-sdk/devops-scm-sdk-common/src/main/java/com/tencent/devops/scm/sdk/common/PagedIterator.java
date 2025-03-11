package com.tencent.devops.scm.sdk.common;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * 分页迭代器。迭代每个页面的内容项，根据需要自动请求新页面。
 * @param <T>
 */
public class PagedIterator<T> implements Iterator<T> {

    /**
     * 请求数据的迭代器
     */
    protected final Iterator<T[]> base;

    /**
     * 当前页
     */
    private T[] currentPage;

    /**
     * 下一页开始索引
     */
    private int nextItemIndex;

    public PagedIterator(Iterator<T[]> base) {
        this.base = base;
    }

    @Override
    public boolean hasNext() {
        fetch();
        return (currentPage != null && currentPage.length > nextItemIndex);
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return currentPage[nextItemIndex++];
    }

    /**
     * 获取下一页数据
     */
    private void fetch() {
        if ((currentPage == null || currentPage.length <= nextItemIndex) && base.hasNext()) {
            currentPage = Objects.requireNonNull(base.next());
            nextItemIndex = 0;
        }
    }
}
