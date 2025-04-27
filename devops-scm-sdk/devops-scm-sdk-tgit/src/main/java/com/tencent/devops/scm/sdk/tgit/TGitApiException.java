package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponse;
import com.tencent.devops.scm.sdk.common.exception.ScmApiException;

public class TGitApiException extends ScmApiException {

    public TGitApiException(String message) {
        super(message);
    }

    public TGitApiException(int statusCode, String message) {
        super(statusCode, message);
    }

    public TGitApiException(Exception e) {
        super(e);
    }

    public TGitApiException(String message, Exception e) {
        super(message, e);
    }

    public TGitApiException(Exception e, ScmConnectorResponse connectorResponse) {
        super(e, connectorResponse);
    }

    public TGitApiException(ScmConnectorResponse connectorResponse) {
        super(connectorResponse);
    }
}
