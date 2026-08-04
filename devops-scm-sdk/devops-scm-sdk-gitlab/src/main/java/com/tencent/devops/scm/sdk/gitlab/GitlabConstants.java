package com.tencent.devops.scm.sdk.gitlab;

public interface GitlabConstants {
    String PAGE_PARAM = "page";
    String PER_PAGE_PARAM = "per_page";
    String PRIVATE_TOKEN_HEADER = "PRIVATE-TOKEN";
    String AUTHORIZATION_HEADER = "Authorization";
    String BEARER_PREFIX = "Bearer ";
    String REQUEST_ID_HEADER = "X-Request-Id";
    String TOTAL_HEADER = "X-Total";
    String TOTAL_PAGES_HEADER = "X-Total-Pages";
    String PER_PAGE_HEADER = "X-Per-Page";
    String PAGE_HEADER = "X-Page";
    String NEXT_PAGE_HEADER = "X-Next-Page";
    String PREV_PAGE_HEADER = "X-Prev-Page";
    int DEFAULT_PAGE = 1;
    int DEFAULT_PER_PAGE = 100;
}
