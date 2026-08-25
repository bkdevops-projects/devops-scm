package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.RepositoryService
import com.tencent.devops.scm.api.pojo.Hook
import com.tencent.devops.scm.api.pojo.HookInput
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.Perm
import com.tencent.devops.scm.api.pojo.RepoListOptions
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository
import com.tencent.devops.scm.sdk.gitlab.GitlabApiFactory

class GitlabRepositoryService(private val apiFactory: GitlabApiFactory) : RepositoryService {
    override fun find(repository: ScmProviderRepository): ScmServerRepository =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            GitlabObjectConverter.convertRepository(api.projectsApi.getProject(repo.projectIdOrPath))
        }

    override fun findPerms(repository: ScmProviderRepository, username: String): Perm =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            val project = api.projectsApi.getProject(repo.projectIdOrPath)
            val member = api.projectsApi.getMember(repo.projectIdOrPath, username)
            val level = member?.accessLevel ?: 0
            val canPull = when (project.visibility) {
                "public" -> true
                "private" -> level >= REPORTER_ACCESS_LEVEL
                else -> level >= GUEST_ACCESS_LEVEL
            }
            Perm(pull = canPull, push = level >= DEVELOPER_ACCESS_LEVEL, admin = level >= MAINTAINER_ACCESS_LEVEL)
        }

    override fun list(auth: IScmAuth, opts: RepoListOptions): List<ScmServerRepository> =
        GitlabApiTemplate.execute(auth, apiFactory) { api ->
            api.projectsApi.getProjects(opts.repoName, opts.page, pageSize(opts.pageSize))
                .map(GitlabObjectConverter::convertRepository)
        }

    override fun listHooks(repository: ScmProviderRepository, opts: ListOptions): List<Hook> =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.projectHooksApi.getHooks(repo.projectIdOrPath, opts.page, pageSize(opts.pageSize))
                .map(GitlabObjectConverter::convertHook)
        }

    override fun createHook(repository: ScmProviderRepository, input: HookInput): Hook {
        validateHookInput(input)
        val hook = GitlabObjectConverter.convertFromHookInput(input)
        return GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            GitlabObjectConverter.convertHook(api.projectHooksApi.addHook(repo.projectIdOrPath, hook, input.secret))
        }
    }

    override fun updateHook(repository: ScmProviderRepository, hookId: Long, input: HookInput): Hook {
        validateHookInput(input)
        val hook = GitlabObjectConverter.convertFromHookInput(input)
        return GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            GitlabObjectConverter.convertHook(
                api.projectHooksApi.updateHook(repo.projectIdOrPath, hookId, hook, input.secret)
            )
        }
    }

    override fun getHook(repository: ScmProviderRepository, hookId: Long): Hook =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            GitlabObjectConverter.convertHook(api.projectHooksApi.getHook(repo.projectIdOrPath, hookId))
        }

    override fun deleteHook(repository: ScmProviderRepository, hookId: Long) {
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.projectHooksApi.deleteHook(repo.projectIdOrPath, hookId)
        }
    }

    private fun pageSize(value: Int?) = (value ?: 100).coerceIn(1, 100)

    private fun validateHookInput(input: HookInput) {
        require(input.url.isNotBlank()) { "hook url cannot be blank" }
        requireNotNull(input.events) { "hook events cannot be null" }
    }

    private companion object {
        const val GUEST_ACCESS_LEVEL = 10
        const val REPORTER_ACCESS_LEVEL = 20
        const val DEVELOPER_ACCESS_LEVEL = 30
        const val MAINTAINER_ACCESS_LEVEL = 40
    }
}
