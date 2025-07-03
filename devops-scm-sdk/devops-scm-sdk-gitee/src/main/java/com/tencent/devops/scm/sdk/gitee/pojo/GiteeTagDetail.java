package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiteeTagDetail {
    private Asset[] assets;
    private String tagName;
    private Boolean prerelease;
    private GiteeBaseUser author;
    @JsonProperty("target_commitish")
    private String targetCommitish;
    private String name;
    @JsonProperty("created_at")
    private Date createdAt;
    private Long id;
    private String body;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Asset {
        private String name;
        @JsonProperty("browser_download_url")
        private String browserDownloadUrl;
    }
}


