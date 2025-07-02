package com.tencent.devops.scm.provider.git.gitee

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
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.sdk.common.enums.SortOrder
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory
import com.tencent.devops.scm.sdk.gitee.enums.GiteeBranchOrderBy

class GiteeRefService(private val apiFactory: GiteeApiFactory) : RefService {

    override fun createBranch(repository: ScmProviderRepository, input: ReferenceInput) {
        // 暂未实现
    }

    override fun findBranch(repository: ScmProviderRepository, name: String): Reference {
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val branchesApi = giteeApi.getBranchesApi()
            val branch = branchesApi.getBranch(repo.projectIdOrPath, name)
            GiteeObjectConverter.convertBranches(branch)
        }
    }

    override fun listBranches(
        repository: ScmProviderRepository,
        opts: BranchListOptions
    ): List<Reference> {
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val branchesApi = giteeApi.getBranchesApi()
            val branches = branchesApi.getBranches(repo.projectIdOrPath)
            branches.map { GiteeObjectConverter.convertBranches(it) }
        }
    }

    override fun createTag(repository: ScmProviderRepository, input: ReferenceInput) {
        // 暂未实现
    }

    override fun findTag(repository: ScmProviderRepository, name: String): Reference {
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val tagApi = giteeApi.getTagApi()
            val tag = tagApi.getTags(repo.projectIdOrPath, name)
            GiteeObjectConverter.convertTag(tag)
        }
    }

    override fun listTags(
        repository: ScmProviderRepository,
        opts: TagListOptions
    ): List<Reference> {
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val tagApi = giteeApi.getTagApi()
            val tags = tagApi.getTags(
                repo.projectIdOrPath,
                opts.page,
                opts.pageSize,
                opts.orderBy?.let { GiteeBranchOrderBy.valueOf(it) },
                opts.sort?.let { SortOrder.valueOf(it) }
            )
            tags.map { GiteeObjectConverter.convertTag(it) }
        }
    }

    override fun findCommit(repository: ScmProviderRepository, ref: String): Commit {
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val commit = giteeApi.getCommitApi().getCommit(repo.projectIdOrPath, ref)
            GiteeObjectConverter.convertCommit(commit)
        }
    }

    override fun listCommits(
        repository: ScmProviderRepository,
        opts: CommitListOptions
    ): List<Commit> {
        // 暂未实现
        return emptyList()
    }

    override fun listChanges(
        repository: ScmProviderRepository,
        ref: String,
        opts: ListOptions
    ): List<Change> {
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val commit = giteeApi.getCommitApi().getCommit(repo.projectIdOrPath, ref)
            GiteeObjectConverter.convertChange(commit)
        }
    }

    override fun compareChanges(
        repository: ScmProviderRepository,
        source: String,
        target: String,
        opts: ListOptions
    ): List<Change> {
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val commitCompare = giteeApi.getFileApi().commitCompare(
                repo.projectIdOrPath,
                source,
                target,
                null
            )
            GiteeObjectConverter.convertCompare(commitCompare)
        }
    }
}
