package com.tencent.devops.scm.provider.svn.common

import com.tencent.devops.scm.api.enums.ContentKind
import com.tencent.devops.scm.api.pojo.Tree
import com.tencent.devops.scm.api.pojo.auth.SshPrivateKeyScmAuth
import com.tencent.devops.scm.api.pojo.auth.TokenSshPrivateKeyScmAuth
import com.tencent.devops.scm.api.pojo.auth.TokenUserPassScmAuth
import com.tencent.devops.scm.api.pojo.auth.UserPassScmAuth
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository
import org.tmatesoft.svn.core.SVNDirEntry
import org.tmatesoft.svn.core.SVNException
import org.tmatesoft.svn.core.SVNNodeKind
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.auth.SVNAuthentication
import org.tmatesoft.svn.core.auth.SVNPasswordAuthentication
import org.tmatesoft.svn.core.auth.SVNSSHAuthentication
import org.tmatesoft.svn.core.io.SVNRepository
import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.wc.DefaultSVNRepositoryPool
import org.tmatesoft.svn.core.wc.SVNWCUtil

object SvnkitUtils {

    @Throws(SVNException::class)
    fun openRepo(providerRepository: SvnScmProviderRepository): SVNRepository {
        val svnURL = SVNURL.parseURIEncoded(providerRepository.url)

        val authentications = arrayOf(createAuthentication(svnURL, providerRepository))
        val authManager = TmatesoftBasicAuthenticationManager(authentications)
        val options = DefaultSVNRepositoryPool(
            authManager,
            SVNWCUtil.createDefaultOptions(true),
            30 * 1000L,
            true
        )

        val repository = SVNRepositoryFactory.create(svnURL, options)
        repository.authenticationManager = authManager
        return repository
    }

    fun closeRepo(repository: SVNRepository?) {
        repository?.closeSession()
    }

    fun createAuthentication(svnURL: SVNURL, providerRepository: SvnScmProviderRepository): SVNAuthentication {
        val auth = providerRepository.auth
        return when (auth) {
            is UserPassScmAuth -> {
                SVNPasswordAuthentication.newInstance(
                    auth.username,
                    auth.password.toCharArray(),
                    false,
                    svnURL,
                    false
                )
            }
            is TokenUserPassScmAuth -> {
                SVNPasswordAuthentication.newInstance(
                    auth.username,
                    auth.password.toCharArray(),
                    false,
                    svnURL,
                    false
                )
            }
            is SshPrivateKeyScmAuth -> {
                SVNSSHAuthentication.newInstance(
                    providerRepository.userName,
                    auth.privateKey.toCharArray(),
                    auth.passphrase?.toCharArray(),
                    22,
                    false,
                    svnURL,
                    false
                )
            }
            is TokenSshPrivateKeyScmAuth -> {
                SVNSSHAuthentication.newInstance(
                    providerRepository.userName,
                    auth.privateKey.toCharArray(),
                    auth.passphrase?.toCharArray(),
                    22,
                    false,
                    svnURL,
                    false
                )
            }
            else -> throw UnsupportedOperationException("not support svn auth type")
        }
    }

    @Throws(SVNException::class)
    fun listFiles(svnRepository: SVNRepository, path: String, revision: Long, recursive: Boolean): List<Tree> {
        val trees = mutableListOf<Tree>()
        val dirEntries = mutableSetOf<SVNDirEntry>()
        svnRepository.getDir(path, revision, false, dirEntries)
        
        for (entry in dirEntries) {
            val filePath = if (path.isEmpty()) entry.name else "$path/${entry.name}"
            if (entry.kind == SVNNodeKind.DIR) {
                trees.add(
                    Tree(
                        path = filePath,
                        sha = entry.revision.toString(),
                        kind = ContentKind.DIRECTORY,
                        blobId = entry.revision.toString()
                    )
                )
                if (recursive) {
                    val subTrees = listFiles(svnRepository, filePath, entry.revision, true)
                    trees.addAll(subTrees)
                }
            } else {
                trees.add(
                    Tree(
                        path = filePath,
                        sha = entry.revision.toString(),
                        kind = ContentKind.FILE,
                        blobId = entry.revision.toString()
                    )
                )
            }
        }
        return trees
    }
}