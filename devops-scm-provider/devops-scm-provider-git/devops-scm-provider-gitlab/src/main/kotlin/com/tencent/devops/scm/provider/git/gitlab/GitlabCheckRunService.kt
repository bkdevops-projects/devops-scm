package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.CheckRunService
import com.tencent.devops.scm.api.pojo.CheckRun
import com.tencent.devops.scm.api.pojo.CheckRunInput
import com.tencent.devops.scm.api.pojo.CheckRunListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.gitlab.GitlabApiFactory

class GitlabCheckRunService(private val apiFactory: GitlabApiFactory) : CheckRunService {
    override fun create(repository: ScmProviderRepository, input: CheckRunInput): CheckRun {
        val sha = requireNotNull(input.ref).takeIf(String::isNotBlank)
            ?: throw IllegalArgumentException("check run ref cannot be blank")
        val state = GitlabObjectConverter.convertCheckRunState(input.status, input.conclusion)
        return GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            GitlabObjectConverter.convertCheckRun(
                api.commitStatusesApi.create(
                    repo.projectIdOrPath,
                    sha,
                    state,
                    input.name,
                    null,
                    input.detailsUrl,
                    input.output?.summary
                )
            )
        }
    }

    override fun update(repository: ScmProviderRepository, input: CheckRunInput): CheckRun = create(repository, input)

    override fun getCheckRuns(repository: ScmProviderRepository, opts: CheckRunListOptions): List<CheckRun> =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.commitStatusesApi.getStatuses(
                repo.projectIdOrPath,
                opts.ref,
                null,
                null,
                opts.page,
                (opts.pageSize ?: 100).coerceIn(1, 100)
            ).map(GitlabObjectConverter::convertCheckRun)
        }
}
