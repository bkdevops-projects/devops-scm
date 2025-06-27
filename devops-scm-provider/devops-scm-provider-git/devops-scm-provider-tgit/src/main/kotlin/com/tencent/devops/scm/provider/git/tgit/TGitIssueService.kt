package com.tencent.devops.scm.provider.git.tgit

import com.tencent.devops.scm.api.IssueService
import com.tencent.devops.scm.api.enums.IssueState
import com.tencent.devops.scm.api.exception.ScmApiException
import com.tencent.devops.scm.api.pojo.Comment
import com.tencent.devops.scm.api.pojo.CommentInput
import com.tencent.devops.scm.api.pojo.Issue
import com.tencent.devops.scm.api.pojo.IssueInput
import com.tencent.devops.scm.api.pojo.IssueListOptions
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.tgit.TGitApi
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory
import com.tencent.devops.scm.sdk.tgit.enums.TGitIssueState
import com.tencent.devops.scm.sdk.tgit.pojo.TGitIssue
import com.tencent.devops.scm.sdk.tgit.pojo.TGitNote
import org.apache.commons.lang3.StringUtils

/**
 * TGit 问题服务实现类
 * @property apiFactory TGit API工厂
 */
class TGitIssueService(
    private val apiFactory: TGitApiFactory
) : IssueService {

    /**
     * 获取指定问题
     * @param repository 代码仓库信息
     * @param number 问题编号
     * @return 问题详情
     */
    override fun find(repository: ScmProviderRepository, number: Int): Issue {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val issue = tGitApi.issuesApi.getIssueByIid(repo.projectIdOrPath, number)
            TGitObjectConverter.convertIssue(issue)
        }
    }

    /**
     * 创建问题
     * @param repository 代码仓库信息
     * @param input 问题输入参数
     * @return 创建的问题详情
     * @throws IllegalArgumentException 当标题为空时抛出
     */
    override fun create(repository: ScmProviderRepository, input: IssueInput): Issue {
        if (StringUtils.isBlank(input.title)) {
            throw IllegalArgumentException("issue title cannot be empty")
        }
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val issue = tGitApi.issuesApi.createIssue(repo.projectIdOrPath, input.title, input.body)
            TGitObjectConverter.convertIssue(issue)
        }
    }

    /**
     * 获取问题列表
     * @param repository 代码仓库信息
     * @param opts 列表查询选项
     * @return 问题列表
     */
    override fun list(repository: ScmProviderRepository, opts: IssueListOptions): List<Issue> {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            tGitApi.issuesApi.getIssues(
                repo.projectIdOrPath,
                convertFromState(opts.state),
                opts.page,
                opts.pageSize
            ).map { TGitObjectConverter.convertIssue(it) }
        }
    }

    /**
     * 关闭问题
     * @param repository 代码仓库信息
     * @param number 问题编号
     */
    override fun close(repository: ScmProviderRepository, number: Int) {
        TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val issue = getIssueIdByNumber(tGitApi, repo.projectIdOrPath, number)
            tGitApi.issuesApi.closeIssue(repo.projectIdOrPath, issue.id)
        }
    }

    /**
     * 获取问题评论
     * @param repository 代码仓库信息
     * @param number 问题编号
     * @param commentId 评论ID
     * @return 评论详情
     */
    override fun findComment(repository: ScmProviderRepository, number: Int, commentId: Long): Comment {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val issue = getIssueIdByNumber(tGitApi, repo.projectIdOrPath, number)
            val note = tGitApi.notesApi.getIssueNote(repo.projectIdOrPath, issue.id, commentId)
            TGitObjectConverter.convertComment(note)
        }
    }

    /**
     * 获取问题评论列表
     * @param repository 代码仓库信息
     * @param number 问题编号
     * @param opts 列表查询选项
     * @return 评论列表
     */
    override fun listComments(repository: ScmProviderRepository, number: Int, opts: ListOptions): List<Comment> {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val issue = getIssueIdByNumber(tGitApi, repo.projectIdOrPath, number)
            tGitApi.notesApi.getIssueNotes(repo.projectIdOrPath, issue.id, opts.page, opts.pageSize)
                    .map { TGitObjectConverter.convertComment(it) }
        }
    }

    /**
     * 创建问题评论
     * @param repository 代码仓库信息
     * @param number 问题编号
     * @param input 评论输入参数
     * @return 创建的评论详情
     * @throws IllegalArgumentException 当评论内容为空时抛出
     */
    override fun createComment(repository: ScmProviderRepository, number: Int, input: CommentInput): Comment {
        if (StringUtils.isBlank(input.body)) {
            throw IllegalArgumentException("comment body cannot be empty")
        }
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val issue = getIssueIdByNumber(tGitApi, repo.projectIdOrPath, number)
            val note = tGitApi.notesApi.createIssueNote(repo.projectIdOrPath, issue.id, input.body)
            TGitObjectConverter.convertComment(note)
        }
    }

    /**
     * 删除问题评论
     * @param repository 代码仓库信息
     * @param number 问题编号
     * @param commentId 评论ID
     */
    override fun deleteComment(repository: ScmProviderRepository, number: Int, commentId: Long) {
        // 待实现
    }

    /**
     * 根据问题编号获取问题ID
     * @param tGitApi TGit API实例
     * @param projectIdOrPath 项目ID或路径
     * @param number 问题编号
     * @return 问题详情
     * @throws ScmApiException 当问题不存在时抛出
     */
    private fun getIssueIdByNumber(tGitApi: TGitApi, projectIdOrPath: Any, number: Int): TGitIssue {
        return tGitApi.issuesApi.getIssueByIid(projectIdOrPath, number)
            ?: throw ScmApiException("issue ($number) not found")
    }

    /**
     * 转换问题状态枚举
     * @param from 源问题状态
     * @return 目标问题状态
     */
    private fun convertFromState(from: IssueState): TGitIssueState? {
        return when (from) {
            IssueState.OPENED -> TGitIssueState.OPENED
            IssueState.CLOSED -> TGitIssueState.CLOSED
            IssueState.REOPENED -> TGitIssueState.REOPENED
            else -> null
        }
    }
}
