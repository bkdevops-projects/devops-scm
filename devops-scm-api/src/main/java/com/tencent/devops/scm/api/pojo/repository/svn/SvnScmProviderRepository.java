package com.tencent.devops.scm.api.pojo.repository.svn;

import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import lombok.Getter;

/**
 * svn提供者代码库信息
 */
@Getter
public class SvnScmProviderRepository implements ScmProviderRepository {

    public static final String CLASS_TYPE = "SVN";

    private String url;
    private String userName;
    private IScmAuth auth;

    public SvnScmProviderRepository withUrl(String url) {
        this.url = url;
        return this;
    }

    public SvnScmProviderRepository withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public SvnScmProviderRepository withAuth(IScmAuth auth) {
        this.auth = auth;
        return this;
    }
}
