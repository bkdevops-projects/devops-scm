package com.tencent.devops.scm.sdk.bkcode;

import com.tencent.devops.scm.sdk.common.connector.ScmConnectorResponse;
import com.tencent.devops.scm.sdk.common.exception.BaseScmApiException;

public class BkCodeApiException extends BaseScmApiException {

    public BkCodeApiException(String message) {
        super(message);
    }

    public BkCodeApiException(int statusCode, String message) {
        super(statusCode, message);
    }

    public BkCodeApiException(Exception e) {
        super(e);
    }

    public BkCodeApiException(String message, Exception e) {
        super(message, e);
    }

    public BkCodeApiException(Exception e,
            ScmConnectorResponse connectorResponse) {
        super(e, connectorResponse);
    }

    public BkCodeApiException(ScmConnectorResponse connectorResponse) {
        super(connectorResponse);
    }
}
