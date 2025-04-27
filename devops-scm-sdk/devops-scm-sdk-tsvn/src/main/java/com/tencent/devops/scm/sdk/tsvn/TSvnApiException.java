package com.tencent.devops.scm.sdk.tsvn;

import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponse;
import com.tencent.devops.scm.sdk.common.exception.ScmApiException;

public class TSvnApiException extends ScmApiException {

    public TSvnApiException(String message) {
        super(message);
    }

    public TSvnApiException(int statusCode, String message) {
        super(statusCode, message);
    }

    public TSvnApiException(Exception e) {
        super(e);
    }

    public TSvnApiException(String message, Exception e) {
        super(message, e);
    }

    public TSvnApiException(Exception e, ScmConnectorResponse connectorResponse) {
        super(e, connectorResponse);
    }

    public TSvnApiException(ScmConnectorResponse connectorResponse) {
        super(connectorResponse);
    }
}
