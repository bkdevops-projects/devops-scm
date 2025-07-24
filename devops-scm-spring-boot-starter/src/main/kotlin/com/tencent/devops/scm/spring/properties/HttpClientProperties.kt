package com.tencent.devops.scm.spring.properties

/**
 * HTTP客户端配置属性
 *
 * @property apiUrl API地址
 * @property connectTimeout 连接超时时间（单位：秒），默认5秒
 * @property readTimeout 读取超时时间（单位：秒），默认30秒
 * @property writeTimeout 写入超时时间（单位：秒），默认30秒
 * @property metrics 是否启用度量，默认false
 */
data class HttpClientProperties(
    var apiUrl: String? = null,
    var connectTimeout: Long = 5L,
    var readTimeout: Long = 30L,
    var writeTimeout: Long = 30L,
    var metrics: Boolean = false
)