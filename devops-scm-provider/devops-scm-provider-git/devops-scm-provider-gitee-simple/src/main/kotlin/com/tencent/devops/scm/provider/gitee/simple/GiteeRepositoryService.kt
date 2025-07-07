package com.tencent.devops.scm.provider.gitee.simple

import com.gitee.sdk.gitee5j.ApiException
import com.gitee.sdk.gitee5j.api.RepositoriesApi
import com.gitee.sdk.gitee5j.model.Project
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

class GiteeRepositoryService(private val giteeApiFactory: GiteeApiClientFactory) : RepositoryService {

    override fun find(repository: ScmProviderRepository): ScmServerRepository {
        return GiteeApiTemplate.execute(
            repository,
            giteeApiFactory
        ) { repoName, client ->
            val repositoriesApi = RepositoriesApi(client)
            val project: Project
            try {
                project = repositoriesApi.getReposOwnerRepoWithHttpInfo(
                    repoName.first,
                    repoName.second
                ).data
            } catch (e: ApiException) {
                throw RuntimeException(e)
            }
            // 结果转化
            GiteeObjectConverter.convertRepository(project)
        }
    }

    override fun findPerms(repository: ScmProviderRepository, username: String): Perm {
        throw UnsupportedOperationException("gitee template not support find perms")
    }

    override fun list(auth: IScmAuth, opts: RepoListOptions): List<ScmServerRepository> {
        throw UnsupportedOperationException("gitee template not support list repo")
    }

    override fun listHooks(repository: ScmProviderRepository, opts: ListOptions): List<Hook> {
        throw UnsupportedOperationException("gitee template not support list hook")
    }

    override fun createHook(repository: ScmProviderRepository, input: HookInput): Hook {
        throw UnsupportedOperationException("gitee template not support create hook")
    }

    override fun updateHook(
        repository: ScmProviderRepository,
        hookId: Long,
        input: HookInput
    ): Hook {
        throw UnsupportedOperationException("gitee template not support update hook")
    }

    override fun getHook(repository: ScmProviderRepository, hookId: Long): Hook {
        throw UnsupportedOperationException("gitee template not support get hook")
    }

    override fun deleteHook(repository: ScmProviderRepository, hookId: Long) {
        throw UnsupportedOperationException("gitee template not support delete hook")
    }

    override fun listStatus(
        repository: ScmProviderRepository,
        ref: String,
        opts: ListOptions
    ): List<Status> {
        throw UnsupportedOperationException("gitee template not support list status")
    }

    override fun createStatus(
        repository: ScmProviderRepository,
        ref: String,
        input: StatusInput
    ): Status {
        throw UnsupportedOperationException("gitee template not support create status")
    }
}
