package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.PullRequestService
import com.tencent.devops.scm.api.enums.PullRequestState
import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.Comment
import com.tencent.devops.scm.api.pojo.CommentInput
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.PullRequestInput
import com.tencent.devops.scm.api.pojo.PullRequestListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.gitlab.GitlabApiFactory
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabMergeRequest
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabMergeRequestParams

class GitlabPullRequestService(private val apiFactory: GitlabApiFactory) : PullRequestService {
    override fun find(repository: ScmProviderRepository, number: Int): PullRequest =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            convert(api.mergeRequestsApi.getMergeRequest(repo.projectIdOrPath, number.toLong()), repo.projectIdOrPath, api)
        }

    override fun list(repository: ScmProviderRepository, opts: PullRequestListOptions): List<PullRequest> =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            val target = api.projectsApi.getProject(repo.projectIdOrPath)
            api.mergeRequestsApi.getMergeRequests(
                repo.projectIdOrPath,
                state(opts.state),
                opts.sourceBranch,
                opts.targetBranch,
                opts.page,
                size(opts.pageSize)
            ).map { GitlabObjectConverter.convertPullRequest(it, null, target) }
        }

    override fun create(repository: ScmProviderRepository, input: PullRequestInput): PullRequest {
        require(input.title.isNotBlank()) { "merge request title cannot be blank" }
        require(input.sourceBranch.isNotBlank()) { "source branch cannot be blank" }
        require(input.targetBranch.isNotBlank()) { "target branch cannot be blank" }
        val params = GitlabMergeRequestParams.builder()
            .title(input.title).description(input.body).sourceBranch(input.sourceBranch)
            .targetBranch(input.targetBranch).targetProjectId((input.targetRepo as? Number)?.toLong()).build()
        return GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            convert(api.mergeRequestsApi.createMergeRequest(repo.projectIdOrPath, params), repo.projectIdOrPath, api)
        }
    }

    override fun listChanges(repository: ScmProviderRepository, number: Int, opts: ListOptions): List<Change> =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            val mergeRequest = api.mergeRequestsApi.getMergeRequestChanges(repo.projectIdOrPath, number.toLong())
            check(mergeRequest.overflow != true) {
                "GitLab merge request changes overflow; complete changes are unavailable"
            }
            mergeRequest.changes.orEmpty().map(GitlabObjectConverter::convertChange)
        }

    override fun listCommits(repository: ScmProviderRepository, number: Int, opts: ListOptions): List<Commit> =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.mergeRequestsApi.getMergeRequestCommits(repo.projectIdOrPath, number.toLong(), opts.page, size(opts.pageSize))
                .map(GitlabObjectConverter::convertCommit)
        }

    override fun merge(repository: ScmProviderRepository, number: Int) {
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.mergeRequestsApi.mergeMergeRequest(repo.projectIdOrPath, number.toLong(), null, null, null)
        }
    }

    override fun close(repository: ScmProviderRepository, number: Int) {
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.mergeRequestsApi.closeMergeRequest(repo.projectIdOrPath, number.toLong())
        }
    }

    override fun findComment(repository: ScmProviderRepository, number: Int, commentId: Long): Comment =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            GitlabObjectConverter.convertComment(api.notesApi.getMergeRequestNote(repo.projectIdOrPath, number.toLong(), commentId))
        }

    override fun listComments(repository: ScmProviderRepository, number: Int, opts: ListOptions): List<Comment> =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.notesApi.getMergeRequestNotes(repo.projectIdOrPath, number.toLong(), opts.page, size(opts.pageSize))
                .map(GitlabObjectConverter::convertComment)
        }

    override fun createComment(repository: ScmProviderRepository, number: Int, input: CommentInput): Comment {
        require(input.body.isNotBlank()) { "comment body cannot be blank" }
        return GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            GitlabObjectConverter.convertComment(
                api.notesApi.createMergeRequestNote(repo.projectIdOrPath, number.toLong(), input.body)
            )
        }
    }

    override fun deleteComment(repository: ScmProviderRepository, number: Int, commentId: Long) {
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.notesApi.deleteMergeRequestNote(repo.projectIdOrPath, number.toLong(), commentId)
        }
    }

    private fun convert(mr: GitlabMergeRequest, project: Any, api: com.tencent.devops.scm.sdk.gitlab.GitlabApi): PullRequest {
        val target = api.projectsApi.getProject(mr.targetProjectId ?: project)
        val source = if (mr.sourceProjectId == mr.targetProjectId) target else mr.sourceProjectId?.let {
            runCatching { api.projectsApi.getProject(it) }.getOrNull()
        }
        return GitlabObjectConverter.convertPullRequest(mr, source, target)
    }

    private fun state(value: PullRequestState?) = when (value) {
        PullRequestState.OPENED, PullRequestState.REOPENED -> "opened"
        PullRequestState.CLOSED -> "closed"
        PullRequestState.MERGED -> "merged"
        else -> null
    }

    private fun size(value: Int?) = (value ?: 100).coerceIn(1, 100)
}
