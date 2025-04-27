package com.tencent.devops.scm.sdk.gitee;

public interface GiteeConstants {

    /**
     * 每页的项目数
     */
    String PER_PAGE_PARAM = "per_page";

    /**
     * 当前页面的索引 (从 1 开始)
     */
    String PAGE_PARAM = "page";

    /**
     * OAuth2 授权请求头KEY
     */
    String OAUTH_TOKEN_HEADER = "Authorization";

    /**
     * 请求结果中的响应头 - 数据总条数
     */
    String TOTAL_HEADER = "total_count";

    /**
     * 请求结果中的响应头 - 数据总页数
     */
    String TOTAL_PAGES_HEADER = "total_page";

    /**
     * 默认页
     */
    int DEFAULT_PAGE = 1;

    /**
     * 每页默认数
     */
    int DEFAULT_PER_PAGE = 100;

    public enum TokenType {
        OAUTH2_ACCESS, PERSONAL_ACCESS;
    }
}
