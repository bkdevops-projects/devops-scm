package com.tencent.devops.scm.api.exception;

import lombok.Getter;

/**
 * scm api异常基类
 */
@Getter
public class ScmApiException extends RuntimeException {
    Integer code;

    public ScmApiException(String message, int code) {
        super(message);
        this.code = code;
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
