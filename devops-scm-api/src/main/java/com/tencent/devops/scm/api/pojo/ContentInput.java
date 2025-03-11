package com.tencent.devops.scm.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentInput {
    // 提交的分支
    private String ref;
    // 提交message
    private String message;
    // 文件内容
    private String content;
}
