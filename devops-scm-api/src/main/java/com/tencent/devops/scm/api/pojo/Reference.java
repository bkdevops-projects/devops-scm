package com.tencent.devops.scm.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代表一个git引用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reference {
    private String name;
    private String sha;
    private String linkUrl;
    private String path;
}
