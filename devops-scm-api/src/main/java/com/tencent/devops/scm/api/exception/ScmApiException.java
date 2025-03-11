package com.tencent.devops.scm.api.exception;

/**
 * scm api异常基类
 */
public class ScmApiException extends RuntimeException {

    public ScmApiException(String message) {
        super(message);
    }

    public ScmApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScmApiException(Throwable cause) {
        super(cause);
    }
}
