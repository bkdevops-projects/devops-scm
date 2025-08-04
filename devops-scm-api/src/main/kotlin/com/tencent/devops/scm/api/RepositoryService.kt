package com.tencent.devops.scm.api

import com.tencent.devops.scm.api.pojo.Hook
import com.tencent.devops.scm.api.pojo.HookInput
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.Perm
import com.tencent.devops.scm.api.pojo.RepoListOptions
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository

/**
 * 代码库服务接口
 */
interface RepositoryService {
    /**
     * 查找代码库通过代码库名
     * @param repository 代码库信息
     * @return 代码库详细信息
     */
    fun find(repository: ScmProviderRepository): ScmServerRepository

    /**
     * 获取用户权限
     * @param repository 代码库信息
     * @param username 用户名
     * @return 用户权限信息
     */
    fun findPerms(repository: ScmProviderRepository, username: String): Perm

    /**
     * 获取代码库列表
     * @param auth 认证信息
     * @param opts 仓库列表查询参数
     * @return 代码库列表
     */
    fun list(auth: IScmAuth, opts: RepoListOptions): List<ScmServerRepository>

    /**
     * 列出代码库钩子
     * @param repository 代码库信息
     * @param opts 列表查询选项
     * @return 钩子列表
     */
    fun listHooks(repository: ScmProviderRepository, opts: ListOptions): List<Hook>

    /**
     * 创建代码库钩子
     * @param repository 代码库信息
     * @param input 钩子输入参数
     * @return 创建的钩子
     */
    fun createHook(repository: ScmProviderRepository, input: HookInput): Hook

    /**
     * 更新代码库钩子
     * @param repository 代码库信息
     * @param hookId 钩子ID
     * @param input 钩子输入参数
     * @return 更新后的钩子
     */
    fun updateHook(repository: ScmProviderRepository, hookId: Long, input: HookInput): Hook

    /**
     * 获取代码库钩子
     * @param repository 代码库信息
     * @param hookId 钩子ID
     * @return 钩子详情
     */
    fun getHook(repository: ScmProviderRepository, hookId: Long): Hook

    /**
     * 删除代码库钩子
     * @param repository 代码库信息
     * @param hookId 钩子ID
     */
    fun deleteHook(repository: ScmProviderRepository, hookId: Long)
}
