package com.tencent.devops.scm.api.pojo.repository.git;

import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * git提供者代码库信息
 */
@Getter
public class GitScmProviderRepository implements ScmProviderRepository {

    public static final String CLASS_TYPE = "GIT";

    private Object projectIdOrPath;
    private IScmAuth auth;
    private String url;
    private String externalId;

    public GitScmProviderRepository withProjectIdOrPath(Object projectIdOrPath) {
        this.projectIdOrPath = projectIdOrPath;
        return this;
    }

    public GitScmProviderRepository withAuth(IScmAuth auth) {
        this.auth = auth;
        return this;
    }

    public GitScmProviderRepository withUrl(String url) {
        this.url = url;
        this.projectIdOrPath = new GitRepositoryUrl(url).getFullName();
        return this;
    }

    public GitScmProviderRepository withExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    @Override
    public String externalId() {
        return this.externalId;
    }
}
