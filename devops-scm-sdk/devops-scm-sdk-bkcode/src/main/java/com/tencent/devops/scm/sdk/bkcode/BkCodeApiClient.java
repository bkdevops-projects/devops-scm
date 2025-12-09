package com.tencent.devops.scm.sdk.bkcode;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

import com.tencent.devops.scm.sdk.common.ScmApiClient;
import com.tencent.devops.scm.sdk.common.ScmRequest;
import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorRequest;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponse;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponseErrorHandler;
import com.tencent.devops.scm.sdk.common.exception.ScmHttpRetryException;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import com.tencent.devops.scm.sdk.common.util.ScmSdkJsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP客户端实现，负责与BkCode API服务器的底层通信
 * 核心功能：
 *    - 管理API请求认证（通过HttpAuthProvider）
 *    - 处理请求前后的拦截逻辑（beforeRequest/afterRequest）
 *    - 统一异常处理（handleException）
 *    - 实现错误响应处理（通过内部类TGitConnectorResponseErrorHandler）
 */
public class BkCodeApiClient extends ScmApiClient {

    private static final Logger logger = LoggerFactory.getLogger(BkCodeApiClient.class);
    private final HttpAuthProvider authorizationProvider;

    public BkCodeApiClient(String apiUrl, ScmConnector connector, HttpAuthProvider authorizationProvider) {
        super(apiUrl, connector);
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public void beforeRequest(ScmRequest originRequest, ScmRequest.Builder<?> builder) {
        authorizationProvider.authorization(builder);
    }

    @Override
    public void afterRequest(ScmConnectorResponse connectorResponse, ScmRequest request) {
        String traceId = connectorResponse.header("X-Bkcode-Trace-Id");
        logger.info("BkCode API response|X-Bkcode-Trace-Id {}", traceId);
        ScmConnectorResponseErrorHandler errorHandler = new BkCodeConnectorResponseErrorHandler();
        if (errorHandler.isError(connectorResponse)) {
            errorHandler.onError(connectorResponse);
        }
    }

    @Override
    public ScmSdkJsonFactory getJsonFactory() {
        return ScmJsonUtil.getJsonFactory();
    }

    @Override
    public RuntimeException handleException(
            Exception e, ScmConnectorRequest connectorRequest, ScmConnectorResponse connectorResponse) {
        if (e instanceof BkCodeApiException) {
            return (BkCodeApiException) e;
        } else {
            return new BkCodeApiException(e, connectorResponse);
        }
    }

    static class BkCodeConnectorResponseErrorHandler implements ScmConnectorResponseErrorHandler {

        @Override
        public Boolean isError(ScmConnectorResponse connectorResponse) {
            return connectorResponse.statusCode() >= HTTP_BAD_REQUEST;
        }

        @Override
        public void onError(ScmConnectorResponse connectorResponse) {
            int statusCode = connectorResponse.statusCode();
            // 如果服务异常或者请求限流,则重试
            if (statusCode > HTTP_INTERNAL_ERROR || statusCode == 429) {
                throw new ScmHttpRetryException();
            } else {
                throw new BkCodeApiException(connectorResponse);
            }
        }
    }
}
