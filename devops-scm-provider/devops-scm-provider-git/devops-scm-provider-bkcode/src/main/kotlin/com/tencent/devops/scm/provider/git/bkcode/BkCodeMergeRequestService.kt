package com.tencent.devops.scm.provider.git.bkcode

import com.tencent.devops.scm.api.PullRequestService
import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.Comment
import com.tencent.devops.scm.api.pojo.CommentInput
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.PullRequestInput
import com.tencent.devops.scm.api.pojo.PullRequestListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.bkcode.BkCodeApiFactory
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeDiffFileRevRange
import org.slf4j.LoggerFactory

/**
 * BkCode 合并请求服务实现类
 * @property apiFactory BkCode API工厂
 */
class BkCodeMergeRequestService(
    private val apiFactory: BkCodeApiFactory
) : PullRequestService {

    companion object {
        val logger = LoggerFactory.getLogger(BkCodeMergeRequestService::class.java)
    }

    /**
     * 获取指定合并请求
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     * @return 合并请求详情
     */
    override fun find(repository: ScmProviderRepository, number: Int): PullRequest {
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            val mergeRequest = bkCodeApi.mergeRequestApi.getMergeRequestByNumber(repo.projectIdOrPath, number)
            val targetProject = bkCodeApi.projectApi.getProject(repo.projectIdOrPath)
            // 授权者不一定有fork仓库的权限
            val sourceProject = if (mergeRequest.sourceRepoId == mergeRequest.targetRepoId) {
                targetProject
            } else {
                try {
                    bkCodeApi.projectApi.getProject(mergeRequest.sourceRepoId)
                } catch (e: Exception) {
                    logger.warn("get source project[${mergeRequest.sourceRepoId}] failed", e)
                    null
                }
            }
            BkCodeObjectConverter.convertPullRequest(mergeRequest, sourceProject, targetProject)
        }
    }

    /**
     * 获取合并请求列表
     * @param repository 代码仓库信息
     * @param opts 列表查询选项
     * @return 合并请求列表
     */
    override fun list(repository: ScmProviderRepository, opts: PullRequestListOptions): List<PullRequest> {
        throw UnsupportedOperationException("BkCode does not support listing pull requests")
    }

    /**
     * 创建合并请求
     * @param repository 代码仓库信息
     * @param input 合并请求输入参数
     * @return 创建的合并请求详情
     * @throws IllegalArgumentException 当标题、源分支或目标分支为空时抛出
     */
    override fun create(repository: ScmProviderRepository, input: PullRequestInput): PullRequest {
        throw UnsupportedOperationException("BkCode does not support creating pull requests")
    }

    /**
     * 获取合并请求变更列表
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     * @param opts 列表查询选项
     * @return 变更列表
     */
    override fun listChanges(repository: ScmProviderRepository, number: Int, opts: ListOptions): List<Change> {
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            val mergeRequest = bkCodeApi.mergeRequestApi.getMergeRequestByNumber(repo.projectIdOrPath, number)
            val compare = bkCodeApi.commitApi.compare(
                repo.projectIdOrPath,
                mergeRequest.baseCommitId,
                mergeRequest.headCommitId,
                BkCodeDiffFileRevRange.DOUBLE_DOT,
                false
            )
            compare.diffFiles.map {
                BkCodeObjectConverter.convertChange(it)
            }
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
        throw UnsupportedOperationException("BkCode does not support listing commits for pull requests")
    }

    /**
     * 合并指定合并请求
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     */
    override fun merge(repository: ScmProviderRepository, number: Int) {
        throw UnsupportedOperationException("BkCode does not support merging pull requests")
    }

    /**
     * 关闭指定合并请求
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     */
    override fun close(repository: ScmProviderRepository, number: Int) {
        throw UnsupportedOperationException("BkCode does not support closing pull requests")
    }

    /**
     * 获取合并请求评论
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     * @param commentId 评论ID
     * @return 评论详情
     */
    override fun findComment(repository: ScmProviderRepository, number: Int, commentId: Long): Comment {
        throw UnsupportedOperationException("BkCode does not support fetching pull request comments")
    }

    /**
     * 获取合并请求评论列表
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     * @param opts 列表查询选项
     * @return 评论列表
     */
    override fun listComments(repository: ScmProviderRepository, number: Int, opts: ListOptions): List<Comment> {
        throw UnsupportedOperationException("BkCode does not support listing pull request comments")
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
        throw UnsupportedOperationException("BkCode does not support creating pull request comments")
    }

    /**
     * 删除合并请求评论
     * @param repository 代码仓库信息
     * @param number 合并请求编号
     * @param commentId 评论ID
     */
    override fun deleteComment(repository: ScmProviderRepository, number: Int, commentId: Long) {
        throw UnsupportedOperationException("BkCode does not support deleting pull request comments")
    }
}