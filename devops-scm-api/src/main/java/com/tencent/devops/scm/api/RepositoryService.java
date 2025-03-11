package com.tencent.devops.scm.api;

import com.tencent.devops.scm.api.pojo.Hook;
import com.tencent.devops.scm.api.pojo.HookInput;
import com.tencent.devops.scm.api.pojo.ListOptions;
import com.tencent.devops.scm.api.pojo.Perm;
import com.tencent.devops.scm.api.pojo.RepoListOptions;
import com.tencent.devops.scm.api.pojo.Status;
import com.tencent.devops.scm.api.pojo.StatusInput;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository;
import java.util.List;

public interface RepositoryService {

    /**
     * 查找代码库通过代码库名
     *
     * @param repository 代码库名
     * @return 代码库信息
     */
    ScmServerRepository find(ScmProviderRepository repository);

    /**
     * 获取用户权限
     *
     * @param username 用户名
     */
    Perm findPerms(ScmProviderRepository repository, String username);

    /**
     * 获取代码库列表
     *
     * @param opts 仓库列表查下参数
     * @return 代码库列表
     */
    List<ScmServerRepository> list(IScmAuth auth, RepoListOptions opts);

    List<Hook> listHooks(ScmProviderRepository repository, ListOptions opts);

    Hook createHook(ScmProviderRepository repository, HookInput input);

    Hook updateHook(ScmProviderRepository repository, Long hookId, HookInput input);

    Hook getHook(ScmProviderRepository repository, Long hookId);

    void deleteHook(ScmProviderRepository repository, Long hookId);

    List<Status> listStatus(ScmProviderRepository repository, String ref, ListOptions opts);

    Status createStatus(ScmProviderRepository repository, String ref, StatusInput input);
}
