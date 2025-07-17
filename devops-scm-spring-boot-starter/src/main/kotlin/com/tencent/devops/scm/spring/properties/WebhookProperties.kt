package com.tencent.devops.scm.spring.properties

import com.tencent.devops.scm.api.enums.WebhookSecretType

/**
 * webhook 属性
 */
data class WebhookProperties(
    // webhook 鉴权类型
    var secretType: WebhookSecretType? = null
)