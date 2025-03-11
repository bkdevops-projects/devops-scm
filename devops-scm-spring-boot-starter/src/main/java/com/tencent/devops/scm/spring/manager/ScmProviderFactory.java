package com.tencent.devops.scm.spring.manager;

import com.tencent.devops.scm.api.ScmProvider;
import com.tencent.devops.scm.spring.properties.ScmProviderProperties;

public interface ScmProviderFactory {

    Boolean support(ScmProviderProperties properties);

    /**
     * 获取源码提供者处理类
     *
     * @param properties 仓库属性
     * @param tokenApi 是否是token api请求
     */
    ScmProvider build(ScmProviderProperties properties, boolean tokenApi);
}
