package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.ScmApiClient;
import com.tencent.devops.scm.sdk.common.ScmRequest;
import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorRequest;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponse;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponseErrorHandler;
import com.tencent.devops.scm.sdk.common.exception.ScmHttpRetryException;
import com.tencent.devops.scm.sdk.common.util.ScmSdkJsonFactory;
import com.tencent.devops.scm.sdk.tgit.util.TGitJsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class TGitApiClient extends ScmApiClient {

    private static final Logger logger = LoggerFactory.getLogger(TGitApiClient.class);
    private final HttpAuthProvider authorizationProvider;

    public TGitApiClient(String apiUrl, ScmConnector connector, HttpAuthProvider authorizationProvider) {
        super(apiUrl, connector);
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public void beforeRequest(ScmRequest originRequest, ScmRequest.Builder<?> builder) {
        // 获取会话不需要读取授权
        if ("/session".equals(originRequest.urlPath())) {
            return;
        }
        authorizationProvider.authorization(builder);
    }

    @Override
    public void afterRequest(ScmConnectorResponse connectorResponse, ScmRequest request) {
        String traceId = connectorResponse.header("trace_id");
        logger.info("TGit API response|traceId:{}", traceId);
        ScmConnectorResponseErrorHandler errorHandler = new TGitConnectorResponseErrorHandler();
        if (errorHandler.isError(connectorResponse)) {
            errorHandler.onError(connectorResponse);
        }
    }

    @Override
    public ScmSdkJsonFactory getJsonFactory() {
        return TGitJsonUtil.getJsonFactory();
    }

    @Override
    public RuntimeException handleException(
            Exception e, ScmConnectorRequest connectorRequest, ScmConnectorResponse connectorResponse) {
        if (e instanceof TGitApiException) {
            return (TGitApiException) e;
        } else {
            return new TGitApiException(e, connectorResponse);
        }
    }

    static class TGitConnectorResponseErrorHandler implements ScmConnectorResponseErrorHandler {

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
                throw new TGitApiException(connectorResponse);
            }
        }
    }
}
