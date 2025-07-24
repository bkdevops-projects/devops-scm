package com.tencent.devops.scm.provider.git.tgit

import com.tencent.devops.scm.api.FileService
import com.tencent.devops.scm.api.pojo.Content
import com.tencent.devops.scm.api.pojo.ContentInput
import com.tencent.devops.scm.api.pojo.Tree
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory

/**
 * TGit 文件服务实现类
 * @property apiFactory TGit API工厂
 */
class TGitFileService(
    private val apiFactory: TGitApiFactory
) : FileService {

    /**
     * 获取文件内容
     * @param repository 代码仓库信息
     * @param path 文件路径
     * @param ref 分支/标签/提交ID
     * @return 文件内容对象
     */
    override fun find(repository: ScmProviderRepository, path: String, ref: String): Content {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val file = tGitApi.repositoryFileApi.getFile(repo.projectIdOrPath, path, ref)
            TGitObjectConverter.convertContent(file)
        }
    }

    /**
     * 创建文件
     * @param repository 代码仓库信息
     * @param path 文件路径
     * @param input 文件内容输入
     */
    override fun create(repository: ScmProviderRepository, path: String, input: ContentInput) {
        TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            tGitApi.repositoryFileApi.createFile(
                repo.projectIdOrPath,
                path,
                input.ref,
                input.content,
                input.message
            )
        }
    }

    /**
     * 更新文件
     * @param repository 代码仓库信息
     * @param path 文件路径
     * @param input 文件内容输入
     */
    override fun update(repository: ScmProviderRepository, path: String, input: ContentInput) {
        TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            tGitApi.repositoryFileApi.updateFile(
                repo.projectIdOrPath,
                path,
                input.ref,
                input.content,
                input.message
            )
        }
    }

    /**
     * 获取目录树
     * @param repository 代码仓库信息
     * @param path 目录路径
     * @param ref 分支/标签/提交ID
     * @param recursive 是否递归获取
     * @return 目录树列表
     */
    override fun listTree(
        repository: ScmProviderRepository,
        path: String,
        ref: String,
        recursive: Boolean
    ): List<Tree> {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            tGitApi.repositoryFileApi.getTree(repo.projectIdOrPath, path, ref, recursive)
                    .map { TGitObjectConverter.convertTree(it) }
        }
    }
}
