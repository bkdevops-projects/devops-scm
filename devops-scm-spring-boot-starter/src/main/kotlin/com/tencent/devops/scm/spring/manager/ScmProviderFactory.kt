package com.tencent.devops.scm.spring.manager

import com.tencent.devops.scm.api.ScmProvider
import com.tencent.devops.scm.spring.properties.ScmProviderProperties

interface ScmProviderFactory {

    /**
     * 判断是否支持该仓库属性
     *
     * @param properties 仓库属性
     * @return 是否支持
     */
    fun support(properties: ScmProviderProperties): Boolean

    /**
     * 获取源码提供者处理类
     *
     * @param properties 仓库属性
     * @param tokenApi 是否是token api请求
     * @return 源码提供者处理类
     */
    fun build(properties: ScmProviderProperties, tokenApi: Boolean): ScmProvider
}