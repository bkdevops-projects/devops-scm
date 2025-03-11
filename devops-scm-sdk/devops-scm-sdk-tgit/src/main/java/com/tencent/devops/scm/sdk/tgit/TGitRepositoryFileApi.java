package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.tgit.enums.TGitEncoding;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitDiff;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitRepositoryFile;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitTreeItem;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class TGitRepositoryFileApi extends AbstractTGitApi {


    // 文件请求uri
    private static final String FILES_URI_PATTERN = "projects/:id/repository/files";
    // 文件树请求uri
    private static final String TREE_URI_PATTERN = "projects/:id/repository/tree";
    // 获取差异文件列表
    private static final String FILE_CHANGE_LIST_URI_PATTERN = "projects/:id/repository/compare/changed_files/list";

    public TGitRepositoryFileApi(TGitApi tGitApi) {
        super(tGitApi);
    }

    public TGitRepositoryFile getFile(Object projectIdOrPath, String filePath, String ref) {
        if (StringUtils.isEmpty(ref)) {
            throw new IllegalArgumentException("ref can not be empty");
        }
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        FILES_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("ref", ref)
                .with("file_path", filePath)
                .fetch(TGitRepositoryFile.class);
    }

    public void createFile(Object projectIdOrPath, String filePath, String branchName, String content,
            String commitMessage) {
        createFile(projectIdOrPath, filePath, branchName, TGitEncoding.TEXT, content, commitMessage);
    }

    public void createFile(Object projectIdOrPath, String filePath, String branchName, TGitEncoding encoding,
            String content, String commitMessage) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        tGitApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        FILES_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("file_path", filePath)
                .with("branch_name", branchName)
                .with("encoding", encoding.toValue())
                .with("content", content)
                .with("commit_message", commitMessage)
                .send();
    }

    public void updateFile(Object projectIdOrPath, String filePath, String branchName, String content,
            String commitMessage) {
        updateFile(projectIdOrPath, filePath, branchName, TGitEncoding.TEXT, content, commitMessage);
    }

    public void updateFile(Object projectIdOrPath, String filePath, String branchName, TGitEncoding encoding,
            String content, String commitMessage) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        tGitApi.createRequest()
                .method(ScmHttpMethod.PUT)
                .withUrlPath(
                        FILES_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("file_path", filePath)
                .with("branch_name", branchName)
                .with("encoding", encoding.toValue())
                .with("content", content)
                .with("commit_message", commitMessage)
                .send();
    }

    public void deleteFile(Object projectIdOrPath, String filePath, String branchName, String commitMessage) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        tGitApi.createRequest()
                .method(ScmHttpMethod.DELETE)
                .withUrlPath(
                        FILES_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("file_path", filePath)
                .with("branch_name", branchName)
                .with("commit_message", commitMessage)
                .send();
    }

    /**
     * 获取版本库文件和目录列表
     */
    public List<TGitTreeItem> getTree(Object projectIdOrPath, String filePath, String refName, Boolean recursive) {
        if (StringUtils.isEmpty(refName)) {
            throw new IllegalArgumentException("ref can not be empty");
        }
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TGitTreeItem[] treeItems = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        TREE_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("ref_name", refName)
                .with("path", filePath)
                .with("recursive", recursive)
                .fetch(TGitTreeItem[].class);
        return Arrays.asList(treeItems);
    }

    /**
     * 获取差异文件列表
     */
    public List<TGitDiff> getFileChange(Object projectIdOrPath, String to, String from, Boolean straight,
            Boolean calcLines, Integer page, Integer perPage) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TGitDiff[] treeItems = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        FILE_CHANGE_LIST_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("to", to)
                .with("from", from)
                .with("straight", straight)
                .with("calc_lines", calcLines)
                .with("straight", straight)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(TGitDiff[].class);
        return Arrays.asList(treeItems);
    }
}
