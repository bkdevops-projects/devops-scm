package com.tencent.devops.scm.api

import com.tencent.devops.scm.api.pojo.CheckRun
import com.tencent.devops.scm.api.pojo.CheckRunInput
import com.tencent.devops.scm.api.pojo.CheckRunListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository

/**
 * 仓库检查
 */
interface CheckRunService {
    /**
     * 创建检查
     * @param repository 代码库信息
     * @param input 检查状态信息
     */
    fun create(repository: ScmProviderRepository, input: CheckRunInput): CheckRun

    /**
     * 更新检查
     * @param repository 代码库信息
     * @param input 检查状态信息
     */
    fun update(repository: ScmProviderRepository, input: CheckRunInput): CheckRun

    /**
     * 获取检查项列表
     * @param repository 代码库信息
     * @param opts 检查项目过滤条件
     */
    fun getCheckRuns(repository: ScmProviderRepository, opts: CheckRunListOptions): List<CheckRun>
}
