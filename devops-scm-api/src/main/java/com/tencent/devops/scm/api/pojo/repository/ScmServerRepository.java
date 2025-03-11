package com.tencent.devops.scm.api.pojo.repository;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmServerRepository;

/**
 * 表示源代码管理服务端仓库信息
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GitScmServerRepository.class, name = GitScmServerRepository.CLASS_TYPE),
        @JsonSubTypes.Type(value = SvnScmServerRepository.class, name = SvnScmServerRepository.CLASS_TYPE),
})
public interface ScmServerRepository {

    /**
     * 获取服务端仓库ID
     * git: 仓库ID,不能为空
     *
     */
    Object getId();

    /**
     * 获取服务端代码库名称
     */
    String getName();

    /**
     * 获取服务端代码库全名,包含组
     *
     */
    String getFullName();
}
