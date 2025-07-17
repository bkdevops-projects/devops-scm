package com.tencent.devops.scm.api.pojo

/**
 * scm i18变量,只声明code,118转换在bk-ci工程，运行时转换
 */
data class ScmI18Variable(
    // i18 code
    val code: String,
    // 转换参数
    val params: List<String>? = null,
    // 默认消息
    val defaultMessage: String? = null
)
