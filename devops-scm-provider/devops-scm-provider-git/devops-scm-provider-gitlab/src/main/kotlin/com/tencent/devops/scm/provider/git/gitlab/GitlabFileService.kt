package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.FileService
import com.tencent.devops.scm.api.pojo.Content
import com.tencent.devops.scm.api.pojo.ContentInput
import com.tencent.devops.scm.api.pojo.Tree
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.gitlab.GitlabApiFactory

class GitlabFileService(private val apiFactory: GitlabApiFactory) : FileService {
    override fun find(repository: ScmProviderRepository, path: String, ref: String): Content =
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            GitlabObjectConverter.convertContent(api.repositoryFilesApi.getFile(repo.projectIdOrPath, path, ref))
        }

    override fun create(repository: ScmProviderRepository, path: String, input: ContentInput) {
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.repositoryFilesApi.createFile(
                repo.projectIdOrPath, path, input.ref, input.content, input.message
            )
        }
    }

    override fun update(repository: ScmProviderRepository, path: String, input: ContentInput) {
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            api.repositoryFilesApi.updateFile(
                repo.projectIdOrPath, path, input.ref, input.content, input.message
            )
        }
    }

    override fun listTree(
        repository: ScmProviderRepository,
        path: String,
        ref: String,
        recursive: Boolean
    ): List<Tree> = GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
        val trees = mutableListOf<Tree>()
        var page = 1
        do {
            val current = api.repositoryFilesApi.getTree(repo.projectIdOrPath, path, ref, recursive, page++, PAGE_SIZE)
            trees += current.map { GitlabObjectConverter.convertTree(it, path) }
        } while (current.size == PAGE_SIZE)
        trees
    }

    private companion object {
        const val PAGE_SIZE = 100
    }
}
