package com.tencent.devops.scm.api.exception;

import lombok.Getter;

/**
 * 资源不存在（HTTP 404）
 * 用于处理404异常
 */
@Getter
public class NotFoundScmApiException extends ScmApiException {
    int code = 404;

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
