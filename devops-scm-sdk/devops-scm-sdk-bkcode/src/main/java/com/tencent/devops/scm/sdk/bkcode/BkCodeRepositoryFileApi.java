package com.tencent.devops.scm.sdk.bkcode;

import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommitCompare;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;

public class BkCodeRepositoryFileApi extends AbstractBkCodeApi {
    private static final String FILE_CHANGE_LIST_URI_PATTERN = "repos/:id/compare/:diff_ref";

    public BkCodeRepositoryFileApi(BkCodeApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 对比提交文件变更信息
     * @param projectIdOrPath 仓库名
     */
    public BkCodeCommitCompare commitCompare(
            Object projectIdOrPath,
            String to,
            String from,
            Boolean straight
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        BkCodeCommitCompare commitCompare = bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        FILE_CHANGE_LIST_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("diff_ref", String.format("%s...%s", to, from))
                                .build()
                )
                .withRepoId(repoId)
                .with("straight", straight)
                .fetch(BkCodeCommitCompare.class);
        return commitCompare;
    }
}
