package com.tencent.devops.scm.sdk.bkcode;

import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeDiffFile;

public interface BkCodeConstants {

    /**
     * 每页的项目数
     */
    String PER_PAGE_PARAM = "pageSize";

    /**
     * 当前页面的索引 (从 1 开始)
     */
    String PAGE_PARAM = "page";

    /**
     * OAuth2 授权请求头KEY
     */
    String OAUTH_TOKEN_HEADER = "Authorization";
    String PERSONAL_ACCESS_TOKEN_HEADER = "X-BKCODE-PRIVATE-TOKEN";

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
    /**
     * 默认文件路径，基于此路径推断文件变更操作是：
     * - 新增: srcPath 为此值
     * - 修改: srcPath 与 dstPath 均不为此值
     * - 删除: dstPath 为此值
     * @see BkCodeDiffFile#srcPath
     * @see BkCodeDiffFile#dstPath
     */
    String DEFAULT_FILE_PATH = "/dev/null";
    String SIGNATURE_HEADER = "x-bkcode-signature-256";
    String SIGNATURE_PARAM_PREFIX = "sha256=";
    enum TokenType {
        OAUTH2_ACCESS, PERSONAL_ACCESS;
    }


    enum SortType {
        DESC, ASC;
    }
}
