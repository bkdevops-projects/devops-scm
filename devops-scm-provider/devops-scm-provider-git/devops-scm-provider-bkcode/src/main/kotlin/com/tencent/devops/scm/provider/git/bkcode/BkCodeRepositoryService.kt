package com.tencent.devops.scm.provider.git.bkcode

import com.tencent.devops.scm.api.RepositoryService
import com.tencent.devops.scm.api.pojo.Hook
import com.tencent.devops.scm.api.pojo.HookInput
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.Perm
import com.tencent.devops.scm.api.pojo.RepoListOptions
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.sdk.bkcode.BkCodeApiFactory
import com.tencent.devops.scm.sdk.bkcode.BkCodeConstants.DEFAULT_PAGE
import com.tencent.devops.scm.sdk.bkcode.BkCodeConstants.DEFAULT_PER_PAGE
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeProjectHookInput
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeWebhookConfig
import okio.ByteString.Companion.encode
import org.slf4j.LoggerFactory

/**
 * BkCode 仓库服务实现类
 * @property apiFactory BkCode API工厂
 */
class BkCodeRepositoryService(private val apiFactory: BkCodeApiFactory) : RepositoryService {

    /**
     * 查找仓库
     * @param repository 代码仓库信息
     * @return 仓库详情
     */
    override fun find(repository: ScmProviderRepository): GitScmServerRepository {
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            val project = bkCodeApi.projectApi.getProject(repo.projectIdOrPath)
            BkCodeObjectConverter.convertRepository(project, repository)
        }
    }

    /**
     * 查找权限
     * @param repository 代码仓库信息
     * @param username 用户名
     * @return 权限信息
     */
    override fun findPerms(repository: ScmProviderRepository, username: String): Perm {
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            try {
                val member = bkCodeApi.projectApi.getAllMembers(
                    repo.projectIdOrPath,
                    username,
                    DEFAULT_PAGE,
                    DEFAULT_PER_PAGE
                )
                if (member == null) {
                    Perm(
                        pull = true,
                        push = false,
                        admin = false
                    )
                } else {
                    Perm(
                        pull = true,
                        push = false,
                        admin = false
                    )
                }
            } catch (e: Exception) {
                Perm(
                    pull = false,
                    push = false,
                    admin = false
                )
            }
        }
    }

    /**
     * 获取仓库列表
     * @param auth 认证信息
     * @param opts 列表查询选项
     * @return 仓库列表
     */
    override fun list(auth: IScmAuth, opts: RepoListOptions): List<ScmServerRepository> {
        return BkCodeApiTemplate.execute(auth, apiFactory) { bkCodeApi ->
            bkCodeApi.projectApi.getProjects(opts.repoName, opts.page, opts.pageSize)
                    .records
                    .map { BkCodeObjectConverter.convertRepository(it) }
        }
    }

    /**
     * 获取钩子列表
     * @param repository 代码仓库信息
     * @param opts 列表查询选项
     * @return 钩子列表
     */
    override fun listHooks(repository: ScmProviderRepository, opts: ListOptions): List<Hook> {
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            bkCodeApi.projectApi.getHooks(repo.projectIdOrPath, opts.page, opts.pageSize)
                    .records
                    .map { BkCodeObjectConverter.convertHook(it) }
        }
    }

    /**
     * 创建钩子
     * @param repository 代码仓库信息
     * @param input 钩子输入参数
     * @return 创建的钩子
     */
    override fun createHook(repository: ScmProviderRepository, input: HookInput): Hook {
        if (input.url == null) {
            throw IllegalArgumentException("url can not empty")
        }
        if (input.events == null || input.events?.getEnabledEvents()?.isEmpty() == true) {
            throw IllegalArgumentException("events can not empty")
        }
        val bkCodeProjectHook = BkCodeObjectConverter.convertFromHookInput(input)
        // bkcode添加hook时，需校验[同名]/[同url]是否存在，若存在则直接修改，反之直接添加
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            val hook = bkCodeApi.projectApi.getHooks(repo.projectIdOrPath).records?.find {
                input.url == it.url || input.name == it.name
            }?.let {
                // 已存在hook，则直接修改
                logger.info(
                    "The hook [$it] already exists; " +
                            "attempting to integrate the input[$bkCodeProjectHook] with the existing hook"
                )
                val existsEvents = it.events ?: listOf()
                if (existsEvents.containsAll(bkCodeProjectHook.events.toList())) {
                    it
                } else {
                    bkCodeApi.projectApi.updateHook(
                        repo.projectIdOrPath,
                        it.id.toLong(),
                        BkCodeProjectHookInput.builder()
                                .name(it.name)
                                .url(it.url)
                                .token(it.token ?: "")
                                .sslVerificationEnabled(it.isSslVerificationEnabled)
                                .events(existsEvents.plus(bkCodeProjectHook.events).toTypedArray())
                                .branchPattern(it.branchPattern)
                                .enabled(true)
                                .build()
                    )
                }
            } ?: bkCodeApi.projectApi.addHook(repo.projectIdOrPath, bkCodeProjectHook)
            BkCodeObjectConverter.convertHook(hook)
        }
    }

    /**
     * 更新钩子
     * @param repository 代码仓库信息
     * @param hookId 钩子ID
     * @param input 钩子输入参数
     * @return 更新后的钩子
     */
    override fun updateHook(repository: ScmProviderRepository, hookId: Long, input: HookInput): Hook {
        val bkCodeProjectHookInput = BkCodeObjectConverter.convertFromHookInput(input)
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            val hook = bkCodeApi.projectApi.updateHook(
                repo.projectIdOrPath,
                hookId,
                bkCodeProjectHookInput
            )
            BkCodeObjectConverter.convertHook(hook)
        }
    }

    /**
     * 删除钩子
     * @param repository 代码仓库信息
     * @param hookId 钩子ID
     */
    override fun deleteHook(repository: ScmProviderRepository, hookId: Long) {
        BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            bkCodeApi.projectApi.deleteHook(repo.projectIdOrPath, hookId)
        }
    }

    /**
     * 获取钩子
     * @param repository 代码仓库信息
     * @param hookId 钩子ID
     * @return 钩子详情
     */
    override fun getHook(repository: ScmProviderRepository, hookId: Long): Hook {
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            val hook = bkCodeApi.projectApi.getHook(repo.projectIdOrPath, hookId)
            BkCodeObjectConverter.convertHook(hook)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BkCodeRepositoryService::class.java)
    }
}
