package com.tencent.devops.scm.sdk.bkcode;

import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeResult;
import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.ResponseResult;
import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import lombok.Getter;

/**
 * BkCode API入口类，提供所有BkCode API功能的统一访问点
 * 核心功能：
 *      -管理各种API子模块的懒加载（如项目、仓库、分支等API）
 *      -使用双重检查锁实现线程安全的单例模式
 *      -提供统一的请求构造器（createRequest）
 */
public class BkCodeApi {

    @Getter
    private final BkCodeApiClient client;
    private volatile BkCodeBranchesApi branchesApi;
    private volatile BkCodeMergeRequestApi mergeRequestApi;
    private volatile BkCodeRepositoryFileApi fileApi;
    private volatile BkCodeProjectApi projectApi;
    private volatile BkCodeCommitApi commitApi;
    private volatile BkCodeTagApi tagApi;
    private volatile BkCodeUserApi userApi;


    public BkCodeApi(BkCodeApiClient client) {
        this.client = client;
    }

    public BkCodeApi(String apiUrl, ScmConnector connector, HttpAuthProvider authorizationProvider) {
        this.client = new BkCodeApiClient(apiUrl, connector, authorizationProvider);
    }

    /**
     * 创建请求，使用默认外层响应结构
     * @see BkCodeResult
     */
    Requester createRequest() {
        Requester requester = new Requester(client);
        requester.withResult(BkCodeResult.class);
        return requester;
    }

    /**
     * 创建请求，自定义外层响应结构
     * @param useResult 是否使用外层响应结构
     * @return 请求对象
     */
    <T extends ResponseResult>  Requester createRequest(boolean useResult, Class<T> responseResultCls) {
        Requester requester = new Requester(client);
        if (useResult) {
            requester.withResult(responseResultCls);
        }
        return requester;
    }

    public BkCodeBranchesApi getBranchesApi() {
        if (branchesApi == null) {
            synchronized (this) {
                if (branchesApi == null) {
                    branchesApi = new BkCodeBranchesApi(this);
                }
            }
        }
        return branchesApi;
    }

    public BkCodeMergeRequestApi getMergeRequestApi() {
        if (mergeRequestApi == null) {
            synchronized (this) {
                if (mergeRequestApi == null) {
                    mergeRequestApi = new BkCodeMergeRequestApi(this);
                }
            }
        }
        return mergeRequestApi;
    }

    public BkCodeRepositoryFileApi getFileApi() {
        if (fileApi == null) {
            synchronized (this) {
                if (fileApi == null) {
                    fileApi = new BkCodeRepositoryFileApi(this);
                }
            }
        }
        return fileApi;
    }

    public BkCodeProjectApi getProjectApi() {
        if (projectApi == null) {
            synchronized (this) {
                if (projectApi == null) {
                    projectApi = new BkCodeProjectApi(this);
                }
            }
        }
        return projectApi;
    }

    public BkCodeCommitApi getCommitApi() {
        if (commitApi == null) {
            synchronized (this) {
                if (commitApi == null) {
                    commitApi = new BkCodeCommitApi(this);
                }
            }
        }
        return commitApi;
    }

    public BkCodeTagApi getTagApi() {
        if (tagApi == null) {
            synchronized (this) {
                if (tagApi == null) {
                    tagApi = new BkCodeTagApi(this);
                }
            }
        }
        return tagApi;
    }

    public BkCodeUserApi getUserApi() {
        if (userApi == null) {
            synchronized (this) {
                if (userApi == null) {
                    userApi = new BkCodeUserApi(this);
                }
            }
        }
        return userApi;
    }
}
