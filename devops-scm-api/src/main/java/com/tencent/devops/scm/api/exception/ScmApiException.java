package com.tencent.devops.scm.api.exception;

import lombok.Getter;

/**
 * scm api异常基类
 */
@Getter
public class ScmApiException extends RuntimeException {
    Integer statusCode;

    public ScmApiException(String message, int code) {
        super(message);
        this.statusCode = code;
    }

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
