package com.tencent.devops.scm.provider.gitee.simple

import com.gitee.sdk.gitee5j.ApiException
import com.gitee.sdk.gitee5j.api.RepositoriesApi
import com.gitee.sdk.gitee5j.model.Branch
import com.gitee.sdk.gitee5j.model.CompleteBranch
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
import java.util.stream.Collectors

class GiteeRefService(private val giteeApiFactory: GiteeApiClientFactory) : RefService {

    override fun createBranch(repository: ScmProviderRepository, input: ReferenceInput) {
        throw UnsupportedOperationException("gitee template not support create branch")
    }

    override fun findBranch(repository: ScmProviderRepository, name: String): Reference {
        return GiteeApiTemplate.execute(
            repository,
            giteeApiFactory
        ) { repoName, client ->
            val repositoriesApi = RepositoriesApi(client)
            val completeBranch: CompleteBranch
            try {
                completeBranch = repositoriesApi.getReposOwnerRepoBranchesBranchWithHttpInfo(
                    repoName.first,
                    repoName.second,
                    name
                ).data
            } catch (e: ApiException) {
                throw RuntimeException(e)
            }
            GiteeObjectConverter.convertBranches(completeBranch)
        }
    }

    override fun listBranches(repository: ScmProviderRepository, opts: BranchListOptions): List<Reference> {
        return GiteeApiTemplate.execute(
            repository,
            giteeApiFactory
        ) { repoName, client ->
            val repositoriesApi = RepositoriesApi(client)
            val branches: List<Branch>
            try {
                branches = repositoriesApi.getReposOwnerRepoBranchesWithHttpInfo(
                    repoName.first,
                    repoName.second,
                    null,
                    null,
                    opts.page,
                    opts.pageSize
                ).data
            } catch (e: ApiException) {
                throw RuntimeException(e)
            }
            // 结果转化
            branches.stream()
                .map { branch: Branch -> GiteeObjectConverter.convertBranches(branch) }
                .collect(Collectors.toList())
        }
    }

    override fun createTag(repository: ScmProviderRepository, input: ReferenceInput) {
        throw UnsupportedOperationException("gitee template not support create tag")
    }

    override fun findTag(repository: ScmProviderRepository, name: String): Reference {
        throw UnsupportedOperationException("gitee template not support find tag")
    }

    override fun listTags(repository: ScmProviderRepository, opts: TagListOptions): List<Reference> {
        throw UnsupportedOperationException("gitee template not support list tag")
    }

    override fun findCommit(repository: ScmProviderRepository, ref: String): Commit {
        throw UnsupportedOperationException("gitee template not support find commit")
    }

    override fun listCommits(repository: ScmProviderRepository, opts: CommitListOptions): List<Commit> {
        throw UnsupportedOperationException("gitee template not support list commit")
    }

    override fun listChanges(repository: ScmProviderRepository, ref: String, opts: ListOptions): List<Change> {
        throw UnsupportedOperationException("gitee template not support list change")
    }

    override fun compareChanges(
        repository: ScmProviderRepository,
        source: String,
        target: String,
        opts: ListOptions
    ): List<Change> {
        throw UnsupportedOperationException("gitee template not support compare changes")
    }
}
