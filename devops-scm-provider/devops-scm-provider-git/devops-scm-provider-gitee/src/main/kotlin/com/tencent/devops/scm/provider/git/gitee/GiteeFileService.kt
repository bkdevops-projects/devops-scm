package com.tencent.devops.scm.provider.git.gitee

import com.tencent.devops.scm.api.FileService
import com.tencent.devops.scm.api.pojo.Content
import com.tencent.devops.scm.api.pojo.ContentInput
import com.tencent.devops.scm.api.pojo.Tree
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory

class GiteeFileService(
    private val apiFactory: GiteeApiFactory
) : FileService {
    override fun find(repository: ScmProviderRepository, path: String, ref: String): Content {
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            val fileContent = bkCodeApi.fileApi.getFileContent(repo.projectIdOrPath, path, ref)
            GiteeObjectConverter.convertFileContent(
                path = path,
                form = fileContent
            )
        }
    }

    override fun create(repository: ScmProviderRepository, path: String, input: ContentInput) {
        throw UnsupportedOperationException("Gitee does not support create file")
    }

    override fun update(repository: ScmProviderRepository, path: String, input: ContentInput) {
        throw UnsupportedOperationException("Gitee does not support update file")
    }

    override fun listTree(
        repository: ScmProviderRepository,
        path: String,
        ref: String,
        recursive: Boolean
    ): List<Tree> {
        throw UnsupportedOperationException("Gitee does not support list tree")
    }
}