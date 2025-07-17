package com.tencent.devops.scm.api

import com.tencent.devops.scm.api.pojo.Content
import com.tencent.devops.scm.api.pojo.ContentInput
import com.tencent.devops.scm.api.pojo.Tree
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository

/**
 * 文件服务接口
 */
interface FileService {
    /**
     * 查找文件内容
     * @param repository 代码库信息
     * @param path 文件路径
     * @param ref 分支/标签/commit引用
     * @return 文件内容对象
     */
    fun find(repository: ScmProviderRepository, path: String, ref: String): Content

    /**
     * 创建文件
     * @param repository 代码库信息
     * @param path 文件路径
     * @param input 文件内容输入
     */
    fun create(repository: ScmProviderRepository, path: String, input: ContentInput)

    /**
     * 更新文件
     * @param repository 代码库信息
     * @param path 文件路径
     * @param input 文件内容输入
     */
    fun update(repository: ScmProviderRepository, path: String, input: ContentInput)

    /**
     * 列出目录树
     * @param repository 代码库信息
     * @param path 目录路径
     * @param ref 分支/标签/commit引用
     * @param recursive 是否递归列出
     * @return 目录树列表
     */
    fun listTree(repository: ScmProviderRepository, path: String, ref: String, recursive: Boolean): List<Tree>
}
