package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.pojo.HookRequest
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook
import com.tencent.devops.scm.api.pojo.webhook.git.GitTagHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class GitlabWebhookParserTest {
    private val parser = GitlabWebhookParser()

    @Test
    fun `parses push create and delete actions`() {
        val pushed = parse("gitlab_push_event.json", "Push Hook", GitPushHook::class.java)
        assertEquals(EventAction.PUSH_FILE, pushed.action)
        assertEquals("PUSH", pushed.eventType)
        assertFalse(pushed.skipCi())
        assertEquals(setOf("new.txt", "README.md"), pushed.changes.map { it.path }.toSet())
        assertTrue(pushed.changes.single { it.path == "new.txt" }.added)
        assertFalse(pushed.changes.single { it.path == "README.md" }.added)
        assertEquals("push-file", pushed.outputs()["GIT_CI_ACTION"])
        assertFalse(pushed.outputs().containsKey("BK_REPO_GIT_WEBHOOK_PUSH_ACTION_KIND"))
        assertFalse(pushed.outputs().containsKey("BK_REPO_GIT_WEBHOOK_PUSH_OPERATION_KIND"))
        assertFalse(pushed.outputs().containsKey("BK_CI_REPO_GIT_MANUAL_UNLOCK"))
        val created = parse("gitlab_branch_create_event.json", "Push Hook", GitPushHook::class.java)
        assertEquals(EventAction.NEW_BRANCH, created.action)
        assertEquals("new-branch", created.outputs()["GIT_CI_ACTION"])
        val deleted = parse("gitlab_branch_delete_event.json", "Push Hook", GitPushHook::class.java)
        assertEquals(EventAction.DELETE, deleted.action)
        assertNull(deleted.commit)
        assertTrue(deleted.skipCi())
    }

    @Test
    fun `parses changes from commits when creating branch`() {
        val hook = parseBody(branchCreateWithCommitsBody(), "Push Hook") as GitPushHook

        assertEquals(EventAction.NEW_BRANCH, hook.action)
        assertEquals(3, hook.changes.size)
        assertTrue(hook.changes.single { it.path == ".ci/templates/base.yml" }.added)
        assertFalse(hook.changes.single { it.path == ".ci/pipeline.yml" }.added)
        assertTrue(hook.changes.single { it.path == ".ci/old.yml" }.deleted)
        assertEquals(".ci/old.yml", hook.changes.single { it.path == ".ci/old.yml" }.oldPath)
        assertEquals("second", hook.commit?.message)
        assertEquals("second", hook.outputs()["BK_CI_HOOK_MESSAGE"])
        assertEquals("new-branch", hook.outputs()["GIT_CI_ACTION"])
    }

    @Test
    fun `parses tag actions`() {
        val created = parse("gitlab_tag_push_event.json", "Tag Push Hook", GitTagHook::class.java)
        assertEquals(EventAction.CREATE, created.action)
        assertEquals("TAG_PUSH", created.eventType)
        assertEquals("Dev User", created.outputs()["GIT_CI_COMMIT_AUTHOR"])
        assertEquals("Release 1.0.0", created.outputs()["GIT_CI_COMMIT_MESSAGE"])
        assertEquals("0000000000000000000000000000000000000000", created.outputs()["GIT_CI_BEFORE_SHA"])
        val deleted = parse("gitlab_tag_delete_event.json", "Tag Push Hook", GitTagHook::class.java)
        assertEquals(EventAction.DELETE, deleted.action)
        assertFalse(deleted.skipCi())
    }

    @Test
    fun `parses merge request issue and note hooks`() {
        val opened = parse("gitlab_mr_open_event.json", "Merge Request Hook", PullRequestHook::class.java)
        assertEquals(EventAction.OPEN, opened.action)
        assertEquals("MERGE_REQUEST", opened.eventType)
        val outputs = opened.outputs()
        assertEquals(42L, outputs["BK_CI_HOOK_SOURCE_PROJECT_ID"])
        assertEquals(42L, outputs["BK_CI_HOOK_TARGET_PROJECT_ID"])
        assertEquals("feature/gitlab", outputs["BK_CI_HOOK_SOURCE_BRANCH"])
        assertEquals("main", outputs["BK_CI_HOOK_TARGET_BRANCH"])
        assertEquals(9001L, outputs["GIT_CI_MR_ID"])
        assertEquals("dev", outputs["GIT_CI_MR_PROPOSER"])
        assertEquals(EventAction.PUSH_UPDATE, parse("gitlab_mr_update_event.json", "Merge Request Hook", PullRequestHook::class.java).action)
        assertEquals(EventAction.MERGE, parse("gitlab_mr_merge_event.json", "Merge Request Hook", PullRequestHook::class.java).action)
        val issue = assertInstanceOf(IssueHook::class.java, parseRaw("gitlab_issue_event.json", "Issue Hook"))
        assertEquals("ISSUES", issue.eventType)
        assertEquals(EventAction.OPEN, issue.action)
        assertEquals("open", issue.outputs()["BK_CI_REPO_GIT_WEBHOOK_ISSUE_ACTION"])
        assertEquals("dev", issue.outputs()["BK_CI_REPO_GIT_WEBHOOK_ISSUE_OWNER"])
        val mrNote = assertInstanceOf(
            PullRequestCommentHook::class.java,
            parseRaw("gitlab_mr_note_event.json", "Note Hook")
        )
        assertEquals("NOTE", mrNote.eventType)
        val issueNote = assertInstanceOf(
            IssueCommentHook::class.java,
            parseRaw("gitlab_issue_note_event.json", "Note Hook")
        )
        assertEquals("NOTE", issueNote.eventType)
    }

    @Test
    fun `verifies token and rejects conflicting event header`() {
        val request = HookRequest(mapOf("X-Gitlab-Token" to "secret"), fixture("gitlab_push_event.json"))
        assertTrue(parser.verify(request, null))
        assertTrue(parser.verify(request, "secret"))
        assertFalse(parser.verify(request, "other"))

        request.headers = mapOf("X-Gitlab-Event" to "Issue Hook")
        assertNull(parser.parse(request))
    }

    @Test
    fun `maps merge request action variants and push updates`() {
        listOf("approved", "approval").forEach { action ->
            val hook = parseBody(mergeRequestBody(action), "Merge Request Hook") as PullRequestHook
            assertEquals(EventAction.EDIT, hook.action)
            assertEquals("approved", hook.outputs()["BK_CI_REPO_GIT_WEBHOOK_MR_ACTION"])
            assertEquals(action, hook.outputs()["GIT_CI_ACTION"])
        }
        listOf("unapproved", "unapproval").forEach { action ->
            val hook = parseBody(mergeRequestBody(action), "Merge Request Hook") as PullRequestHook
            assertEquals(EventAction.EDIT, hook.action)
        }
        val pushUpdate = parseBody(mergeRequestBody("update", "\"oldrev\":\"abc\","), "Merge Request Hook")
            as PullRequestHook
        assertEquals(EventAction.PUSH_UPDATE, pushUpdate.action)
    }

    @Test
    fun `maps note update and defaults old payload to create`() {
        val updated = parseBody(noteBody("\"action\":\"update\","), "Note Hook") as IssueCommentHook
        assertEquals(EventAction.UPDATE, updated.action)
        val legacy = parseBody(noteBody(""), "Note Hook") as IssueCommentHook
        assertEquals(EventAction.CREATE, legacy.action)
    }

    @Test
    fun `uses payload authors before sender`() {
        val mr = parseBody(
            mergeRequestBody("open", "\"author\":{\"id\":99,\"username\":\"author\",\"name\":\"Author\"},"),
            "Merge Request Hook"
        ) as PullRequestHook
        assertEquals(99, mr.pullRequest.author?.id)
        assertEquals("author", mr.pullRequest.author?.username)

        val issue = parseBody(issueBody("\"author_id\":88,"), "Issue Hook") as IssueHook
        assertEquals(88, issue.issue.author.id)
        assertEquals("sender", issue.issue.author.username)
    }

    @Test
    fun `supports GitLab UTC time and returns null for invalid optional time`() {
        val hook = parseBody(
            mergeRequestBody(
                "open",
                "\"created_at\":\"2026-07-13 08:10:00 UTC\",\"updated_at\":\"invalid\","
            ),
            "Merge Request Hook"
        ) as PullRequestHook
        assertEquals(LocalDateTime.of(2026, 7, 13, 8, 10), hook.pullRequest.created)
        assertNull(hook.pullRequest.updated)
    }

    @Test
    fun `builds issue link when official note payload omits it`() {
        val hook = parseBody(noteBody(""), "Note Hook") as IssueCommentHook

        assertEquals("https://gitlab.example.com/group/demo/-/issues/23", hook.issue.link)
    }

    @Test
    fun `maps nested merge request repositories`() {
        val repositories = """
            "source":{"id":41,"name":"fork","path_with_namespace":"contributor/fork",
              "git_http_url":"https://gitlab.example.com/contributor/fork.git",
              "git_ssh_url":"git@gitlab.example.com:contributor/fork.git",
              "web_url":"https://gitlab.example.com/contributor/fork"},
            "target":{"id":42,"name":"demo","path_with_namespace":"group/demo",
              "git_http_url":"https://gitlab.example.com/group/demo.git",
              "web_url":"https://gitlab.example.com/group/demo"},
        """.trimIndent()
        val hook = parseBody(
            mergeRequestBody("open", repositories).replace("\"source_project_id\":42", "\"source_project_id\":41"),
            "Merge Request Hook"
        ) as PullRequestHook

        assertEquals("contributor/fork", hook.pullRequest.sourceRepo.fullName)
        assertEquals("https://gitlab.example.com/contributor/fork.git", hook.pullRequest.sourceRepo.httpUrl)
        assertEquals("group/demo", hook.pullRequest.targetRepo.fullName)
    }

    private fun parseRaw(name: String, header: String) = parser.parse(
        HookRequest(mapOf("X-Gitlab-Event" to header), fixture(name))
    )

    private fun parseBody(body: String, header: String) = parser.parse(
        HookRequest(mapOf("X-Gitlab-Event" to header), body)
    )

    private fun <T> parse(name: String, header: String, type: Class<T>): T = type.cast(parseRaw(name, header))

    private fun fixture(name: String): String = requireNotNull(javaClass.classLoader.getResource(name)).readText()

    private fun mergeRequestBody(action: String, attributes: String = "") = """
        {
          "object_kind":"merge_request",
          "user":{"id":7,"username":"sender","name":"Sender"},
          "project":{"id":42,"name":"demo","path_with_namespace":"group/demo","web_url":"https://gitlab.example.com/group/demo"},
          "object_attributes":{
            $attributes
            "id":9001,"iid":17,"action":"$action","state":"opened","title":"MR","description":"body",
            "source_branch":"feature","target_branch":"main","source_project_id":42,"target_project_id":42,
            "last_commit":{"id":"abc","message":"message"}
          }
        }
    """.trimIndent()

    private fun issueBody(attributes: String) = """
        {
          "object_kind":"issue",
          "user":{"id":7,"username":"sender","name":"Sender"},
          "project":{"id":42,"name":"demo","path_with_namespace":"group/demo","web_url":"https://gitlab.example.com/group/demo"},
          "object_attributes":{
            $attributes
            "id":1001,"iid":23,"action":"open","state":"opened","title":"Issue",
            "created_at":"2026-07-13T08:10:00Z"
          }
        }
    """.trimIndent()

    private fun noteBody(attributes: String) = """
        {
          "object_kind":"note",
          "user":{"id":7,"username":"sender","name":"Sender"},
          "project":{"id":42,"name":"demo","path_with_namespace":"group/demo","web_url":"https://gitlab.example.com/group/demo"},
          "object_attributes":{
            $attributes
            "id":501,"note":"body","noteable_type":"Issue","created_at":"2026-07-13T08:10:00Z"
          },
          "issue":{"id":1001,"iid":23,"state":"opened","title":"Issue","created_at":"2026-07-13T08:10:00Z"}
        }
    """.trimIndent()

    private fun branchCreateWithCommitsBody() = """
        {
          "object_kind":"push",
          "before":"0000000000000000000000000000000000000000",
          "after":"3333333333333333333333333333333333333333",
          "ref":"refs/heads/feature/new",
          "checkout_sha":"3333333333333333333333333333333333333333",
          "user_id":7,"user_name":"Dev User","user_username":"dev",
          "project":{"id":42,"name":"demo","path_with_namespace":"group/demo",
            "web_url":"https://gitlab.example.com/group/demo"},
          "commits":[
            {
              "id":"2222222222222222222222222222222222222222","message":"first",
              "added":[".ci/templates/base.yml"],"modified":[".ci/pipeline.yml"],"removed":[]
            },
            {
              "id":"3333333333333333333333333333333333333333","message":"second",
              "added":[],"modified":[".ci/templates/base.yml"],"removed":[".ci/old.yml"]
            }
          ],
          "total_commits_count":2
        }
    """.trimIndent()
}
