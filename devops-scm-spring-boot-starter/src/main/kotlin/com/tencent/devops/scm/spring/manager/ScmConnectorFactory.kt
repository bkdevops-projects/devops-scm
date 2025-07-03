package com.tencent.devops.scm.spring.manager

import com.tencent.devops.scm.sdk.common.connector.ScmConnector
import com.tencent.devops.scm.spring.properties.HttpClientProperties

interface ScmConnectorFactory {
    /**
     * 创建SCM连接器
     *
     * @param properties HTTP客户端配置属性
     * @return SCM连接器实例
     */
    fun create(properties: HttpClientProperties): ScmConnector
}