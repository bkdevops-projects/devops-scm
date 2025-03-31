package com.tencent.devops.scm.sdk.gitee;

import com.tencent.devops.scm.sdk.common.util.UrlEncoder;

/**
 * 作用：Gitee API的抽象基类，提供公共功能
 * 核心功能：
 *   - 封装项目ID/路径的转换逻辑（getProjectIdOrPath）
 *   - 提供URL编码工具方法（urlEncode）
 */
public class AbstractGiteeApi implements GiteeConstants {

    protected final GiteeApi giteeApi;

    public AbstractGiteeApi(GiteeApi giteeApi) {
        this.giteeApi = giteeApi;
    }

    /**
     * 转换obj对象获取项目ID或者仓库名
     *
     * @param obj 仓库名
     * @return 项目ID或仓库名
     */
    public String getProjectIdOrPath(Object obj) {
        if (obj == null) {
            throw new GiteeApiException("Cannot determine ID or path from null object");
        } else if (obj instanceof String) {
            return (String) obj;
        } else {
            throw new GiteeApiException("Cannot determine ID or path from provided " + obj.getClass().getSimpleName()
                    + " instance, must be Integer, String, or a Project instance");
        }
    }

    protected String urlEncode(String s) {
        return UrlEncoder.urlEncode(s);
    }
}
