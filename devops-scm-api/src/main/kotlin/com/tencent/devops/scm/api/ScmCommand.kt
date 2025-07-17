package com.tencent.devops.scm.api

import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository

/**
 * SCM命令行接口
 */
interface ScmCommand {
    /**
     * 获取远端仓库信息
     * 主要用于校验凭证有效性
     * @param repository 提供者代码库信息
     */
    fun remoteInfo(repository: ScmProviderRepository)
}
