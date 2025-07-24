package com.tencent.devops.scm.sdk.gitee.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiteeTag {
    private GiteeTagTagger tagger;
    private String name;
    private GiteeTagCommit commit;
    private String message;
}
