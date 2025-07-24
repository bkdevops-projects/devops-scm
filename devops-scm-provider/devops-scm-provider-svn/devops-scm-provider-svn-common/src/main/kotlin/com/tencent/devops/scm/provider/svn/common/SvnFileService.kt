package com.tencent.devops.scm.provider.svn.common

import com.tencent.devops.scm.api.FileService
import com.tencent.devops.scm.api.enums.ContentKind
import com.tencent.devops.scm.api.exception.ScmApiException
import com.tencent.devops.scm.api.pojo.Content
import com.tencent.devops.scm.api.pojo.ContentInput
import com.tencent.devops.scm.api.pojo.Tree
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository
import java.io.ByteArrayOutputStream
import org.tmatesoft.svn.core.SVNException
import org.tmatesoft.svn.core.SVNNodeKind
import org.tmatesoft.svn.core.SVNProperties

class SvnFileService : FileService {

    override fun find(repository: ScmProviderRepository, path: String, ref: String): Content {
        val providerRepository = repository as SvnScmProviderRepository
        try {
            val bos = ByteArrayOutputStream()
            val svnRepository = SvnkitUtils.openRepo(providerRepository)
            svnRepository.getFile(path, ref.toLong(), SVNProperties(), bos)
            return Content(
                path = path,
                sha = ref,
                content = bos.toString(),
                blobId = ref
            )
        } catch (e: SVNException) {
            throw ScmApiException(e)
        }
    }

    override fun create(repository: ScmProviderRepository, path: String, input: ContentInput) {
        throw UnsupportedOperationException("svn not support create file")
    }

    override fun update(repository: ScmProviderRepository, path: String, input: ContentInput) {
        throw UnsupportedOperationException("svn not support update file")
    }

    override fun listTree(
        repository: ScmProviderRepository,
        path: String,
        ref: String,
        recursive: Boolean
    ): List<Tree> {
        val providerRepository = repository as SvnScmProviderRepository
        try {
            val revision = ref.toLong()
            val svnRepository = SvnkitUtils.openRepo(providerRepository)
            val svnNodeKind = svnRepository.checkPath(path, revision)
            val trees = mutableListOf<Tree>()
            if (svnNodeKind == SVNNodeKind.DIR) {
                trees.addAll(SvnkitUtils.listFiles(svnRepository, path, revision, recursive))
            } else {
                trees.add(
                    Tree(
                        path = path,
                        sha = ref,
                        kind = ContentKind.FILE,
                        blobId = ref
                    )
                )
            }
            return trees
        } catch (e: SVNException) {
            throw ScmApiException(e)
        }
    }
}