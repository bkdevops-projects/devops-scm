package com.tencent.devops.scm.sample;

import com.tencent.devops.scm.api.enums.ScmProviderCodes;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository;
import com.tencent.devops.scm.spring.manager.ScmProviderManager;
import com.tencent.devops.scm.spring.properties.HttpClientProperties;
import com.tencent.devops.scm.spring.properties.ScmProviderProperties;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * git类型的源码管理使用示例
 */
public class SampleGitScmProvider {

    @Autowired
    private ScmProviderManager scmProviderManager;

    private ScmProviderProperties initProviderProperties() {
        HttpClientProperties httpClientProperties = HttpClientProperties.builder()
                .apiUrl("https://api.github.com")
                .build();
        return ScmProviderProperties.builder()
                .providerCode(ScmProviderCodes.GITHUB.name())
                .httpClientProperties(httpClientProperties)
                .build();
    }

    private GitScmProviderRepository initProviderRepository() {
        IScmAuth auth = new PersonalAccessTokenScmAuth("YOUR_PERSONAL_ACCESS_TOKEN");

        return new GitScmProviderRepository()
                .withAuth(auth)
                .withUrl("https://github.com/bkdevops-projects/devops-scm.git");
    }

    /**
     * 获取仓库信息
     */
    public void getRepository() {
        ScmProviderProperties providerProperties = initProviderProperties();
        GitScmProviderRepository providerRepository = initProviderRepository();
        scmProviderManager.repositories(providerProperties).find(providerRepository);
    }

    /**
     * 获取分支信息
     */
    public void getBranch() {
        ScmProviderProperties providerProperties = initProviderProperties();
        GitScmProviderRepository providerRepository = initProviderRepository();
        scmProviderManager.refs(providerProperties).findBranch(providerRepository, "master");
    }

    /**
     * 获取tag信息
     */
    public void getTag() {
        ScmProviderProperties providerProperties = initProviderProperties();
        GitScmProviderRepository providerRepository = initProviderRepository();
        scmProviderManager.refs(providerProperties).findTag(providerRepository, "v1.0.0");
    }

    /**
     * 获取issue信息
     */
    public void getIssue() {
        ScmProviderProperties providerProperties = initProviderProperties();
        GitScmProviderRepository providerRepository = initProviderRepository();
        scmProviderManager.issues(providerProperties).find(providerRepository, 1);
    }

    /**
     * 获取pr信息
     */
    public void getPullRequest() {
        ScmProviderProperties providerProperties = initProviderProperties();
        GitScmProviderRepository providerRepository = initProviderRepository();
        scmProviderManager.pullRequests(providerProperties).find(providerRepository, 1);
    }

    /**
     * 获取用户信息
     */
    public void getUser() {
        ScmProviderProperties providerProperties = initProviderProperties();
        IScmAuth auth = new PersonalAccessTokenScmAuth("YOUR_PERSONAL_ACCESS_TOKEN");
        scmProviderManager.users(providerProperties).find(auth);
    }
}
