package com.tencent.devops.scm.api.pojo.webhook

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.tencent.devops.scm.api.pojo.ScmI18Variable
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository
import com.tencent.devops.scm.api.pojo.webhook.git.CommitCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook
import com.tencent.devops.scm.api.pojo.webhook.git.GitTagHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestReviewHook
import com.tencent.devops.scm.api.pojo.webhook.svn.PostCommitHook

/**
 * Webhook接口定义
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
    JsonSubTypes.Type(value = GitPushHook::class, name = GitPushHook.CLASS_TYPE),
    JsonSubTypes.Type(value = GitTagHook::class, name = GitTagHook.CLASS_TYPE),
    JsonSubTypes.Type(value = PullRequestHook::class, name = PullRequestHook.CLASS_TYPE),
    JsonSubTypes.Type(value = IssueHook::class, name = IssueHook.CLASS_TYPE),
    JsonSubTypes.Type(value = CommitCommentHook::class, name = CommitCommentHook.CLASS_TYPE),
    JsonSubTypes.Type(value = IssueCommentHook::class, name = IssueCommentHook.CLASS_TYPE),
    JsonSubTypes.Type(value = PullRequestCommentHook::class, name = PullRequestCommentHook.CLASS_TYPE),
    JsonSubTypes.Type(value = PullRequestReviewHook::class, name = PullRequestReviewHook.CLASS_TYPE),
    JsonSubTypes.Type(value = PostCommitHook::class, name = PostCommitHook.CLASS_TYPE)
)
interface Webhook {
    /**
     * 获取触发事件源，服务端代码库信息
     */
    fun repository(): ScmServerRepository

    /**
     * 获取触发用户名
     */
    @get:JsonIgnore
    val userName: String

    /**
     * 获取事件类型
     */
    val eventType: String

    /**
     * 获取事件描述
     */
    @get:JsonIgnore
    val eventDesc: ScmI18Variable

    /**
     * 获取webhook事件输出的参数
     */
    fun outputs(): Map<String, Any>

    /**
     * 是否跳过当前事件
     * 针对webhook进行校验，如果为true，则为无效事件，不进行后续流水线触发
     */
    fun skipCi(): Boolean = false
}
