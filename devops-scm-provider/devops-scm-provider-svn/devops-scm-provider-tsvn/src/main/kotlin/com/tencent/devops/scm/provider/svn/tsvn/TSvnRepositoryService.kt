package com.tencent.devops.scm.provider.svn.tsvn

import com.tencent.devops.scm.api.RepositoryService
import com.tencent.devops.scm.api.pojo.Hook
import com.tencent.devops.scm.api.pojo.HookInput
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.Perm
import com.tencent.devops.scm.api.pojo.RepoListOptions
import com.tencent.devops.scm.api.pojo.Status
import com.tencent.devops.scm.api.pojo.StatusInput
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository
import com.tencent.devops.scm.sdk.tsvn.TSvnApiFactory
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnWebHookConfig

class TSvnRepositoryService(private val apiFactory: TSvnApiFactory) : RepositoryService {

    override fun find(repository: ScmProviderRepository): ScmServerRepository {
        throw UnsupportedOperationException("tsvn not support find repository")
    }

    override fun findPerms(repository: ScmProviderRepository, username: String): Perm {
        throw UnsupportedOperationException("tsvn not support find perms")
    }

    override fun list(auth: IScmAuth, opts: RepoListOptions): List<ScmServerRepository> {
        throw UnsupportedOperationException("tsvn not support get repo list")
    }

    override fun listHooks(repository: ScmProviderRepository, opts: ListOptions): List<Hook> {
        return TSvnApiTemplate.execute(repository, apiFactory) { repo, tSvnApi ->
            val webhookApi = tSvnApi.webhookApi
            val projectApiHooks = webhookApi.listHook(
                repo.projectIdOrPath,
                opts.page,
                opts.pageSize
            )
            projectApiHooks.map { TSvnObjectConverter.convertHook(it) }
        }
    }

    override fun createHook(repository: ScmProviderRepository, input: HookInput): Hook {
        return TSvnApiTemplate.execute(repository, apiFactory) { repo, tSvnApi ->
            val webhookApi = tSvnApi.webhookApi
            val projectApiHook = webhookApi.addHook(
                repo.projectIdOrPath,
                convertFromHookInput(input)
            )
            TSvnObjectConverter.convertHook(projectApiHook)
        }
    }

    override fun updateHook(repository: ScmProviderRepository, hookId: Long, input: HookInput): Hook {
        return TSvnApiTemplate.execute(repository, apiFactory) { repo, tSvnApi ->
            val webhookApi = tSvnApi.webhookApi
            val projectApiHook = webhookApi.editHook(
                repo.projectIdOrPath,
                hookId,
                convertFromHookInput(input)
            )
            TSvnObjectConverter.convertHook(projectApiHook)
        }
    }

    override fun getHook(repository: ScmProviderRepository, hookId: Long): Hook {
        return TSvnApiTemplate.execute(repository, apiFactory) { repo, tSvnApi ->
            val webhookApi = tSvnApi.webhookApi
            val projectApiHook = webhookApi.getHook(
                repo.projectIdOrPath,
                hookId
            )
            TSvnObjectConverter.convertHook(projectApiHook)
        }
    }

    override fun deleteHook(repository: ScmProviderRepository, hookId: Long) {
        return TSvnApiTemplate.execute(repository, apiFactory) { repo, tSvnApi ->
            val webhookApi = tSvnApi.webhookApi
            webhookApi.delHook(
                repo.projectIdOrPath,
                hookId
            )
        }
    }

    override fun listStatus(repository: ScmProviderRepository, ref: String, opts: ListOptions): List<Status> {
        throw UnsupportedOperationException("tsvn not support get status list")
    }

    override fun createStatus(repository: ScmProviderRepository, ref: String, input: StatusInput): Status {
        throw UnsupportedOperationException("tsvn not support create status")
    }

    private fun convertFromHookInput(input: HookInput) = with(input) {
        TSvnWebHookConfig.builder()
                .url(url)
                .path(path)
                .svnPreLockEvents(events?.svnPreLockEvents == true)
                .svnPostLockEvents(events?.svnPostLockEvents == true)
                .svnPreCommitEvents(events?.svnPreCommitEvents == true)
                .svnPostCommitEvents(events?.svnPostCommitEvents == true)
                .build()
    }
}