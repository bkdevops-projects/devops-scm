package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.enums.CheckRunStatus
import com.tencent.devops.scm.api.exception.ScmApiException
import com.tencent.devops.scm.api.pojo.CheckRunInput
import com.tencent.devops.scm.api.pojo.ContentInput
import com.tencent.devops.scm.api.pojo.HookInput
import com.tencent.devops.scm.api.pojo.IssueInput
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.PullRequestInput
import com.tencent.devops.scm.api.pojo.ReferenceInput
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.sdk.gitlab.GitlabApi
import com.tencent.devops.scm.sdk.gitlab.GitlabApiFactory
import com.tencent.devops.scm.sdk.gitlab.GitlabCommitStatusesApi
import com.tencent.devops.scm.sdk.gitlab.GitlabCommitsApi
import com.tencent.devops.scm.sdk.gitlab.GitlabIssuesApi
import com.tencent.devops.scm.sdk.gitlab.GitlabMergeRequestsApi
import com.tencent.devops.scm.sdk.gitlab.GitlabOauth2Api
import com.tencent.devops.scm.sdk.gitlab.GitlabProjectsApi
import com.tencent.devops.scm.sdk.gitlab.GitlabRepositoryFilesApi
import com.tencent.devops.scm.sdk.gitlab.GitlabUsersApi
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabCommit
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabCommitStatus
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabIssue
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabMergeRequest
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabMember
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabOauth2AccessToken
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabProject
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabRepositoryFile
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabTreeItem
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.Date

class GitlabServicesTest {
    private val auth = PersonalAccessTokenScmAuth("token")
    private val repository = GitScmProviderRepository(projectIdOrPath = "group/demo", auth = auth)

    @Test
    fun `repository service converts a project`() {
        val (factory, api) = api()
        val projects = mock(GitlabProjectsApi::class.java)
        `when`(api.projectsApi).thenReturn(projects)
        `when`(projects.getProject("group/demo")).thenReturn(project())
        assertEquals("group/demo", GitlabRepositoryService(factory).find(repository).fullName)
    }

    @Test
    fun `repository pull permission respects visibility and reporter level`() {
        val (factory, api) = api()
        val projects = mock(GitlabProjectsApi::class.java)
        val project = project().apply { visibility = "private" }
        val member = GitlabMember().apply { username = "dev"; accessLevel = 10 }
        `when`(api.projectsApi).thenReturn(projects)
        `when`(projects.getProject("group/demo")).thenReturn(project)
        `when`(projects.getMember("group/demo", "dev")).thenReturn(member)

        val service = GitlabRepositoryService(factory)
        assertFalse(service.findPerms(repository, "dev").pull)
        member.accessLevel = 20
        assertTrue(service.findPerms(repository, "dev").pull)
        project.visibility = "public"
        member.accessLevel = 0
        assertTrue(service.findPerms(repository, "dev").pull)
    }

    @Test
    fun `ref service converts a commit`() {
        val (factory, api) = api()
        val commits = mock(GitlabCommitsApi::class.java)
        `when`(api.commitsApi).thenReturn(commits)
        `when`(commits.getCommit("group/demo", "main")).thenReturn(commit())
        assertEquals("abc", GitlabRefService(factory).findCommit(repository, "main").sha)
    }

    @Test
    fun `pull request service uses project iid`() {
        val (factory, api) = api()
        val mergeRequests = mock(GitlabMergeRequestsApi::class.java)
        val projects = mock(GitlabProjectsApi::class.java)
        `when`(api.mergeRequestsApi).thenReturn(mergeRequests)
        `when`(api.projectsApi).thenReturn(projects)
        `when`(mergeRequests.getMergeRequest("group/demo", 17)).thenReturn(mergeRequest())
        `when`(projects.getProject(42L)).thenReturn(project())
        assertEquals(17, GitlabPullRequestService(factory).find(repository, 17).number)
        verify(mergeRequests).getMergeRequest("group/demo", 17)

        assertThrows(IllegalArgumentException::class.java) {
            GitlabPullRequestService(factory).create(repository, PullRequestInput("", null, "source", "main"))
        }
    }

    @Test
    fun `pull request changes reject overflow response`() {
        val (factory, api) = api()
        val mergeRequests = mock(GitlabMergeRequestsApi::class.java)
        `when`(api.mergeRequestsApi).thenReturn(mergeRequests)
        `when`(mergeRequests.getMergeRequestChanges("group/demo", 17)).thenReturn(
            GitlabMergeRequest().apply { overflow = true }
        )

        val error = assertThrows(ScmApiException::class.java) {
            GitlabPullRequestService(factory).listChanges(repository, 17, ListOptions())
        }
        assertTrue(error.message.orEmpty().contains("overflow"))
    }

    @Test
    fun `issue service uses project iid`() {
        val (factory, api) = api()
        val issues = mock(GitlabIssuesApi::class.java)
        `when`(api.issuesApi).thenReturn(issues)
        `when`(issues.getIssue("group/demo", 23)).thenReturn(issue())
        assertEquals(23, GitlabIssueService(factory).find(repository, 23).number)
        verify(issues).getIssue("group/demo", 23)

        assertThrows(IllegalArgumentException::class.java) {
            GitlabIssueService(factory).create(repository, IssueInput(""))
        }
    }

    @Test
    fun `file service decodes GitLab base64 content`() {
        val (factory, api) = api()
        val files = mock(GitlabRepositoryFilesApi::class.java)
        `when`(api.repositoryFilesApi).thenReturn(files)
        val file = GitlabRepositoryFile().apply {
            filePath = "README.md"
            encoding = "base64"
            content = "aGVsbG8="
            commitId = "abc"
            blobId = "blob"
        }
        `when`(files.getFile("group/demo", "README.md", "main")).thenReturn(file)
        assertEquals("hello", GitlabFileService(factory).find(repository, "README.md", "main").content)

        GitlabFileService(factory).create(repository, "new.txt", ContentInput("main", "add", "text"))
        verify(files).createFile("group/demo", "new.txt", "main", "text", "add")
    }

    @Test
    fun `file service lists every tree page`() {
        val (factory, api) = api()
        val files = mock(GitlabRepositoryFilesApi::class.java)
        `when`(api.repositoryFilesApi).thenReturn(files)
        val firstPage = (1..100).map { index ->
            GitlabTreeItem().apply {
                id = "blob-$index"
                name = "file-$index"
                path = "directory/file-$index"
            }
        }
        val secondPage = listOf(
            GitlabTreeItem().apply {
                id = "blob-101"
                name = "file-101"
                path = "directory/file-101"
            }
        )
        `when`(files.getTree("group/demo", "", "main", true, 1, 100)).thenReturn(firstPage)
        `when`(files.getTree("group/demo", "", "main", true, 2, 100)).thenReturn(secondPage)

        val trees = GitlabFileService(factory).listTree(repository, "", "main", true)

        assertEquals(101, trees.size)
        assertEquals("file-1", trees.first().path)
        verify(files).getTree("group/demo", "", "main", true, 2, 100)
    }

    @Test
    fun `file service returns paths relative to requested directory`() {
        val (factory, api) = api()
        val files = mock(GitlabRepositoryFilesApi::class.java)
        `when`(api.repositoryFilesApi).thenReturn(files)
        val fileTrees = listOf(
            GitlabTreeItem().apply {
                id = "build-blob"
                name = "build.yml"
                path = ".ci/build.yml"
                type = "blob"
                mode = "100644"
            },
            GitlabTreeItem().apply {
                id = "template-blob"
                name = "base.yml"
                path = ".ci/templates/base.yml"
                type = "blob"
                mode = "100644"
            }
        )
        `when`(files.getTree("group/demo", ".ci", "main", true, 1, 100)).thenReturn(fileTrees)

        val trees = GitlabFileService(factory).listTree(repository, ".ci", "main", true)

        assertEquals(listOf("build.yml", "templates/base.yml"), trees.map { it.path })
    }

    @Test
    fun `ref creation rejects missing sha`() {
        val factory = mock(GitlabApiFactory::class.java)
        val service = GitlabRefService(factory)
        listOf(null, "", " ").forEach { sha ->
            assertThrows(IllegalArgumentException::class.java) {
                service.createBranch(repository, ReferenceInput("branch", sha))
            }
            assertThrows(IllegalArgumentException::class.java) {
                service.createTag(repository, ReferenceInput("tag", sha))
            }
        }
    }

    @Test
    fun `hook update rejects missing url or events`() {
        val service = GitlabRepositoryService(mock(GitlabApiFactory::class.java))
        assertThrows(IllegalArgumentException::class.java) {
            service.updateHook(repository, 1, HookInput("hook", "", events = com.tencent.devops.scm.api.pojo.HookEvents()))
        }
        assertThrows(IllegalArgumentException::class.java) {
            service.updateHook(repository, 1, HookInput("hook", "https://example.com/hook", events = null))
        }
    }

    @Test
    fun `user service converts current user`() {
        val (factory, api) = api()
        val users = mock(GitlabUsersApi::class.java)
        `when`(api.usersApi).thenReturn(users)
        `when`(users.currentUser).thenReturn(GitlabUser().apply { id = 7; username = "dev"; name = "Dev" })
        assertEquals("dev", GitlabUserService(factory).find(auth).username)
    }

    @Test
    fun `check run service maps queued status and validates ref`() {
        val (factory, api) = api()
        val statuses = mock(GitlabCommitStatusesApi::class.java)
        `when`(api.commitStatusesApi).thenReturn(statuses)
        `when`(statuses.create("group/demo", "abc", "pending", "ci", null, null, null)).thenReturn(
            GitlabCommitStatus().apply { id = 1; status = "pending"; name = "ci" }
        )
        val service = GitlabCheckRunService(factory)
        assertEquals(CheckRunStatus.QUEUED, service.create(repository, CheckRunInput(name = "ci", ref = "abc", status = CheckRunStatus.QUEUED)).status)
        assertThrows(IllegalArgumentException::class.java) {
            service.create(repository, CheckRunInput(name = "ci", ref = null, status = CheckRunStatus.QUEUED))
        }
    }

    @Test
    fun `token service preserves oauth fields`() {
        val oauth = mock(GitlabOauth2Api::class.java)
        `when`(oauth.callback("code")).thenReturn(GitlabOauth2AccessToken().apply {
            accessToken = "access"
            tokenType = "Bearer"
            expiresIn = 7200
            refreshToken = "refresh"
            scope = "api"
        })
        val token = GitlabTokenService(oauth).callback("code")
        assertEquals("access", token.accessToken)
        assertEquals("refresh", token.refreshToken)

        `when`(oauth.callback("missing-access")).thenReturn(GitlabOauth2AccessToken().apply { tokenType = "Bearer" })
        assertThrows(IllegalArgumentException::class.java) {
            GitlabTokenService(oauth).callback("missing-access")
        }
        `when`(oauth.refresh("missing-type")).thenReturn(GitlabOauth2AccessToken().apply { accessToken = "access" })
        assertThrows(IllegalArgumentException::class.java) {
            GitlabTokenService(oauth).refresh("missing-type")
        }
    }

    private fun api(): Pair<GitlabApiFactory, GitlabApi> {
        val factory = mock(GitlabApiFactory::class.java)
        val api = mock(GitlabApi::class.java)
        `when`(factory.fromAuthProvider(any())).thenReturn(api)
        return factory to api
    }

    private fun project() = GitlabProject().apply {
        id = 42
        name = "demo"
        pathWithNamespace = "group/demo"
        httpUrlToRepo = "https://gitlab.example.com/group/demo.git"
        sshUrlToRepo = "git@gitlab.example.com:group/demo.git"
        webUrl = "https://gitlab.example.com/group/demo"
    }

    private fun commit() = GitlabCommit().apply {
        id = "abc"
        message = "message"
        committedDate = Date(0)
    }

    private fun mergeRequest() = GitlabMergeRequest().apply {
        id = 9001
        iid = 17
        title = "MR"
        description = "body"
        state = "opened"
        sourceBranch = "feature"
        targetBranch = "main"
        sourceProjectId = 42
        targetProjectId = 42
        sha = "abc"
    }

    private fun issue() = GitlabIssue().apply {
        id = 1001
        iid = 23
        title = "Issue"
        state = "opened"
        createdAt = Date(0)
    }
}
