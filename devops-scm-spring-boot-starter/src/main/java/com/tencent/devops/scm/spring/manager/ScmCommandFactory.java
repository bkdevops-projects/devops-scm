package com.tencent.devops.scm.spring.manager;

import com.tencent.devops.scm.api.ScmCommand;
import com.tencent.devops.scm.spring.properties.ScmProviderProperties;

public interface ScmCommandFactory {

    Boolean support(ScmProviderProperties properties);

    /**
     * 获取源码提供者处理类
     *
     * @param properties 仓库属性
     */
    ScmCommand build(ScmProviderProperties properties);
}
