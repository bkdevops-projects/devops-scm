package com.tencent.devops.scm.provider.svn.common

import com.tencent.devops.scm.api.RefService
import com.tencent.devops.scm.api.exception.ScmApiException
import com.tencent.devops.scm.api.pojo.BranchListOptions
import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.CommitListOptions
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.Reference
import com.tencent.devops.scm.api.pojo.ReferenceInput
import com.tencent.devops.scm.api.pojo.TagListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository
import org.tmatesoft.svn.core.SVNException

open class SvnRefService : RefService {

    override fun createBranch(repository: ScmProviderRepository, input: ReferenceInput) {
        throw UnsupportedOperationException("svn not support create branch")
    }

    override fun findBranch(repository: ScmProviderRepository, name: String): Reference {
        throw UnsupportedOperationException("svn not support find branch")
    }

    override fun listBranches(repository: ScmProviderRepository, opts: BranchListOptions): List<Reference> {
        val providerRepository = repository as SvnScmProviderRepository
        try {
            val svnRepository = SvnkitUtils.openRepo(providerRepository)
            val trees = SvnkitUtils.listFiles(svnRepository, "", -1, false)
            return trees.map { tree ->
                Reference(
                    name = tree.path,
                    sha = tree.sha
                )
            }
        } catch (e: SVNException) {
            throw ScmApiException(e)
        }
    }

    override fun createTag(repository: ScmProviderRepository, input: ReferenceInput) {
        throw UnsupportedOperationException("svn not support create tag")
    }

    override fun findTag(repository: ScmProviderRepository, name: String): Reference {
        throw UnsupportedOperationException("svn not support find branch")
    }

    override fun listTags(repository: ScmProviderRepository, opts: TagListOptions): List<Reference> {
        throw UnsupportedOperationException("svn not support list tag")
    }

    override fun findCommit(repository: ScmProviderRepository, ref: String): Commit {
        throw UnsupportedOperationException("svn not support find commit")
    }

    override fun listCommits(repository: ScmProviderRepository, opts: CommitListOptions): List<Commit> {
        throw UnsupportedOperationException("svn not support list commit")
    }

    override fun listChanges(repository: ScmProviderRepository, ref: String, opts: ListOptions): List<Change> {
        throw UnsupportedOperationException("svn not support list change")
    }

    override fun compareChanges(
        repository: ScmProviderRepository,
        source: String,
        target: String,
        opts: ListOptions
    ): List<Change> {
        throw UnsupportedOperationException("svn not support compare change")
    }
}
