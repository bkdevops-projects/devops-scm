package com.tencent.devops.scm.sdk.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponse;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import java.io.IOException;
import lombok.Setter;

import java.util.Iterator;

/**
 * 构造客户端请求
 */
public class Requester extends ScmRequest.Builder<Requester> {

    protected final ScmApiClient client;
    @Setter
    private PagedIteratorFactory iteratorFactory;
    private Class<? extends ResponseResult> responseResultCls;

    public Requester(ScmApiClient client) {
        this.client = client;
        this.withApiUrl(client.getApiUrl());
    }

    public void send() {
        client.sendRequest(this, ScmResponse::getBodyAsString);
    }

    public <T> T fetch(Class<T> clazz) {
        return client.sendRequest(this,
                (connectorResponse) -> {
                    String resultData = fetchResultData(connectorResponse);
                    return ScmResponse.parseBody(resultData, clazz, client.getJsonFactory());
                }
        ).body();
    }

    public <T> T fetch(TypeReference<T> typeReference) {
        return client.sendRequest(this,
                (connectorResponse) -> {
                    String resultData = fetchResultData(connectorResponse);
                    return ScmResponse.parseBody(resultData, typeReference, client.getJsonFactory());
                }
        ).body();
    }

    public String fetch() {
        return client.sendRequest(this, this::fetchResultData).body();
    }

    public <T extends ResponseResult> void withResult(Class<T> responseResultCls) {
        this.responseResultCls = responseResultCls;
    }

    private String fetchResultData(ScmConnectorResponse connectorResponse) throws IOException {
        String data = null;
        // 提取响应结果
        if (responseResultCls != null) {
            data = ScmJsonUtil.getJsonFactory().toJson(
                    ScmResponse.parseBody(
                            connectorResponse,
                            responseResultCls,
                            client.getJsonFactory()
                    ).getData()
            );
        } else {
            data = ScmResponse.getBodyAsString(connectorResponse);
        }
        return data;
    }

    public <T> PagedIterable<T> toIterable(Class<T[]> type) {
        if (iteratorFactory == null) {
            throw new IllegalArgumentException("iterator factory can't be null");
        }
        Iterator<T[]> iterator = iteratorFactory.create(client, build(), type);
        return new PagedIterable<>(iterator);
    }
}
