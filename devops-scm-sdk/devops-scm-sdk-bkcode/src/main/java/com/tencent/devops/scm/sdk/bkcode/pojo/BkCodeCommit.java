package com.tencent.devops.scm.sdk.bkcode.pojo;

import lombok.Data;
import java.util.List;

@Data
public class BkCodeCommit {
    private String id;
    private BkCodeAuthor author;
    private BkCodeCommitter committer;
    private String title;
    private String message;
    private List<String> parents;
}
