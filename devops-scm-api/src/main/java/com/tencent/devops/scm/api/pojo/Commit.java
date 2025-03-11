package com.tencent.devops.scm.api.pojo;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代表仓库提交
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commit {

    private String sha;
    private String message;
    private Signature author;
    private Signature committer;
    private LocalDateTime commitTime;
    private String link;
    private List<String> added;
    private List<String> modified;
    private List<String> removed;
}
