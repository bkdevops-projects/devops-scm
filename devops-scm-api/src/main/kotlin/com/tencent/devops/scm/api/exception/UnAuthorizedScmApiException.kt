package com.tencent.devops.scm.api.exception

/**
 * 认证失败异常（HTTP 401）
 * 用于处理以下情况：
 * 1. 无效的访问令牌
 * 2. 过期的访问凭证
 * 3. 未提供认证信息
 * 4. 权限范围不足
 */
class UnAuthorizedScmApiException : ScmApiException {
    constructor(message: String?) : super(message) {
        this.statusCode = 401
    }

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
