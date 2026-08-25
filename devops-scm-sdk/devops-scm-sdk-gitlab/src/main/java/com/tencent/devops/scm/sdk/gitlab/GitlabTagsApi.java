package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabTag;
import java.util.Arrays;
import java.util.List;

public class GitlabTagsApi extends AbstractGitlabApi {
    GitlabTagsApi(GitlabApi api) {
        super(api);
    }

    public List<GitlabTag> getTags(Object project, String search, Integer page, Integer perPage) {
        GitlabTag[] result = request(project, "projects/%s/repository/tags").method(ScmHttpMethod.GET)
                .with("search", search).with(PAGE_PARAM, page(page)).with(PER_PAGE_PARAM, perPage(perPage))
                .fetch(GitlabTag[].class);
        return Arrays.asList(result);
    }

    public GitlabTag getTag(Object project, String tag) {
        return request(project, "projects/%s/repository/tags/" + segment(tag)).method(ScmHttpMethod.GET)
                .fetch(GitlabTag.class);
    }

    public GitlabTag createTag(Object project, String tag, String ref, String message) {
        return request(project, "projects/%s/repository/tags").method(ScmHttpMethod.POST)
                .with("tag_name", tag).with("ref", ref).with("message", message).fetch(GitlabTag.class);
    }

    public void deleteTag(Object project, String tag) {
        request(project, "projects/%s/repository/tags/" + segment(tag)).method(ScmHttpMethod.DELETE).send();
    }
}
