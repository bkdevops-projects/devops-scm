package com.tencent.devops.scm.api

import com.tencent.devops.scm.api.pojo.Comment
import com.tencent.devops.scm.api.pojo.CommentInput
import com.tencent.devops.scm.api.pojo.Issue
import com.tencent.devops.scm.api.pojo.IssueInput
import com.tencent.devops.scm.api.pojo.IssueListOptions
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository

/**
 * 问题(Issue)服务接口
 */
interface IssueService {
    /**
     * 查找指定问题
     * @param repository 代码库信息
     * @param number 问题编号
     * @return 问题对象
     */
    fun find(repository: ScmProviderRepository, number: Int): Issue

    /**
     * 创建问题
     * @param repository 代码库信息
     * @param input 问题输入参数
     * @return 创建的问题对象
     */
    fun create(repository: ScmProviderRepository, input: IssueInput): Issue

    /**
     * 列出问题列表
     * @param repository 代码库信息
     * @param opts 列表查询选项
     * @return 问题列表
     */
    fun list(repository: ScmProviderRepository, opts: IssueListOptions): List<Issue>

    /**
     * 关闭问题
     * @param repository 代码库信息
     * @param number 问题编号
     */
    fun close(repository: ScmProviderRepository, number: Int)

    /**
     * 查找问题评论
     * @param repository 代码库信息
     * @param number 问题编号
     * @param commentId 评论ID
     * @return 评论对象
     */
    fun findComment(repository: ScmProviderRepository, number: Int, commentId: Long): Comment

    /**
     * 列出问题评论
     * @param repository 代码库信息
     * @param number 问题编号
     * @param opts 列表查询选项
     * @return 评论列表
     */
    fun listComments(repository: ScmProviderRepository, number: Int, opts: ListOptions): List<Comment>

    /**
     * 创建问题评论
     * @param repository 代码库信息
     * @param number 问题编号
     * @param input 评论输入参数
     * @return 创建的评论对象
     */
    fun createComment(repository: ScmProviderRepository, number: Int, input: CommentInput): Comment

    /**
     * 删除问题评论
     * @param repository 代码库信息
     * @param number 问题编号
     * @param commentId 评论ID
     */
    fun deleteComment(repository: ScmProviderRepository, number: Int, commentId: Long)
}
