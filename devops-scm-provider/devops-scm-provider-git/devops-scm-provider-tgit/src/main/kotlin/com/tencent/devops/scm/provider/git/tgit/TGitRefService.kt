package com.tencent.devops.scm.provider.git.tgit

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
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory

/**
 * TGit 引用服务实现类
 * @property apiFactory TGit API工厂
 */
class TGitRefService(
    private val apiFactory: TGitApiFactory
) : RefService {

    /**
     * 创建分支
     * @param repository 代码仓库信息
     * @param input 分支输入参数
     */
    override fun createBranch(repository: ScmProviderRepository, input: ReferenceInput) {
        TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            tGitApi.branchesApi.createBranch(repo.projectIdOrPath, input.name, input.sha)
        }
    }

    /**
     * 查找分支
     * @param repository 代码仓库信息
     * @param name 分支名称
     * @return 分支引用信息
     */
    override fun findBranch(repository: ScmProviderRepository, name: String): Reference {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val branch = tGitApi.branchesApi.getBranch(repo.projectIdOrPath, name)
            TGitObjectConverter.convertBranch(branch)
        }
    }

    /**
     * 获取分支列表
     * @param repository 代码仓库信息
     * @param opts 列表查询选项
     * @return 分支引用列表
     */
    override fun listBranches(repository: ScmProviderRepository, opts: BranchListOptions): List<Reference> {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            tGitApi.branchesApi.getBranches(repo.projectIdOrPath, opts.search, opts.page, opts.pageSize)
                    .map { TGitObjectConverter.convertBranch(it) }
        }
    }

    /**
     * 创建标签
     * @param repository 代码仓库信息
     * @param input 标签输入参数
     */
    override fun createTag(repository: ScmProviderRepository, input: ReferenceInput) {
        TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            tGitApi.tagsApi.createTag(repo.projectIdOrPath, input.name, input.sha)
        }
    }

    /**
     * 查找标签
     * @param repository 代码仓库信息
     * @param name 标签名称
     * @return 标签引用信息
     */
    override fun findTag(repository: ScmProviderRepository, name: String): Reference {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val tag = tGitApi.tagsApi.getTag(repo.projectIdOrPath, name)
            TGitObjectConverter.convertTag(tag)
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
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            tGitApi.tagsApi.getTags(repo.projectIdOrPath, opts.search, opts.page, opts.pageSize)
                    .map { TGitObjectConverter.convertTag(it) }
        }
    }

    /**
     * 查找提交
     * @param repository 代码仓库信息
     * @param ref 提交引用
     * @return 提交信息
     */
    override fun findCommit(repository: ScmProviderRepository, ref: String): Commit {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val commit = tGitApi.commitsApi.getCommit(repo.projectIdOrPath, ref)
            TGitObjectConverter.convertCommit(commit)
        }
    }

    /**
     * 获取提交列表
     * @param repository 代码仓库信息
     * @param opts 列表查询选项
     * @return 提交列表
     */
    override fun listCommits(repository: ScmProviderRepository, opts: CommitListOptions): List<Commit> {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            tGitApi.commitsApi.getCommits(repo.projectIdOrPath, opts.ref, opts.path, opts.page, opts.pageSize)
                    .map { TGitObjectConverter.convertCommit(it) }
        }
    }

    /**
     * 获取变更列表
     * @param repository 代码仓库信息
     * @param ref 提交引用
     * @param opts 列表查询选项
     * @return 变更列表
     */
    override fun listChanges(repository: ScmProviderRepository, ref: String, opts: ListOptions): List<Change> {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            tGitApi.commitsApi.getDiff(repo.projectIdOrPath, ref).map {
                TGitObjectConverter.convertChange(it)
            }
        }
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
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val compareResults = tGitApi.repositoryApi.compare(
                repo.projectIdOrPath, source, target, false, null
            )
            compareResults.diffs.map { TGitObjectConverter.convertChange(it) }
        }
    }
}
