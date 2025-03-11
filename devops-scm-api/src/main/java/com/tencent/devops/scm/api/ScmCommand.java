package com.tencent.devops.scm.api;

import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;

/**
 * scm命令行接口
 */
public interface ScmCommand {

    /**
     * 获取远端仓库信息
     * 主要用户校验凭证有效性
     * @param repository 提供者代码库
     */
    void remoteInfo(ScmProviderRepository repository);
}
