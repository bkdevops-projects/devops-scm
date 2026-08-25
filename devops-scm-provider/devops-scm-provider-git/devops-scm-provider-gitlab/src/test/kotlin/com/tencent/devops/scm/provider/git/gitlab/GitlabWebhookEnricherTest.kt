package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.pojo.HookRequest
import com.tencent.devops.scm.api.constant.WebhookOutputCode.CI_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BASE_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_HEAD_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_TAG_DESC
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook
import com.tencent.devops.scm.api.pojo.webhook.git.GitTagHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook
import com.tencent.devops.scm.sdk.gitlab.GitlabApi
import com.tencent.devops.scm.sdk.gitlab.GitlabApiFactory
import com.tencent.devops.scm.sdk.gitlab.GitlabCommitsApi
import com.tencent.devops.scm.sdk.gitlab.GitlabMergeRequestsApi
import com.tencent.devops.scm.sdk.gitlab.GitlabProjectsApi
import com.tencent.devops.scm.sdk.gitlab.GitlabTagsApi
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabCommit
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabCompareResults
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabDiff
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabDiffRefs
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabMergeRequest
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabMilestone
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabProject
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabTag
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import java.util.Date

class GitlabWebhookEnricherTest {
    private val parser = GitlabWebhookParser()
    private val repository = GitScmProviderRepository(
        projectIdOrPath = "group/demo",
        auth = PersonalAccessTokenScmAuth("token")
    )

    @Test
    fun `enriches pull request changes and complete variables`() {
        val mocks = apiMocks()
        val hook = parseFixture("gitlab_mr_open_event.json", "Merge Request Hook") as PullRequestHook
        val mr = mergeRequest().apply { changes = listOf(diff()) }
        `when`(mocks.mergeRequests.getMergeRequestChanges("group/demo", 17)).thenReturn(mr)

        val enriched = GitlabWebhookEnricher(mocks.factory).enrich(repository, hook) as PullRequestHook

        assertSame(hook, enriched)
        assertEquals("src/Main.kt", enriched.changes.single().path)
        assertEquals("backend,urgent", enriched.outputs()["BK_CI_REPO_GIT_WEBHOOK_MR_LABELS"])
        assertEquals("reviewer", enriched.outputs()["BK_CI_REPO_GIT_WEBHOOK_MR_REVIEWERS"])
        assertEquals("base-sha", enriched.outputs()["BK_REPO_GIT_WEBHOOK_MR_BASE_COMMIT"])
        assertEquals("head-sha", enriched.outputs()["BK_REPO_GIT_WEBHOOK_MR_SOURCE_COMMIT"])
        assertEquals(false, enriched.repo.archived)
    }

    @Test
    fun `enriches issue and merge request comment with default branch variables`() {
        val mocks = apiMocks()
        `when`(mocks.mergeRequests.getMergeRequest("group/demo", 17)).thenReturn(mergeRequest())
        val issue = parseFixture("gitlab_issue_event.json", "Issue Hook") as IssueHook

        GitlabWebhookEnricher(mocks.factory).enrich(repository, issue)

        assertEquals("main", issue.outputs()[CI_BRANCH])
        assertEquals("main-sha", issue.outputs()[PIPELINE_GIT_SHA])

        val comment = parseFixture("gitlab_mr_note_event.json", "Note Hook") as PullRequestCommentHook
        GitlabWebhookEnricher(mocks.factory).enrich(repository, comment)

        assertEquals("default message", comment.outputs()[PIPELINE_GIT_COMMIT_MESSAGE])
        assertEquals(comment.pullRequest?.sourceRef?.name, comment.outputs()[PIPELINE_GIT_HEAD_REF])
        assertEquals(comment.pullRequest?.targetRef?.name, comment.outputs()[PIPELINE_GIT_BASE_REF])
        verify(mocks.mergeRequests).getMergeRequest("group/demo", 17)
    }

    @Test
    fun `enriches push changes and tag description`() {
        val mocks = apiMocks()
        val push = parseFixture("gitlab_push_event.json", "Push Hook") as GitPushHook
        `when`(mocks.commits.compare("group/demo", push.before, push.after, true)).thenReturn(
            GitlabCompareResults().apply { diffs = listOf(diff()) }
        )

        val enrichedPush = GitlabWebhookEnricher(mocks.factory).enrich(repository, push) as GitPushHook

        assertEquals("src/Main.kt", enrichedPush.changes.single().path)

        val tag = parseFixture("gitlab_tag_push_event.json", "Tag Push Hook") as GitTagHook
        `when`(mocks.tags.getTag("group/demo", tag.ref.name)).thenReturn(
            GitlabTag().apply { message = "release notes" }
        )
        GitlabWebhookEnricher(mocks.factory).enrich(repository, tag)

        assertEquals("release notes", tag.outputs()[PIPELINE_GIT_TAG_DESC])
    }

    @Test
    fun `rejects incomplete comparisons and skips deleted refs`() {
        val mocks = apiMocks()
        val push = parseFixture("gitlab_push_event.json", "Push Hook") as GitPushHook
        `when`(mocks.commits.compare("group/demo", push.before, push.after, true)).thenReturn(
            GitlabCompareResults().apply { isCompareTimeout = true }
        )
        assertThrows(RuntimeException::class.java) {
            GitlabWebhookEnricher(mocks.factory).enrich(repository, push)
        }

        val deletedTag = parseFixture("gitlab_tag_delete_event.json", "Tag Push Hook") as GitTagHook
        GitlabWebhookEnricher(mocks.factory).enrich(repository, deletedTag)
        verifyNoInteractions(mocks.tags)
    }

    private fun apiMocks(): ApiMocks {
        val factory = mock(GitlabApiFactory::class.java)
        val api = mock(GitlabApi::class.java)
        val projects = mock(GitlabProjectsApi::class.java)
        val commits = mock(GitlabCommitsApi::class.java)
        val mergeRequests = mock(GitlabMergeRequestsApi::class.java)
        val tags = mock(GitlabTagsApi::class.java)
        `when`(factory.fromAuthProvider(any())).thenReturn(api)
        `when`(api.projectsApi).thenReturn(projects)
        `when`(api.commitsApi).thenReturn(commits)
        `when`(api.mergeRequestsApi).thenReturn(mergeRequests)
        `when`(api.tagsApi).thenReturn(tags)
        `when`(projects.getProject("group/demo")).thenReturn(project())
        `when`(commits.getCommit("group/demo", "main")).thenReturn(defaultCommit())
        return ApiMocks(factory, projects, commits, mergeRequests, tags)
    }

    private fun project() = GitlabProject().apply {
        id = 42
        name = "demo"
        pathWithNamespace = "group/demo"
        defaultBranch = "main"
        archived = false
        httpUrlToRepo = "https://gitlab.example.com/group/demo.git"
        webUrl = "https://gitlab.example.com/group/demo"
    }

    private fun defaultCommit() = GitlabCommit().apply {
        id = "main-sha"
        shortId = "main-sha"
        message = "default message"
        authorName = "Developer"
    }

    private fun mergeRequest() = GitlabMergeRequest().apply {
        id = 9001
        iid = 17
        title = "MR from API"
        description = "body"
        sourceBranch = "feature"
        targetBranch = "main"
        sourceProjectId = 42
        targetProjectId = 42
        author = GitlabUser().apply { username = "author" }
        assignee = GitlabUser().apply { username = "assignee" }
        reviewers = listOf(GitlabUser().apply { username = "reviewer" })
        labels = listOf("backend", "urgent")
        milestone = GitlabMilestone().apply { id = 3; title = "v1.2"; dueDate = Date(0) }
        createdAt = Date(0)
        updatedAt = Date(1000)
        sha = "head-sha"
        diffRefs = GitlabDiffRefs().apply {
            baseSha = "base-sha"
            startSha = "target-sha"
            headSha = "head-sha"
        }
        webUrl = "https://gitlab.example.com/group/demo/-/merge_requests/17"
    }

    private fun diff() = GitlabDiff().apply {
        oldPath = "src/Old.kt"
        newPath = "src/Main.kt"
    }

    private fun parseFixture(name: String, header: String) = parser.parse(
        HookRequest(mapOf("X-Gitlab-Event" to header), requireNotNull(javaClass.classLoader.getResource(name)).readText())
    )

    private data class ApiMocks(
        val factory: GitlabApiFactory,
        val projects: GitlabProjectsApi,
        val commits: GitlabCommitsApi,
        val mergeRequests: GitlabMergeRequestsApi,
        val tags: GitlabTagsApi
    )
}
