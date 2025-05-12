package com.tencent.devops.scm.api.pojo.repository;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GitScmProviderRepository.class, name = GitScmProviderRepository.CLASS_TYPE),
        @JsonSubTypes.Type(value = SvnScmProviderRepository.class, name = SvnScmProviderRepository.CLASS_TYPE),
})
public interface ScmProviderRepository {
    String externalId();
}
