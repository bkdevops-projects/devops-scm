package com.tencent.devops.scm.provider.gitee.simple;

import com.gitee.sdk.gitee5j.ApiClient;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository;
import com.tencent.devops.scm.provider.gitee.simple.auth.GiteeTokenAuthProviderAdapter;

public class GiteeApiClientFactory {

    private String apiUrl;

    public GiteeApiClientFactory(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public ApiClient getClient(GitScmProviderRepository repository) {
        // 创建客户端
        ApiClient client = new ApiClient();
        // 设置基础路径
        client.setBasePath(apiUrl);
        // 绑定授权信信息
        new GiteeTokenAuthProviderAdapter(client).withAuth(repository.getAuth());
        return client;
    }
}
