package com.tencent.devops.scm.sdk.bkcode;

import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeDiffFileRevRange;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommitDetail;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeDiff;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommit;
import java.util.Arrays;
import java.util.List;

public class BkCodeCommitApi extends AbstractBkCodeApi {
    private static final String COMMIT_URI_PATTERN = "repos/:id/commits/:sha";
    private static final String COMMIT_COMPARE_URI_PATTERN = "repos/:id/diff/files";

    public BkCodeCommitApi(BkCodeApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 获取commit
     * @param projectIdOrPath 仓库名
     */
    public BkCodeCommitDetail getCommit(Object projectIdOrPath, String sha) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        COMMIT_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("sha", urlEncode(sha))
                                .build()
                )
                .withRepoId(repoId)
                .fetch(BkCodeCommitDetail.class);
    }

    /**
     * 获取差异文件列表
     */
    public BkCodeDiff compare(
            Object projectIdOrPath,
            String targetRev,
            String sourceRev,
            BkCodeDiffFileRevRange revRange,
            Boolean ignoreWhitespace
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        COMMIT_COMPARE_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("targetRev", targetRev)
                .with("sourceRev", sourceRev)
                .with("revRange", revRange.getValue())
                .with("ignoreWhitespace", ignoreWhitespace)
                .fetch(BkCodeDiff.class);
    }
}
