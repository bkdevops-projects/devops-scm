package com.tencent.devops.scm.sdk.gitlab.pojo.webhook;

import java.util.List;
import lombok.Data;

@Data
public class GitlabEventCommit {
    private String id;
    private String sha;
    private String message;
    private String timestamp;
    private String url;
    private String webUrl;
    private GitlabEventUser author;
    private String authorName;
    private String authorEmail;
    private List<String> added;
    private List<String> modified;
    private List<String> removed;
}
