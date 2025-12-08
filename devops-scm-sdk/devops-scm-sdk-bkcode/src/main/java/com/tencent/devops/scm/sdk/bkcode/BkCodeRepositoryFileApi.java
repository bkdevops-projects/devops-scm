package com.tencent.devops.scm.sdk.bkcode;

import static com.tencent.devops.scm.sdk.common.util.UrlEncoder.urlEncodeUtf8;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeFileContent;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;

public class BkCodeRepositoryFileApi extends AbstractBkCodeApi {
    private static final String FILE_CONTENT_URI_PATTERN = "repos/:id/contents/:file_path";

    public BkCodeRepositoryFileApi(BkCodeApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 获取文件内容
     * @param projectIdOrPath 仓库名
     */
    public BkCodeFileContent getFileContent(
            Object projectIdOrPath,
            String filePath,
            String ref
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        FILE_CONTENT_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("file_path", urlEncodeUtf8(filePath))
                                .build()
                )
                .withRepoId(repoId)
                .with("ref", ref)
                .fetch(new TypeReference<>() {});
    }
}
