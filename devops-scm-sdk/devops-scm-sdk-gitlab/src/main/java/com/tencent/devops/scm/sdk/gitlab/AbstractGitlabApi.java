package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.util.UrlEncoder;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabProject;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class AbstractGitlabApi implements GitlabConstants {
    protected final GitlabApi gitlabApi;

    protected AbstractGitlabApi(GitlabApi gitlabApi) {
        this.gitlabApi = gitlabApi;
    }

    public String getProjectIdOrPath(Object value) {
        if (value instanceof Number && ((Number) value).longValue() > 0) {
            return value.toString();
        }
        if (value instanceof String && !((String) value).trim().isEmpty()) {
            return segment((String) value);
        }
        if (value instanceof GitlabProject) {
            GitlabProject project = (GitlabProject) value;
            if (project.getId() != null && project.getId() > 0) {
                return project.getId().toString();
            }
            if (project.getPathWithNamespace() != null && !project.getPathWithNamespace().isEmpty()) {
                return segment(project.getPathWithNamespace());
            }
        }
        throw new GitlabApiException("Cannot determine a positive project ID or path");
    }

    protected Requester request(Object projectIdOrPath, String path) {
        String project = getProjectIdOrPath(projectIdOrPath);
        return gitlabApi.createRequest().withUrlPath(path.replace("%s", project)).withRepoId(project);
    }

    protected String segment(String value) {
        return UrlEncoder.urlEncode(value);
    }

    protected int page(Integer value) {
        if (value == null) {
            return DEFAULT_PAGE;
        }
        if (value < 1) {
            throw new IllegalArgumentException("page must be positive");
        }
        return value;
    }

    protected int perPage(Integer value) {
        if (value == null) {
            return DEFAULT_PER_PAGE;
        }
        if (value < 1 || value > DEFAULT_PER_PAGE) {
            throw new IllegalArgumentException("perPage must be between 1 and 100");
        }
        return value;
    }

    protected String join(Collection<?> values) {
        return values == null ? null : values.stream().map(String::valueOf).collect(Collectors.joining(","));
    }
}
