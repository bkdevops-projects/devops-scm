package com.tencent.devops.scm.sdk.gitlab;

import com.fasterxml.jackson.databind.JsonNode;
import com.tencent.devops.scm.sdk.common.ScmResponse;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponse;
import com.tencent.devops.scm.sdk.common.exception.BaseScmApiException;
import com.tencent.devops.scm.sdk.gitlab.util.GitlabJsonUtil;
import lombok.Getter;

public class GitlabApiException extends BaseScmApiException {
    @Getter
    private final String requestId;
    private final String gitlabMessage;

    public GitlabApiException(String message) {
        super(message);
        this.requestId = null;
        this.gitlabMessage = null;
    }

    public GitlabApiException(int statusCode, String message, String requestId) {
        super(statusCode, message);
        this.requestId = requestId;
        this.gitlabMessage = null;
    }

    public GitlabApiException(Exception cause) {
        super(cause);
        this.requestId = null;
        this.gitlabMessage = null;
    }

    public GitlabApiException(Exception cause, ScmConnectorResponse response) {
        super(cause, response);
        this.requestId = response == null ? null : response.header(GitlabConstants.REQUEST_ID_HEADER);
        this.gitlabMessage = extractErrorMessage(response);
    }

    public GitlabApiException(ScmConnectorResponse response) {
        super(response);
        this.requestId = response.header(GitlabConstants.REQUEST_ID_HEADER);
        this.gitlabMessage = extractErrorMessage(response);
    }

    @Override
    public String getMessage() {
        return gitlabMessage == null ? super.getMessage() : gitlabMessage;
    }

    private static String extractErrorMessage(ScmConnectorResponse response) {
        if (response == null) {
            return null;
        }
        try {
            JsonNode body = GitlabJsonUtil.getJsonFactory()
                    .toJsonNode(ScmResponse.getBodyAsString(response));
            String description = nodeMessage(body.get("error_description"));
            return description == null ? nodeMessage(body.get("error")) : description;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String nodeMessage(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        String value = node.isTextual() ? node.asText() : node.toString();
        return value.trim().isEmpty() ? null : value;
    }
}
