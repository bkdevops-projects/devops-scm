package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.PagedIterable;
import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.UriTemplate;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMember;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitNamespace;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProject;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProjectHook;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class TGitProjectApi extends AbstractTGitApi {

    // 项目请求uri
    private static final String PROJECTS_URI_PATTERN = "projects";
    private static final String PROJECT_ID_URI_PATTERN = "projects/:id";
    private static final String PROJECT_HOOK_URI_PATTERN = "projects/:id/hooks";
    private static final String PROJECT_HOOK_ID_URI_PATTERN = "projects/:id/hooks/:hook_id";
    private static final String PROJECT_ID_MEMBERS_PATTERN = "projects/:id/members";
    private static final String PROJECT_ID_MEMBERS_ALL_PATTERN = "projects/:id/members/all";

    public TGitProjectApi(TGitApi tGitApi) {
        super(tGitApi);
    }

    public List<TGitProject> getProjects(Integer page, Integer perPage) {
        TGitProject[] list = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(PROJECTS_URI_PATTERN)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(TGitProject[].class);
        return Arrays.asList(list);
    }

    public PagedIterable<TGitProject> getProjects(int itemsPerPage) {
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(PROJECTS_URI_PATTERN)
                .with(PER_PAGE_PARAM, itemsPerPage)
                .toIterable(TGitProject[].class);
    }

    public List<TGitProject> getProjects(String search, Integer page, Integer perPage) {
        TGitProject[] list = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(PROJECTS_URI_PATTERN)
                .with("search", search)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage == null ? DEFAULT_PER_PAGE : perPage)
                .fetch(TGitProject[].class);
        return Arrays.asList(list);
    }

    public TGitProject getProject(Object projectIdOrPath) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .fetch(TGitProject.class);
    }

    public TGitProject getProject(String namespace, String name) {
        if (namespace == null) {
            throw new TGitApiException("namespace cannot be null");
        }

        if (name == null) {
            throw new TGitApiException("name cannot be null");
        }
        String projectPath = null;
        try {
            projectPath = URLEncoder.encode(namespace + "/" + name, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            throw new TGitApiException(uee);
        }
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", projectPath)
                                .build()
                )
                .withRepoId(projectPath)
                .fetch(TGitProject.class);
    }

    public TGitProject createProject(TGitProject project) {
        if (project == null) {
            return null;
        }
        String name = project.getName();

        if (name == null || name.isEmpty()) {
            return null;
        }
        Requester requester = tGitApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(PROJECTS_URI_PATTERN)
                .with("name", name)
                .with("path", project.getPath())
                .with("fork_enabled", project.getForkEnabled())
                .with("description", project.getDescription())
                .with("visibility_level", project.getVisibilityLevel())
                .with("create_from_id", project.getCreatedFromId());
        TGitNamespace namespace = project.getNamespace();
        if (namespace != null && namespace.getId() != null) {
            requester.with("namespace_id", namespace.getId());
        }
        return requester.fetch(TGitProject.class);
    }

    public TGitProject updateProject(TGitProject project) {
        if (project == null) {
            throw new TGitApiException("Project instance cannot be null.");
        }
        String repoId = getProjectIdOrPath(project);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.PUT)
                .withUrlPath(
                        PROJECT_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("name", project.getName())
                .with("fork_enabled", project.getForkEnabled())
                .with("description", project.getDescription())
                .with("default_branch", project.getDefaultBranch())
                .with("issues_enabled", project.getIssuesEnabled())
                .with("merge_requests_enabled", project.getMergeRequestsEnabled())
                .with("wiki_enabled", project.getWikiEnabled())
                .with("review_enabled", project.getReviewEnabled())
                .with("tag_name_regex", project.getTagNameRegex())
                .with("tag_create_push_level", project.getTagCreatePushLevel())
                .with("visibility_level", project.getVisibilityLevel())
                .with("template_repository", project.getTemplateRepository())
                .with("allow_skip_reviewer", project.getAllowSkipReviewer())
                .with("allow_skip_owner", project.getAllowSkipOwner())
                .with("allow_skip_mr_check", project.getAllowSkipMrCheck())
                .with("auto_intelligent_review_enabled", project)
                .fetch(TGitProject.class);
    }

    public void deleteProject(Object projectIdOrPath) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        tGitApi.createRequest()
                .method(ScmHttpMethod.DELETE)
                .withUrlPath(
                        PROJECT_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .send();
    }

    public TGitProjectHook addHook(Object projectIdOrPath, TGitProjectHook enabledHooks) {
        return addHook(projectIdOrPath, enabledHooks, null);
    }

    public TGitProjectHook addHook(Object projectIdOrPath, TGitProjectHook enabledHooks, String secretToken) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        PROJECT_HOOK_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("url", enabledHooks.getUrl())
                .with("push_events", enabledHooks.getPushEvents())
                .with("tag_push_events", enabledHooks.getTagPushEvents())
                .with("issues_events", enabledHooks.getIssuesEvents())
                .with("merge_requests_events", enabledHooks.getMergeRequestsEvents())
                .with("note_events", enabledHooks.getNoteEvents())
                .with("review_events", enabledHooks.getReviewEvents())
                .with("token", secretToken)
                .fetch(TGitProjectHook.class);
    }

    public TGitProjectHook updateHook(Object projectIdOrPath, Long hookId, TGitProjectHook enabledHooks,
            String secretToken) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.PUT)
                .withUrlPath(
                        PROJECT_HOOK_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("hook_id", hookId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .with("url", enabledHooks.getUrl())
                .with("push_events", enabledHooks.getPushEvents())
                .with("tag_push_events", enabledHooks.getTagPushEvents())
                .with("issues_events", enabledHooks.getIssuesEvents())
                .with("merge_requests_events", enabledHooks.getMergeRequestsEvents())
                .with("note_events", enabledHooks.getNoteEvents())
                .with("review_events", enabledHooks.getReviewEvents())
                .with("token", secretToken)
                .fetch(TGitProjectHook.class);
    }

    public TGitProjectHook getHook(Object projectIdOrPath, Long hookId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        String urlPath = UriTemplate.from(PROJECT_HOOK_ID_URI_PATTERN)
                .with("id", repoId)
                .with("hook_id", hookId)
                .expand();
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_HOOK_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("hook_id", hookId.toString())
                                .build()
                )
                .withUrlPath(urlPath)
                .withRepoId(repoId)
                .fetch(TGitProjectHook.class);
    }

    public List<TGitProjectHook> getHooks(Object projectIdOrPath) {
        return getHooks(projectIdOrPath, DEFAULT_PAGE, DEFAULT_PER_PAGE);
    }

    public List<TGitProjectHook> getHooks(Object projectIdOrPath,
            Integer page, Integer perPage) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TGitProjectHook[] data = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_HOOK_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(TGitProjectHook[].class);
        return Arrays.asList(data);
    }

    public void deleteHook(Object projectIdOrPath, Long hookId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        tGitApi.createRequest()
                .method(ScmHttpMethod.DELETE)
                .withUrlPath(
                        PROJECT_HOOK_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("hook_id", hookId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .send();
    }

    public List<TGitMember> getProjectMembers(Object projectIdOrPath, String query, Integer page, Integer perPage) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TGitMember[] data = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_ID_MEMBERS_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .with(query, query)
                .fetch(TGitMember[].class);
        return Arrays.asList(data);
    }

    public List<TGitMember> getAllMembers(Object projectIdOrPath, String query, Integer page, Integer perPage) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TGitMember[] data = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_ID_MEMBERS_ALL_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .with(query, query)
                .fetch(TGitMember[].class);
        return Arrays.asList(data);
    }

    public TGitMember getMember(Object projectIdOrPath, String username) {
        List<TGitMember> members = getAllMembers(projectIdOrPath, username, DEFAULT_PAGE, DEFAULT_PER_PAGE);
        if (members == null || members.isEmpty()) {
            return null;
        }
        for (TGitMember member : members) {
            if (username.equals(member.getUsername())) {
                return member;
            }
        }
        return null;
    }
}
