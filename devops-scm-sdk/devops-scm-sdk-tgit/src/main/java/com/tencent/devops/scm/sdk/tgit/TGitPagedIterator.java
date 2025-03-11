package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.ScmApiClient;
import com.tencent.devops.scm.sdk.common.ScmRequest;
import com.tencent.devops.scm.sdk.common.ScmResponse;

import java.net.URL;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class TGitPagedIterator<T> implements Iterator<T>, TGitConstants {

    private final ScmApiClient client;
    private final Class<T> type;
    private T next;
    private ScmRequest nextRequest;

    private TGitPagedIterator(ScmApiClient client, Class<T> type, ScmRequest request) {
        if (!"GET".equals(request.method())) {
            throw new IllegalStateException("Request method \"GET\" is required for page iterator.");
        }
        this.client = client;
        this.type = type;
        this.nextRequest = request;
    }

    static <T> TGitPagedIterator<T> create(ScmApiClient client, ScmRequest request, Class<T> type) {
        request = request.toBuilder().set(PAGE_PARAM, 1).build();
        return new TGitPagedIterator<>(client, type, request);
    }

    @Override
    public boolean hasNext() {
        fetch();
        return next != null;
    }

    @Override
    public T next() {
        fetch();
        T result = next;
        if (result == null) {
            throw new NoSuchElementException();
        }
        next = null;
        return result;
    }

    private void fetch() {
        if (next != null) {
            return; // already fetched
        }
        if (nextRequest == null) {
            return; // no more data to fetch
        }

        URL url = nextRequest.url();
        try {
            ScmResponse<T> nextResponse = client.sendRequest(nextRequest,
                    (connectorResponse) -> ScmResponse.parseBody(connectorResponse, type, client.getJsonFactory()));
            assert nextResponse.body() != null;
            next = nextResponse.body();
            nextRequest = parseNextRequest(nextRequest, nextResponse);
        } catch (Exception e) {
            if (e instanceof TGitApiException) {
                throw e;
            }
            throw new TGitApiException("Failed to retrieve " + url, e);
        }
    }

    private ScmRequest parseNextRequest(ScmRequest nextRequest, ScmResponse<T> nextResponse) {
        ScmRequest result = null;
        int currentPage = getIntHeaderValue(nextResponse, PAGE_HEADER);
        int totalPages = getIntHeaderValue(nextResponse, TOTAL_PAGES_HEADER);
        if (currentPage < totalPages) {
            result = nextRequest.toBuilder().set(PAGE_PARAM, currentPage + 1).build();
        }
        return result;
    }

    private int getIntHeaderValue(ScmResponse<T> response, String key) {
        String value = getHeaderValue(response, key);
        if (value == null) {
            return -1;
        }
        try {
            return (Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            throw new TGitApiException("Invalid '" + key + "' header value (" + value + ") from server");
        }
    }

    private String getHeaderValue(ScmResponse<T> response, String key) {

        String value = response.header(key);
        value = (value != null ? value.trim() : null);
        if (value == null || value.isEmpty()) {
            return null;
        }
        return (value);
    }
}
