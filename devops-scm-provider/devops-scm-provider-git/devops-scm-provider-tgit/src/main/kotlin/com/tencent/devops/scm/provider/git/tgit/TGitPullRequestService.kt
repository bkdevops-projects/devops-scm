package com.tencent.devops.scm.provider.git.tgit

import com.tencent.devops.scm.api.PullRequestService
import com.tencent.devops.scm.api.enums.PullRequestState
import com.tencent.devops.scm.api.exception.NotFoundScmApiException
import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.Comment
import com.tencent.devops.scm.api.pojo.CommentInput
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.PullRequestInput
import com.tencent.devops.scm.api.pojo.PullRequestListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.tgit.TGitApi
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory
import com.tencent.devops.scm.sdk.tgit.enums.TGitMergeRequestState
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequest
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequestFilter
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequestParams
import org.apache.commons.lang3.StringUtils

/**
 * TGit 合并请求服务实现类
 * @property apiFactory TGit API工厂
 */
class TGitPullRequestService(
    private val apiFactory: TGitApiFactory
) : PullRequestService {

    /**
     * 获取指定合并请求
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     * @return 合并请求详情
     */
    override fun find(repository: ScmProviderRepository, number: Int): PullRequest {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val mergeRequest = tGitApi.mergeRequestApi.getMergeRequestByIid(repo.projectIdOrPath, number)
            val targetProject = tGitApi.projectApi.getProject(repo.projectIdOrPath)
            // 授权者不一定有fork仓库的权限
            val sourceProject = if (mergeRequest.targetProjectId == mergeRequest.sourceProjectId) targetProject else null
            TGitObjectConverter.convertPullRequest(mergeRequest, sourceProject, targetProject)
        }
    }

    /**
     * 获取合并请求列表
     * @param repository 代码仓库信息
     * @param opts 列表查询选项
     * @return 合并请求列表
     */
    override fun list(repository: ScmProviderRepository, opts: PullRequestListOptions): List<PullRequest> {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val filter = TGitMergeRequestFilter.builder()
                    .state(convertFromState(opts.state))
                    .sourceBranch(opts.sourceBranch)
                    .targetBranch(opts.targetBranch)
                    .page(opts.page)
                    .perPage(opts.pageSize)
                    .build()
            val mergeRequests = tGitApi.mergeRequestApi.getMergeRequests(repo.projectIdOrPath, filter)
            val targetProject = tGitApi.projectApi.getProject(repo.projectIdOrPath)
            mergeRequests.map { mergeRequest ->
                TGitObjectConverter.convertPullRequest(mergeRequest, null, targetProject)
            }
        }
    }

    /**
     * 创建合并请求
     * @param repository 代码仓库信息
     * @param input 合并请求输入参数
     * @return 创建的合并请求详情
     * @throws IllegalArgumentException 当标题、源分支或目标分支为空时抛出
     */
    override fun create(repository: ScmProviderRepository, input: PullRequestInput): PullRequest {
        if (StringUtils.isEmpty(input.title)) {
            throw IllegalArgumentException("title cannot be empty")
        }
        if (StringUtils.isEmpty(input.sourceBranch)) {
            throw IllegalArgumentException("source branch cannot be empty")
        }
        if (StringUtils.isEmpty(input.targetBranch)) {
            throw IllegalArgumentException("target branch cannot be empty")
        }

        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val builder = TGitMergeRequestParams.builder()
                    .title(input.title)
                    .description(input.body)
                    .sourceBranch(input.sourceBranch)
                    .targetBranch(input.targetBranch)

            if (input.targetRepo is Long) {
                builder.targetProjectId(input.targetRepo as Long)
            }

            val mergeRequest = tGitApi.mergeRequestApi.createMergeRequest(repo.projectIdOrPath, builder.build())
            val targetProject = tGitApi.projectApi.getProject(mergeRequest.targetProjectId)
            val sourceProject = if (mergeRequest.targetProjectId == mergeRequest.sourceProjectId) {
                targetProject
            } else {
                tGitApi.projectApi.getProject(mergeRequest.sourceProjectId)
            }
            TGitObjectConverter.convertPullRequest(mergeRequest, sourceProject, targetProject)
        }
    }

    /**
     * 获取合并请求变更列表
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     * @param opts 列表查询选项
     * @return 变更列表
     */
    override fun listChanges(repository: ScmProviderRepository, number: Int, opts: ListOptions): List<Change> {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val mergeRequest = getMergeRequestByIid(tGitApi, repo.projectIdOrPath, number)
            val mergeRequestChanges = tGitApi.mergeRequestApi
                    .getMergeRequestChanges(repo.projectIdOrPath, mergeRequest.id)
            mergeRequestChanges.files.map { TGitObjectConverter.convertChange(it) }
        }
    }

    /**
     * 获取合并请求提交列表
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     * @param opts 列表查询选项
     * @return 提交列表
     */
    override fun listCommits(repository: ScmProviderRepository, number: Int, opts: ListOptions): List<Commit> {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val mergeRequest = getMergeRequestByIid(tGitApi, repo.projectIdOrPath, number)
            tGitApi.mergeRequestApi.getMergeRequestCommits(
                repo.projectIdOrPath,
                mergeRequest.id,
                opts.page,
                opts.pageSize
            ).map { TGitObjectConverter.convertCommit(it) }
        }
    }

    /**
     * 合并指定合并请求
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     */
    override fun merge(repository: ScmProviderRepository, number: Int) {
        TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val mergeRequest = getMergeRequestByIid(tGitApi, repo.projectIdOrPath, number)
            tGitApi.mergeRequestApi.mergeMergeRequest(repo.projectIdOrPath, mergeRequest.id)
        }
    }

    /**
     * 关闭指定合并请求
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     */
    override fun close(repository: ScmProviderRepository, number: Int) {
        TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val mergeRequest = getMergeRequestByIid(tGitApi, repo.projectIdOrPath, number)
            tGitApi.mergeRequestApi.closeMergeRequest(repo.projectIdOrPath, mergeRequest.id)
        }
    }

    /**
     * 获取合并请求评论
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     * @param commentId 评论ID
     * @return 评论详情
     */
    override fun findComment(repository: ScmProviderRepository, number: Int, commentId: Long): Comment {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val mergeRequest = getMergeRequestByIid(tGitApi, repo.projectIdOrPath, number)
            val note = tGitApi.notesApi.getMergeRequestNote(repo.projectIdOrPath, mergeRequest.id, commentId)
            TGitObjectConverter.convertComment(note)
        }
    }

    /**
     * 获取合并请求评论列表
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     * @param opts 列表查询选项
     * @return 评论列表
     */
    override fun listComments(repository: ScmProviderRepository, number: Int, opts: ListOptions): List<Comment> {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val mergeRequest = getMergeRequestByIid(tGitApi, repo.projectIdOrPath, number)
            tGitApi.notesApi.getMergeRequestNotes(
                repo.projectIdOrPath,
                mergeRequest.id,
                opts.page,
                opts.pageSize
            ).map { TGitObjectConverter.convertComment(it) }
        }
    }

    /**
     * 创建合并请求评论
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     * @param input 评论输入参数
     * @return 创建的评论详情
     * @throws IllegalArgumentException 当评论内容为空时抛出
     */
    override fun createComment(repository: ScmProviderRepository, number: Int, input: CommentInput): Comment {
        if (StringUtils.isBlank(input.body)) {
            throw IllegalArgumentException("body cannot be empty")
        }
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val mergeRequest = getMergeRequestByIid(tGitApi, repo.projectIdOrPath, number)
            val note = tGitApi.notesApi.createMergeRequestNote(repo.projectIdOrPath, mergeRequest.id, input.body)
            TGitObjectConverter.convertComment(note)
        }
    }

    /**
     * 删除合并请求评论
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     * @param commentId 评论ID
     */
    override fun deleteComment(repository: ScmProviderRepository, number: Int, commentId: Long) {
        // 待实现
    }

    /**
     * 转换合并请求状态枚举
     * @param from 源状态枚举
     * @return 目标状态枚举
     */
    private fun convertFromState(from: PullRequestState?): TGitMergeRequestState? {
        return when (from) {
            PullRequestState.OPENED -> TGitMergeRequestState.OPENED
            PullRequestState.REOPENED -> TGitMergeRequestState.REOPENED
            PullRequestState.MERGED -> TGitMergeRequestState.MERGED
            PullRequestState.CLOSED -> TGitMergeRequestState.CLOSED
            else -> null
        }
    }

    /**
     * 根据编号获取合并请求
     * @param tGitApi TGit API实例
     * @param projectIdOrPath 项目ID或路径
     * @param number 合并请求编号
     * @return 合并请求详情
     * @throws NotFoundScmApiException 当合并请求不存在时抛出
     */
    private fun getMergeRequestByIid(tGitApi: TGitApi, projectIdOrPath: Any, number: Int): TGitMergeRequest {
        return tGitApi.mergeRequestApi.getMergeRequestByIid(projectIdOrPath, number)
            ?: throw NotFoundScmApiException("merge request $number not found")
    }
}
