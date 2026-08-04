package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.ScmApiClient;
import com.tencent.devops.scm.sdk.common.ScmRequest;
import com.tencent.devops.scm.sdk.common.ScmResponse;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class GitlabPagedIterator<T> implements Iterator<T>, GitlabConstants {
    private final ScmApiClient client;
    private final Class<T> type;
    private ScmRequest nextRequest;
    private T next;

    private GitlabPagedIterator(ScmApiClient client, ScmRequest request, Class<T> type) {
        if (!"GET".equals(request.method())) {
            throw new IllegalStateException("GET is required for pagination");
        }
        this.client = client;
        this.type = type;
        this.nextRequest = request.toBuilder().set(PAGE_PARAM, DEFAULT_PAGE).build();
    }

    static <T> GitlabPagedIterator<T> create(ScmApiClient client, ScmRequest request, Class<T> type) {
        return new GitlabPagedIterator<>(client, request, type);
    }

    @Override
    public boolean hasNext() {
        fetch();
        return next != null;
    }

    @Override
    public T next() {
        fetch();
        if (next == null) {
            throw new NoSuchElementException();
        }
        T value = next;
        next = null;
        return value;
    }

    private void fetch() {
        if (next != null || nextRequest == null) {
            return;
        }
        ScmResponse<T> response = client.sendRequest(nextRequest,
                raw -> ScmResponse.parseBody(raw, type, client.getJsonFactory()));
        next = response.body();
        nextRequest = nextRequest(nextRequest, response);
    }

    private ScmRequest nextRequest(ScmRequest request, ScmResponse<T> response) {
        String rawNextPage = response.header(NEXT_PAGE_HEADER);
        String nextPage = trim(rawNextPage);
        if (nextPage != null) {
            return request.toBuilder().set(PAGE_PARAM, parsePositive(NEXT_PAGE_HEADER, nextPage)).build();
        }
        if (rawNextPage != null) {
            return null;
        }
        String current = trim(response.header(PAGE_HEADER));
        String total = trim(response.header(TOTAL_PAGES_HEADER));
        if (current != null && total != null
                && parsePositive(PAGE_HEADER, current) < parsePositive(TOTAL_PAGES_HEADER, total)) {
            return request.toBuilder().set(PAGE_PARAM, parsePositive(PAGE_HEADER, current) + 1).build();
        }
        if (current != null && total != null) {
            return null;
        }
        int perPage = requestArg(request, PER_PAGE_PARAM, DEFAULT_PER_PAGE);
        int currentPage = requestArg(request, PAGE_PARAM, DEFAULT_PAGE);
        if (response.body() != null && response.body().getClass().isArray()
                && Array.getLength(response.body()) >= perPage) {
            return request.toBuilder().set(PAGE_PARAM, currentPage + 1).build();
        }
        return null;
    }

    private int requestArg(ScmRequest request, String name, int defaultValue) {
        for (ScmRequest.Entry entry : request.args()) {
            if (name.equals(entry.getKey())) {
                return Integer.parseInt(entry.getValue().toString());
            }
        }
        return defaultValue;
    }

    private int parsePositive(String header, String value) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < 1) {
                throw new NumberFormatException();
            }
            return parsed;
        } catch (NumberFormatException error) {
            throw new GitlabApiException("Invalid " + header + " response header");
        }
    }

    private String trim(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
