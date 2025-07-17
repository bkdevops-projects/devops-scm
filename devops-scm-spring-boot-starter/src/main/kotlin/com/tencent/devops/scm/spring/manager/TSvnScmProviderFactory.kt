package com.tencent.devops.scm.spring.manager

import com.tencent.devops.scm.api.ScmProvider
import com.tencent.devops.scm.api.enums.ScmProviderCodes
import com.tencent.devops.scm.provider.svn.tsvn.TSvnScmProvider
import com.tencent.devops.scm.spring.properties.ScmProviderProperties

class TSvnScmProviderFactory(
    private val connectorFactory: ScmConnectorFactory
) : ScmProviderFactory {

    /**
     * 判断是否支持TSVN仓库
     *
     * @param properties 仓库属性
     * @return 是否支持
     */
    override fun support(properties: ScmProviderProperties): Boolean {
        return ScmProviderCodes.TSVN.name == properties.providerCode
    }

    /**
     * 构建TSVN SCM提供者
     *
     * @param properties 仓库属性
     * @param tokenApi 是否是token api请求（此实现中未使用）
     * @return TSVN SCM提供者实例
     */
    override fun build(properties: ScmProviderProperties, tokenApi: Boolean): ScmProvider {
        val httpClientProperties = properties.httpClientProperties
        val connector = connectorFactory.create(httpClientProperties!!)
        return TSvnScmProvider(httpClientProperties.apiUrl ?: "", connector)
    }
}