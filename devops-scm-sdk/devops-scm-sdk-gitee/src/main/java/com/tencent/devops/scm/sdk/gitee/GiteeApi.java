package com.tencent.devops.scm.sdk.gitee;

import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import lombok.Getter;

/**
 * Gitee API入口类，提供所有Gitee API功能的统一访问点
 * 核心功能：
 *      -管理各种API子模块的懒加载（如项目、仓库、分支等API）
 *      -使用双重检查锁实现线程安全的单例模式
 *      -提供统一的请求构造器（createRequest）
 */
public class GiteeApi {

    @Getter
    private final GiteeApiClient client;
    private volatile GiteeBranchesApi branchesApi;
    private volatile GiteePullRequestApi pullRequestApi;
    private volatile GiteeRepositoryFileApi fileApi;
    private volatile GiteeProjectApi projectApi;
    private volatile GiteeCommitApi commitApi;
    private volatile GiteeTagApi tagApi;
    private volatile GiteeUserApi userApi;
    private volatile GiteeCheckRunApi checkRunApi;


    public GiteeApi(GiteeApiClient client) {
        this.client = client;
    }

    public GiteeApi(String apiUrl, ScmConnector connector, HttpAuthProvider authorizationProvider) {
        this.client = new GiteeApiClient(apiUrl, connector, authorizationProvider);
    }

    Requester createRequest() {
        return new Requester(client);
    }

    public GiteeBranchesApi getBranchesApi() {
        if (branchesApi == null) {
            synchronized (this) {
                if (branchesApi == null) {
                    branchesApi = new GiteeBranchesApi(this);
                }
            }
        }
        return branchesApi;
    }

    public GiteePullRequestApi getPullRequestApi() {
        if (pullRequestApi == null) {
            synchronized (this) {
                if (pullRequestApi == null) {
                    pullRequestApi = new GiteePullRequestApi(this);
                }
            }
        }
        return pullRequestApi;
    }

    public GiteeRepositoryFileApi getFileApi() {
        if (fileApi == null) {
            synchronized (this) {
                if (fileApi == null) {
                    fileApi = new GiteeRepositoryFileApi(this);
                }
            }
        }
        return fileApi;
    }

    public GiteeProjectApi getProjectApi() {
        if (projectApi == null) {
            synchronized (this) {
                if (projectApi == null) {
                    projectApi = new GiteeProjectApi(this);
                }
            }
        }
        return projectApi;
    }

    public GiteeCommitApi getCommitApi() {
        if (commitApi == null) {
            synchronized (this) {
                if (commitApi == null) {
                    commitApi = new GiteeCommitApi(this);
                }
            }
        }
        return commitApi;
    }

    public GiteeTagApi getTagApi() {
        if (tagApi == null) {
            synchronized (this) {
                if (tagApi == null) {
                    tagApi = new GiteeTagApi(this);
                }
            }
        }
        return tagApi;
    }

    public GiteeUserApi getUserApi() {
        if (userApi == null) {
            synchronized (this) {
                if (userApi == null) {
                    userApi = new GiteeUserApi(this);
                }
            }
        }
        return userApi;
    }

    public GiteeCheckRunApi getCheckRunApi() {
        if (checkRunApi == null) {
            synchronized (this) {
                if (checkRunApi == null) {
                    checkRunApi = new GiteeCheckRunApi(this);
                }
            }
        }
        return checkRunApi;
    }
}
