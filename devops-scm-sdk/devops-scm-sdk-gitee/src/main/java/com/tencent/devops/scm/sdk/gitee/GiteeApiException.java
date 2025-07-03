package com.tencent.devops.scm.sdk.gitee;

import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponse;
import com.tencent.devops.scm.sdk.common.exception.BaseScmApiException;

public class GiteeApiException extends BaseScmApiException {

    public GiteeApiException(String message) {
        super(message);
    }

    public GiteeApiException(int statusCode, String message) {
        super(statusCode, message);
    }

    public GiteeApiException(Exception e) {
        super(e);
    }

    public GiteeApiException(String message, Exception e) {
        super(message, e);
    }

    public GiteeApiException(Exception e,
            ScmConnectorResponse connectorResponse) {
        super(e, connectorResponse);
    }

    public GiteeApiException(ScmConnectorResponse connectorResponse) {
        super(connectorResponse);
    }
}
