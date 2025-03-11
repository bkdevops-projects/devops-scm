package com.tencent.devops.scm.sdk.common;

import lombok.Setter;

import java.util.Iterator;

/**
 * 构造客户端请求
 */
public class Requester extends ScmRequest.Builder<Requester> {
    protected final ScmApiClient client;
    @Setter
    private PagedIteratorFactory iteratorFactory;

    public Requester(ScmApiClient client) {
        this.client = client;
        this.withApiUrl(client.getApiUrl());
    }

    public void send() {
        client.sendRequest(this, ScmResponse::getBodyAsString);
    }

    public <T> T fetch(Class<T> clazz) {
        return client.sendRequest(this,
                (connectorResponse) ->
                        ScmResponse.parseBody(connectorResponse, clazz, client.getJsonFactory())
        ).body();
    }

    public <T> PagedIterable<T> toIterable(Class<T[]> type) {
        if (iteratorFactory == null) {
            throw new IllegalArgumentException("iterator factory can't be null");
        }
        Iterator<T[]> iterator = iteratorFactory.create(client, build(), type);
        return new PagedIterable<>(iterator);
    }
}
