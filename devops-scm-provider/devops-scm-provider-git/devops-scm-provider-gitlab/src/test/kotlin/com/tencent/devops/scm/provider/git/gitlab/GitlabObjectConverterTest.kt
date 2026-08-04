package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.enums.CheckRunConclusion
import com.tencent.devops.scm.api.enums.CheckRunStatus
import com.tencent.devops.scm.api.enums.Visibility
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabProject
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabCommitStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class GitlabObjectConverterTest {
    @Test
    fun `converts namespaced project without deriving it from tgit urls`() {
        val project = GitlabProject().apply {
            id = 42
            name = "demo"
            pathWithNamespace = "group/subgroup/demo"
            archived = true
            visibility = "internal"
            httpUrlToRepo = "https://gitlab.example.com/group/subgroup/demo.git"
            sshUrlToRepo = "git@gitlab.example.com:group/subgroup/demo.git"
            webUrl = "https://gitlab.example.com/group/subgroup/demo"
        }
        val converted = GitlabObjectConverter.convertRepository(project)
        assertEquals("group/subgroup", converted.group)
        assertEquals("group/subgroup/demo", converted.fullName)
        assertEquals(true, converted.archived)
        assertEquals(Visibility.INTERNAL, converted.visibility)
    }

    @Test
    fun `maps all supported commit status outcomes`() {
        assertEquals("pending", GitlabObjectConverter.convertCheckRunState(CheckRunStatus.QUEUED, null))
        assertEquals("running", GitlabObjectConverter.convertCheckRunState(CheckRunStatus.IN_PROGRESS, null))
        assertEquals(
            "canceled",
            GitlabObjectConverter.convertCheckRunState(CheckRunStatus.COMPLETED, CheckRunConclusion.CANCELLED)
        )
        assertThrows(IllegalArgumentException::class.java) {
            GitlabObjectConverter.convertCheckRunState(CheckRunStatus.COMPLETED, null)
        }
        assertThrows(IllegalArgumentException::class.java) {
            GitlabObjectConverter.convertCheckRunState(CheckRunStatus.COMPLETED, CheckRunConclusion.UNKNOWN)
        }
    }

    @Test
    fun `rejects unknown GitLab commit status`() {
        val status = GitlabCommitStatus().apply { this.status = "new-status" }
        assertThrows(IllegalArgumentException::class.java) {
            GitlabObjectConverter.convertCheckRun(status)
        }
    }
}
