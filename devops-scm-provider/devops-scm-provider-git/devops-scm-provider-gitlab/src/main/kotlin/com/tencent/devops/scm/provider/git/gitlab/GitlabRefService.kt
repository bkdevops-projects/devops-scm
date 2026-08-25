package com.tencent.devops.scm.provider.git.gitlab

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
import com.tencent.devops.scm.sdk.gitlab.GitlabApiFactory

class GitlabRefService(private val apiFactory: GitlabApiFactory) : RefService {
    override fun createBranch(repository: ScmProviderRepository, input: ReferenceInput) {
        val sha = requireSha(input)
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.branchesApi.createBranch(repo.projectIdOrPath, input.name, sha)
        }
    }

    override fun findBranch(repository: ScmProviderRepository, name: String): Reference =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            GitlabObjectConverter.convertBranch(api.branchesApi.getBranch(repo.projectIdOrPath, name))
        }

    override fun listBranches(repository: ScmProviderRepository, opts: BranchListOptions): List<Reference> =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.branchesApi.getBranches(repo.projectIdOrPath, opts.search, opts.page, size(opts.pageSize))
                .map(GitlabObjectConverter::convertBranch)
        }

    override fun createTag(repository: ScmProviderRepository, input: ReferenceInput) {
        val sha = requireSha(input)
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.tagsApi.createTag(repo.projectIdOrPath, input.name, sha, null)
        }
    }

    override fun findTag(repository: ScmProviderRepository, name: String): Reference =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            GitlabObjectConverter.convertTag(api.tagsApi.getTag(repo.projectIdOrPath, name))
        }

    override fun listTags(repository: ScmProviderRepository, opts: TagListOptions): List<Reference> =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.tagsApi.getTags(repo.projectIdOrPath, opts.search, opts.page, size(opts.pageSize))
                .map(GitlabObjectConverter::convertTag)
        }

    override fun findCommit(repository: ScmProviderRepository, ref: String): Commit =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            GitlabObjectConverter.convertCommit(api.commitsApi.getCommit(repo.projectIdOrPath, ref))
        }

    override fun listCommits(repository: ScmProviderRepository, opts: CommitListOptions): List<Commit> =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.commitsApi.getCommits(
                repo.projectIdOrPath, opts.ref, opts.path, null, null, opts.page, size(opts.pageSize)
            ).map(GitlabObjectConverter::convertCommit)
        }

    override fun listChanges(repository: ScmProviderRepository, ref: String, opts: ListOptions): List<Change> =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.commitsApi.getDiff(repo.projectIdOrPath, ref, opts.page, size(opts.pageSize))
                .map(GitlabObjectConverter::convertChange)
        }

    override fun compareChanges(
        repository: ScmProviderRepository,
        source: String,
        target: String,
        opts: ListOptions
    ): List<Change> = GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
        api.commitsApi.compare(repo.projectIdOrPath, source, target, false).diffs
            .map(GitlabObjectConverter::convertChange)
    }

    private fun size(value: Int?) = (value ?: 100).coerceIn(1, 100)

    private fun requireSha(input: ReferenceInput): String = input.sha?.takeIf(String::isNotBlank)
        ?: throw IllegalArgumentException("reference sha cannot be blank")
}
