package com.tencent.devops.scm.sdk.common;

import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorRequest;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponse;
import com.tencent.devops.scm.sdk.common.function.FunctionThrows;
import com.tencent.devops.scm.sdk.common.util.ScmSdkJsonFactory;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ScmApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ScmApiClient.class);
    @Getter
    private final String apiUrl;
    private final ScmConnector connector;

    public ScmApiClient(String apiUrl, ScmConnector connector) {
        if (apiUrl.endsWith("/")) {
            apiUrl = apiUrl.substring(0, apiUrl.length() - 1);
        }
        this.apiUrl = apiUrl;
        this.connector = connector;
    }

    public <T> ScmResponse<T> sendRequest(ScmRequest.Builder<?> builder, BodyHandler<T> handler) {
        return sendRequest(builder.build(), handler);
    }

    public <T> ScmResponse<T> sendRequest(ScmRequest request, BodyHandler<T> handler) {
        ScmConnectorRequest connectorRequest = null;
        ScmConnectorResponse connectorResponse = null;
        try {
            connectorRequest = prepareConnectorRequest(request);
            logRequest(connectorRequest);
            connectorResponse = connector.send(connectorRequest);
            logResponse(connectorResponse);
            afterRequest(connectorResponse, request);
            return createResponse(connectorResponse, handler);
        } catch (Exception e) {
            logger.error("Scm API request exception, method:{},url:{}", request.method(), request.url(), e);
            throw handleException(e, connectorRequest, connectorResponse);
        } finally {
            IOUtils.closeQuietly(connectorResponse);
        }
    }

    /**
     * 请求前操作,可以设置授权信息或者客户端信息
     *
     * @param originRequest 原始请求
     * @param builder       构造新的request构造器
     */
    public abstract void beforeRequest(ScmRequest originRequest, ScmRequest.Builder<?> builder);

    /**
     * 请求后操作,可以处理已知错误码
     *
     * @param connectorResponse http请求返回
     * @param request           http请求
     */
    public abstract void afterRequest(ScmConnectorResponse connectorResponse, ScmRequest request);

    /**
     * 异常处理
     */
    public abstract RuntimeException handleException(
            Exception e, ScmConnectorRequest connectorRequest, ScmConnectorResponse connectorResponse);

    /**
     * 不同代码库平台返回的json格式可能不同,所以由代码库平台自行实现json
     *
     */
    public abstract ScmSdkJsonFactory getJsonFactory();

    public void logRequest(ScmConnectorRequest request) {
        logger.info("Scm API request|method:{}|url:{}", request.method(), request.url().toString());
    }

    public void logResponse(ScmConnectorResponse response) {
        logger.info("Scm API response|code:{}|url:{}", response.statusCode(), response.request().url().toString());
    }

    private ScmConnectorRequest prepareConnectorRequest(ScmRequest request) throws IOException {
        ScmRequest.Builder<?> builder = request.toBuilder();
        beforeRequest(request, builder);
        if (request.hasBody()) {
            if (request.body() != null) {
                String contentType = request.contentType();
                if (contentType == null) {
                    contentType = "application/json";
                }
                builder.contentType(contentType);
            } else {
                builder.contentType("application/json");
                Map<String, Object> json = new HashMap<>();
                for (ScmRequest.Entry e : request.args()) {
                    json.put(e.getKey(), e.getValue());
                }
                builder.with(new ByteArrayInputStream(getJsonFactory().writeValueAsBytes(json)));
            }
        }
        return builder.build();
    }

    private <T> ScmResponse<T> createResponse(
            ScmConnectorResponse connectorResponse, BodyHandler<T> handler
    ) throws IOException {
        T body = null;
        if (handler != null) {
            body = handler.apply(connectorResponse);
        }
        return new ScmResponse<>(connectorResponse, body);
    }


    @FunctionalInterface
    public interface BodyHandler<T> extends FunctionThrows<ScmConnectorResponse, T, IOException> {
    }
}
