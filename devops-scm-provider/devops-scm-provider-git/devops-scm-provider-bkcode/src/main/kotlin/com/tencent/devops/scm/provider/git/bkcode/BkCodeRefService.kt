package com.tencent.devops.scm.provider.git.bkcode

import com.tencent.devops.scm.api.RefService
import com.tencent.devops.scm.api.pojo.BranchListOptions
import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.CommitListOptions
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.Reference
import com.tencent.devops.scm.api.pojo.ReferenceInput
import com.tencent.devops.scm.api.pojo.TagListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.bkcode.BkCodeApiFactory
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeDiffFileRevRange

/**
 * BkCode 引用服务实现类
 * @property apiFactory BkCode API工厂
 */
class BkCodeRefService(private val apiFactory: BkCodeApiFactory) : RefService {

    /**
     * 创建分支
     * @param repository 代码仓库信息
     * @param input 分支输入参数
     */
    override fun createBranch(repository: ScmProviderRepository, input: ReferenceInput) {
        throw UnsupportedOperationException("BkCode does not support create branch")
    }

    /**
     * 查找分支
     * @param repository 代码仓库信息
     * @param name 分支名称
     * @return 分支引用信息
     */
    override fun findBranch(repository: ScmProviderRepository, name: String): Reference {
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            val branch = bkCodeApi.branchesApi.getBranch(repo.projectIdOrPath, name)
            BkCodeObjectConverter.convertBranch(branch)
        }
    }

    /**
     * 获取分支列表
     * @param repository 代码仓库信息
     * @param opts 列表查询选项
     * @return 分支引用列表
     */
    override fun listBranches(repository: ScmProviderRepository, opts: BranchListOptions): List<Reference> {
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            bkCodeApi.branchesApi.getBranches(
                repo.projectIdOrPath,
                opts.search,
                opts.page,
                opts.pageSize
            ).records.map { BkCodeObjectConverter.convertBranch(it) }
        }
    }

    /**
     * 创建标签
     * @param repository 代码仓库信息
     * @param input 标签输入参数
     */
    override fun createTag(repository: ScmProviderRepository, input: ReferenceInput) {
        throw UnsupportedOperationException("BkCode does not support create tag")
    }

    /**
     * 查找标签
     * @param repository 代码仓库信息
     * @param name 标签名称
     * @return 标签引用信息
     */
    override fun findTag(repository: ScmProviderRepository, name: String): Reference {
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            val tag = bkCodeApi.tagApi.getTag(repo.projectIdOrPath, name)
            BkCodeObjectConverter.convertTag(tag)
        }
    }

    /**
     * 获取标签列表
     * 注意: 工蜂社区版不支持tag搜索
     * @param repository 代码仓库信息
     * @param opts 列表查询选项
     * @return 标签引用列表
     */
    override fun listTags(repository: ScmProviderRepository, opts: TagListOptions): List<Reference> {
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            bkCodeApi.tagApi.getTags(
                repo.projectIdOrPath,
                opts.search,
                opts.page,
                opts.pageSize
            ).records.map { BkCodeObjectConverter.convertTag(it) }
        }
    }

    /**
     * 查找提交
     * @param repository 代码仓库信息
     * @param ref 提交引用
     * @return 提交信息
     */
    override fun findCommit(repository: ScmProviderRepository, ref: String): Commit {
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            val commit = bkCodeApi.commitApi.getCommit(repo.projectIdOrPath, ref)
            BkCodeObjectConverter.convertCommit(commit)
        }
    }

    /**
     * 获取提交列表
     * @param repository 代码仓库信息
     * @param opts 列表查询选项
     * @return 提交列表
     */
    override fun listCommits(repository: ScmProviderRepository, opts: CommitListOptions): List<Commit> {
        throw UnsupportedOperationException("BkCode does not support list commit")
    }

    /**
     * 获取变更列表
     * @param repository 代码仓库信息
     * @param ref 提交引用
     * @param opts 列表查询选项
     * @return 变更列表
     */
    override fun listChanges(repository: ScmProviderRepository, ref: String, opts: ListOptions): List<Change> {
        throw UnsupportedOperationException("BkCode does not support list changes")
    }

    /**
     * 比较变更
     * @param repository 代码仓库信息
     * @param source 源引用
     * @param target 目标引用
     * @param opts 列表查询选项
     * @return 变更列表
     */
    override fun compareChanges(
        repository: ScmProviderRepository,
        source: String,
        target: String,
        opts: ListOptions
    ): List<Change> {
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            val compareResults = bkCodeApi.commitApi.compare(
                repo.projectIdOrPath,
                target,
                source,
                BkCodeDiffFileRevRange.DOUBLE_DOT,
                false
            ).diffFiles
            compareResults.map { BkCodeObjectConverter.convertChange(it) }
        }
    }
}