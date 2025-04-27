package com.tencent.devops.scm.sdk.tsvn;

import lombok.Getter;

public interface TSvnConstants {

    /**
     * 每页的项目数
     */
    public static final String PER_PAGE_PARAM = "per_page";

    /**
     * 当前页面的索引 (从 1 开始)
     */
    public static final String PAGE_PARAM = "page";

    public static final String PRIVATE_TOKEN_HEADER = "PRIVATE-TOKEN";

    public static final String OAUTH_TOKEN_HEADER = "OAUTH-TOKEN";

    /**
     * The total number of items HTTP header key.
     */
    public static final String TOTAL_HEADER = "X-Total";

    /**
     * The total number of pages HTTP header key.
     */
    public static final String TOTAL_PAGES_HEADER = "X-Total-Pages";

    /**
     * The number of items per page HTTP header key.
     */
    public static final String PER_PAGE = "X-Per-Page";

    /**
     * The index of the current page (starting at 1) HTTP header key.
     */
    public static final String PAGE_HEADER = "X-Page";

    /**
     * The index of the next page HTTP header key.
     */
    public static final String NEXT_PAGE_HEADER = "X-Next-Page";

    /**
     * The index of the previous page HTTP header key.
     */
    public static final String PREV_PAGE_HEADER = "X-Prev-Page";

    /** 默认页 */
    public static final int DEFAULT_PAGE = 1;
    /** 每页默认数 */
    public static final int DEFAULT_PER_PAGE = 100;

    // 闭源项目
    public static final int VISIBILITY_PRIVATE = 0;
    // 内部项目
    public static final int VISIBILITY_INTERNAL = 10;
    public static final String HOOK_SOURCE_TYPE = "X-Source-Type";
    // 测试HOOK，无需触发ci
    public static final String TEST_HOOK_SOURCE_TYPE_VALUE = "Test";


    public enum TokenType {
        OAUTH2_ACCESS, PERSONAL_ACCESS, PRIVATE;
    }

    public enum SortOrder {
        ASC("asc"),
        DESC("desc");

        @Getter
        private String value;

        SortOrder(String value) {
            this.value = value;
        }
    }
}
