package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GiteeAssigne extends GiteeBaseUser {

    @JsonProperty("gists_url")
    private String gistsUrl;
    @JsonProperty("repos_url")
    private String reposUrl;
    @JsonProperty("following_url")
    private String followingUrl;
    private String remark;
    @JsonProperty("starred_url")
    private String starredUrl;
    private String login;
    @JsonProperty("followers_url")
    private String followersUrl;
    private String type;
    private String url;
    private Boolean codeOwner;
    private Boolean accept;
    @JsonProperty("subscriptions_url")
    private String subscriptionsUrl;
    @JsonProperty("received_events_url")
    private String receivedEventsUrl;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    @JsonProperty("events_url")
    private String eventsUrl;
    @JsonProperty("html_url")
    private String htmlUrl;
    private Long id;
    private Boolean assignee;
    @JsonProperty("organizations_url")
    private String organizationsUrl;
}
