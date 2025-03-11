package com.tencent.devops.scm.api.exception;

/**
 * 资源不存在（HTTP 404）
 * 用于处理404异常
 */
public class NotFoundScmApiException extends ScmApiException {

    public NotFoundScmApiException(String message) {
        super(message);
    }

    public NotFoundScmApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundScmApiException(Throwable cause) {
        super(cause);
    }
}
