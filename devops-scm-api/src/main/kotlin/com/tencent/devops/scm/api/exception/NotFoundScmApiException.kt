package com.tencent.devops.scm.api.exception

import lombok.Getter

/**
 * 资源不存在（HTTP 404）
 * 用于处理404异常
 */
@Getter
class NotFoundScmApiException : ScmApiException {
    constructor(message: String?) : super(message) {
        this.statusCode = 404
    }

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)
}
