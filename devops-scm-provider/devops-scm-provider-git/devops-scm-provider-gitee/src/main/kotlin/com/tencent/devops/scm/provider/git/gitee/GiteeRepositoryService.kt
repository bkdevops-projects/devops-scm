package com.tencent.devops.scm.provider.git.gitee

import com.tencent.devops.scm.api.RepositoryService
import com.tencent.devops.scm.api.enums.StatusState
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
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeProjectHook

class GiteeRepositoryService(private val apiFactory: GiteeApiFactory) : RepositoryService {

    override fun find(repository: ScmProviderRepository): ScmServerRepository {
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val projectApi = giteeApi.getProjectApi()
            val repositoryDetail = projectApi.getProject(repo.projectIdOrPath)
            GiteeObjectConverter.convertRepository(repositoryDetail)
        }
    }

    override fun findPerms(repository: ScmProviderRepository, username: String) = Perm()

    override fun list(auth: IScmAuth, opts: RepoListOptions): List<ScmServerRepository> {
        return emptyList()
    }

    override fun listHooks(repository: ScmProviderRepository, opts: ListOptions): List<Hook> {
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val projectApi = giteeApi.getProjectApi()
            val projectApiHooks = projectApi.getHooks(
                repo.projectIdOrPath,
                opts.page,
                opts.pageSize
            )
            projectApiHooks.map { GiteeObjectConverter.convertHook(it) }
        }
    }

    override fun createHook(repository: ScmProviderRepository, input: HookInput): Hook {
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val projectApi = giteeApi.getProjectApi()
            val projectApiHook = projectApi.addHook(
                repo.projectIdOrPath,
                convertFromHookInput(input),
                input.secret
            )
            GiteeObjectConverter.convertHook(projectApiHook)
        }
    }

    override fun updateHook(
        repository: ScmProviderRepository,
        hookId: Long,
        input: HookInput
    ): Hook {
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val projectApi = giteeApi.getProjectApi()
            val projectApiHook = projectApi.updateHook(
                repo.projectIdOrPath,
                hookId,
                convertFromHookInput(input),
                input.secret
            )
            GiteeObjectConverter.convertHook(projectApiHook)
        }
    }

    override fun getHook(repository: ScmProviderRepository, hookId: Long): Hook {
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val projectApi = giteeApi.getProjectApi()
            val projectApiHook = projectApi.getHook(
                repo.projectIdOrPath,
                hookId
            )
            GiteeObjectConverter.convertHook(projectApiHook)
        }
    }

    override fun deleteHook(repository: ScmProviderRepository, hookId: Long) {
        GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val projectApi = giteeApi.getProjectApi()
            projectApi.deleteHook(
                repo.projectIdOrPath,
                hookId
            )
        }
    }

    override fun listStatus(
        repository: ScmProviderRepository,
        ref: String,
        opts: ListOptions
    ): List<Status> {
        return emptyList()
    }

    override fun createStatus(
        repository: ScmProviderRepository,
        ref: String,
        input: StatusInput
    ) = Status(
        state = StatusState.SUCCESS,
        targetUrl = "",
        desc = "",
        context = ""
    )

    private fun convertFromHookInput(input: HookInput): GiteeProjectHook {
        return GiteeProjectHook.builder()
                .url(input.url)
                .pushEvents(input.events?.push ?: false)
                .tagPushEvents(input.events?.tag ?: false)
                .mergeRequestsEvents(input.events?.pullRequest ?: false)
                .issuesEvents(input.events?.issue ?: false)
                .reviewEvents(input.events?.pullRequestReview ?: false)
                .build()
    }
}
