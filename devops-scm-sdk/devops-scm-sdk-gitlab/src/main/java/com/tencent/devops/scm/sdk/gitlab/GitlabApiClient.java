package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.ScmApiClient;
import com.tencent.devops.scm.sdk.common.ScmRequest;
import com.tencent.devops.scm.sdk.common.ScmResponse;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorRequest;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponse;
import com.tencent.devops.scm.sdk.common.exception.ScmHttpRetryException;
import com.tencent.devops.scm.sdk.common.util.ScmSdkJsonFactory;
import com.tencent.devops.scm.sdk.gitlab.auth.GitlabAuthProvider;
import com.tencent.devops.scm.sdk.gitlab.util.GitlabJsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitlabApiClient extends ScmApiClient {
    private static final Logger logger = LoggerFactory.getLogger(GitlabApiClient.class);
    private final GitlabAuthProvider authProvider;

    public GitlabApiClient(String apiUrl, ScmConnector connector, GitlabAuthProvider authProvider) {
        super(apiUrl, connector);
        if (authProvider == null) {
            throw new IllegalArgumentException("authProvider cannot be null");
        }
        this.authProvider = authProvider;
    }

    @Override
    public void beforeRequest(ScmRequest originRequest, ScmRequest.Builder<?> builder) {
        authProvider.authorization(builder);
    }

    @Override
    public <T> ScmResponse<T> sendRequest(ScmRequest request, BodyHandler<T> handler) {
        return super.sendRequest(request, response -> response.statusCode() == 204 || handler == null
                ? null : handler.apply(response));
    }

    @Override
    public void afterRequest(ScmConnectorResponse response, ScmRequest request) {
        logger.info("GitLab API response|requestId:{}", response.header(GitlabConstants.REQUEST_ID_HEADER));
        int status = response.statusCode();
        if (status == 429 || status == 500 || status == 502 || status == 503 || status == 504) {
            throw new ScmHttpRetryException();
        }
        if (status >= 400) {
            throw new GitlabApiException(response);
        }
    }

    @Override
    public RuntimeException handleException(Exception error, ScmConnectorRequest request,
            ScmConnectorResponse response) {
        if (error instanceof ScmHttpRetryException) {
            return (ScmHttpRetryException) error;
        }
        if (error instanceof GitlabApiException) {
            return (GitlabApiException) error;
        }
        return new GitlabApiException(error, response);
    }

    @Override
    public ScmSdkJsonFactory getJsonFactory() {
        return GitlabJsonUtil.getJsonFactory();
    }
}
