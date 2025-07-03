package com.tencent.devops.scm.provider.git.tgit

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.devops.scm.api.enums.IssueState
import com.tencent.devops.scm.api.pojo.Issue
import com.tencent.devops.scm.api.pojo.IssueListOptions
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.sdk.tgit.TGitIssuesApi
import com.tencent.devops.scm.sdk.tgit.TGitNotesApi
import com.tencent.devops.scm.sdk.tgit.enums.TGitIssueState
import com.tencent.devops.scm.sdk.tgit.pojo.TGitIssue
import com.tencent.devops.scm.sdk.tgit.pojo.TGitNote
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class TGitIssueServiceTest : AbstractTGitServiceTest() {

    companion object {
        private const val TEST_ISSUE_ID = 58870L
        private const val TEST_ISSUE_NUMBER = 1
        private const val TEST_ISSUE_NOTE_ID = 5257868L
    }

    private lateinit var issueService: TGitIssueService

    init {
        `when`(tGitApi.getIssuesApi()).thenReturn(mock(TGitIssuesApi::class.java))
        val issuesApi = tGitApi.getIssuesApi()
        `when`(tGitApi.getNotesApi()).thenReturn(mock(TGitNotesApi::class.java))
        val notesApi = tGitApi.getNotesApi()

        `when`(issuesApi.getIssue(TEST_PROJECT_NAME, TEST_ISSUE_ID))
                .thenReturn(read("get_issue.json", TGitIssue::class.java))
        `when`(issuesApi.getIssueByIid(TEST_PROJECT_NAME, TEST_ISSUE_NUMBER))
                .thenReturn(read("get_issue.json", TGitIssue::class.java))
        `when`(issuesApi.getIssues(TEST_PROJECT_NAME, TGitIssueState.OPENED, null, null))
                .thenReturn(read("list_issues.json", object : TypeReference<List<TGitIssue>>() {}))
        `when`(notesApi.getIssueNote(TEST_PROJECT_NAME, TEST_ISSUE_ID, TEST_ISSUE_NOTE_ID))
                .thenReturn(read("get_issue_note.json", TGitNote::class.java))
        `when`(notesApi.getIssueNotes(TEST_PROJECT_NAME, TEST_ISSUE_ID, null, null))
                .thenReturn(read("list_issue_notes.json", object : TypeReference<List<TGitNote>>() {}))

        issueService = TGitIssueService(apiFactory)
    }

    @Test
    fun testFind() {
        val issue = issueService.find(providerRepository, TEST_ISSUE_NUMBER)
        assertIssue(issue)
    }

    @Test
    fun testList() {

        val issues = issueService.list(
            providerRepository,
            IssueListOptions(
                state = IssueState.OPENED
            )
        )
        Assertions.assertEquals(1, issues.size)
        assertIssue(issues[0])
    }

    @Test
    fun testFindComment() {
        val comment = issueService.findComment(providerRepository, TEST_ISSUE_NUMBER, TEST_ISSUE_NOTE_ID)

        Assertions.assertEquals(5257868L, comment.id)
        Assertions.assertEquals("issue note api", comment.body)

        val user = comment.author
        Assertions.assertEquals(132102L, user.id)
        Assertions.assertEquals("mingshewhe", user.username)
    }

    @Test
    fun listComments() {
        val comments = issueService.listComments(providerRepository, TEST_ISSUE_NUMBER, ListOptions())
        Assertions.assertEquals(12, comments.size)

        val lastComment = comments[11]
        Assertions.assertEquals(5257868L, lastComment.id)
        Assertions.assertEquals("issue note api", lastComment.body)
    }

    private fun assertIssue(issue: Issue) {
        Assertions.assertEquals(58870L, issue.id)
        Assertions.assertEquals(1, issue.number)
        Assertions.assertEquals("issue api测试", issue.title)
        Assertions.assertEquals("", issue.body)
        Assertions.assertFalse(issue.closed)

        Assertions.assertEquals(1, issue.labels?.size)

        val user = issue.author
        Assertions.assertEquals(132102L, user.id)
        Assertions.assertEquals("mingshewhe", user.username)
    }
}
