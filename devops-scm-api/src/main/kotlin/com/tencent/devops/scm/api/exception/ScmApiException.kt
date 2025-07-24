package com.tencent.devops.scm.api.exception

open class ScmApiException : RuntimeException {
    var statusCode: Int? = null

    constructor(message: String?, code: Int) : super(message) {
        this.statusCode = code
    }

    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)
}
