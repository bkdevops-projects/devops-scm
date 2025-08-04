package com.tencent.devops.scm.api

/**
 * SCM提供者接口
 */
interface ScmProvider {
    /**
     * 获取代码库服务
     */
    fun repositories(): RepositoryService

    /**
     * 获取引用服务(分支/标签/提交)
     */
    fun refs(): RefService

    /**
     * 获取问题服务
     */
    fun issues(): IssueService

    /**
     * 获取用户服务
     */
    fun users(): UserService

    /**
     * 获取文件服务
     */
    fun files(): FileService

    /**
     * 获取Webhook解析器
     */
    fun webhookParser(): WebhookParser

    /**
     * 获取Webhook增强器
     */
    fun webhookEnricher(): WebhookEnricher

    /**
     * 获取拉取请求服务
     */
    fun pullRequests(): PullRequestService

    /**
     * 获取令牌服务
     */
    fun token(): TokenService

    /**
     * 获取命令行操作服务
     */
    fun command(): ScmCommand

    fun checkRun(): CheckRunService
}
