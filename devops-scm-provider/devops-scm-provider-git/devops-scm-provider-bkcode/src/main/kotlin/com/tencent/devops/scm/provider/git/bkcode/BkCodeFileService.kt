package com.tencent.devops.scm.provider.git.bkcode

import com.tencent.devops.scm.api.FileService
import com.tencent.devops.scm.api.pojo.Content
import com.tencent.devops.scm.api.pojo.ContentInput
import com.tencent.devops.scm.api.pojo.Tree
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.bkcode.BkCodeApiFactory

class BkCodeFileService (
    private val apiFactory: BkCodeApiFactory
) :FileService {
    override fun find(repository: ScmProviderRepository, path: String, ref: String): Content {
        return BkCodeApiTemplate.execute(repository, apiFactory) { repo, bkCodeApi ->
            val fileContent = bkCodeApi.fileApi.getFileContent(repo.projectIdOrPath, path, ref)
            BkCodeObjectConverter.convertContent(fileContent)
        }
    }

    override fun create(repository: ScmProviderRepository, path: String, input: ContentInput) {
        throw UnsupportedOperationException("BkCode does not support create file")
    }

    override fun update(repository: ScmProviderRepository, path: String, input: ContentInput) {
        throw UnsupportedOperationException("BkCode does not support update file")
    }

    override fun listTree(
        repository: ScmProviderRepository,
        path: String,
        ref: String,
        recursive: Boolean
    ): List<Tree> {
        throw UnsupportedOperationException("BkCode does not support list tree")
    }
}