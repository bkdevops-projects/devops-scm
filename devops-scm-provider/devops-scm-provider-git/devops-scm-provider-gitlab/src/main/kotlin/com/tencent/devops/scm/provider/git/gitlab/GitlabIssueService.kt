package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.IssueService
import com.tencent.devops.scm.api.enums.IssueState
import com.tencent.devops.scm.api.pojo.Comment
import com.tencent.devops.scm.api.pojo.CommentInput
import com.tencent.devops.scm.api.pojo.Issue
import com.tencent.devops.scm.api.pojo.IssueInput
import com.tencent.devops.scm.api.pojo.IssueListOptions
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.gitlab.GitlabApiFactory
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabIssueParams

class GitlabIssueService(private val apiFactory: GitlabApiFactory) : IssueService {
    override fun find(repository: ScmProviderRepository, number: Int): Issue =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            GitlabObjectConverter.convertIssue(api.issuesApi.getIssue(repo.projectIdOrPath, number.toLong()))
        }

    override fun create(repository: ScmProviderRepository, input: IssueInput): Issue {
        require(input.title.isNotBlank()) { "issue title cannot be blank" }
        val params = GitlabIssueParams.builder().title(input.title).description(input.body).build()
        return GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            GitlabObjectConverter.convertIssue(api.issuesApi.createIssue(repo.projectIdOrPath, params))
        }
    }

    override fun list(repository: ScmProviderRepository, opts: IssueListOptions): List<Issue> =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.issuesApi.getIssues(repo.projectIdOrPath, state(opts.state), null, opts.page, size(opts.pageSize))
                .map(GitlabObjectConverter::convertIssue)
        }

    override fun close(repository: ScmProviderRepository, number: Int) {
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.issuesApi.closeIssue(repo.projectIdOrPath, number.toLong())
        }
    }

    override fun findComment(repository: ScmProviderRepository, number: Int, commentId: Long): Comment =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            GitlabObjectConverter.convertComment(api.notesApi.getIssueNote(repo.projectIdOrPath, number.toLong(), commentId))
        }

    override fun listComments(repository: ScmProviderRepository, number: Int, opts: ListOptions): List<Comment> =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.notesApi.getIssueNotes(repo.projectIdOrPath, number.toLong(), opts.page, size(opts.pageSize))
                .map(GitlabObjectConverter::convertComment)
        }

    override fun createComment(repository: ScmProviderRepository, number: Int, input: CommentInput): Comment {
        require(input.body.isNotBlank()) { "comment body cannot be blank" }
        return GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            GitlabObjectConverter.convertComment(api.notesApi.createIssueNote(repo.projectIdOrPath, number.toLong(), input.body))
        }
    }

    override fun deleteComment(repository: ScmProviderRepository, number: Int, commentId: Long) {
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.notesApi.deleteIssueNote(repo.projectIdOrPath, number.toLong(), commentId)
        }
    }

    private fun state(value: IssueState?) = when (value) {
        IssueState.OPENED, IssueState.REOPENED -> "opened"
        IssueState.CLOSED -> "closed"
        else -> null
    }

    private fun size(value: Int?) = (value ?: 100).coerceIn(1, 100)
}
