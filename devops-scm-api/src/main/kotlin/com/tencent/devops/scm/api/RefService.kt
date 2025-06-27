package com.tencent.devops.scm.api

import com.tencent.devops.scm.api.pojo.BranchListOptions
import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.CommitListOptions
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.Reference
import com.tencent.devops.scm.api.pojo.ReferenceInput
import com.tencent.devops.scm.api.pojo.TagListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository

/**
 * 引用(分支/标签/提交)服务接口
 */
interface RefService {
    /**
     * 创建分支
     * @param repository 代码库信息
     * @param input 分支输入参数
     */
    fun createBranch(repository: ScmProviderRepository, input: ReferenceInput)

    /**
     * 查找分支
     * @param repository 代码库信息
     * @param name 分支名称
     * @return 分支引用对象
     */
    fun findBranch(repository: ScmProviderRepository, name: String): Reference

    /**
     * 列出分支列表
     * @param repository 代码库信息
     * @param opts 分支列表查询选项
     * @return 分支引用列表
     */
    fun listBranches(repository: ScmProviderRepository, opts: BranchListOptions): List<Reference>

    /**
     * 创建标签
     * @param repository 代码库信息
     * @param input 标签输入参数
     */
    fun createTag(repository: ScmProviderRepository, input: ReferenceInput)

    /**
     * 查找标签
     * @param repository 代码库信息
     * @param name 标签名称
     * @return 标签引用对象
     */
    fun findTag(repository: ScmProviderRepository, name: String): Reference

    /**
     * 列出标签列表
     * @param repository 代码库信息
     * @param opts 标签列表查询选项
     * @return 标签引用列表
     */
    fun listTags(repository: ScmProviderRepository, opts: TagListOptions): List<Reference>

    /**
     * 查找提交
     * @param repository 代码库信息
     * @param ref 提交引用
     * @return 提交对象
     */
    fun findCommit(repository: ScmProviderRepository, ref: String): Commit

    /**
     * 列出提交记录
     * @param repository 代码库信息
     * @param opts 提交列表查询选项
     * @return 提交记录列表
     */
    fun listCommits(repository: ScmProviderRepository, opts: CommitListOptions): List<Commit>

    /**
     * 列出变更文件
     * @param repository 代码库信息
     * @param ref 引用
     * @param opts 列表查询选项
     * @return 变更文件列表
     */
    fun listChanges(repository: ScmProviderRepository, ref: String, opts: ListOptions): List<Change>

    /**
     * 比较变更差异
     * @param repository 代码库信息
     * @param source 源引用
     * @param target 目标引用
     * @param opts 列表查询选项
     * @return 变更差异列表
     */
    fun compareChanges(
        repository: ScmProviderRepository,
        source: String,
        target: String,
        opts: ListOptions
    ): List<Change>
}
