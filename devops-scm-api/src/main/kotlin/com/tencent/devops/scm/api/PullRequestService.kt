package com.tencent.devops.scm.api

import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.Comment
import com.tencent.devops.scm.api.pojo.CommentInput
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.PullRequestInput
import com.tencent.devops.scm.api.pojo.PullRequestListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository

interface PullRequestService {
    fun find(repository: ScmProviderRepository, number: Int): PullRequest

    fun list(repository: ScmProviderRepository, opts: PullRequestListOptions): List<PullRequest>

    fun create(repository: ScmProviderRepository, input: PullRequestInput): PullRequest

    fun listChanges(repository: ScmProviderRepository, number: Int, opts: ListOptions): List<Change>

    fun listCommits(repository: ScmProviderRepository, number: Int, opts: ListOptions): List<Commit>

    fun merge(repository: ScmProviderRepository, number: Int)

    fun close(repository: ScmProviderRepository, number: Int)

    fun findComment(repository: ScmProviderRepository, number: Int, commentId: Long): Comment

    fun listComments(repository: ScmProviderRepository, number: Int, opts: ListOptions): List<Comment>

    fun createComment(repository: ScmProviderRepository, number: Int, input: CommentInput): Comment

    fun deleteComment(repository: ScmProviderRepository, number: Int, commentId: Long)
}
