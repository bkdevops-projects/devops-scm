package com.tencent.devops.scm.sdk.common.exception;

public class ScmSdkException extends RuntimeException {

    public ScmSdkException(String message) {
        super(message);
    }

    public ScmSdkException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScmSdkException(Exception exception) {
        super(exception);
    }
}
