package com.tencent.devops.scm.api.pojo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hook {

    private Long id;
    private String name;
    private String url;
    private HookEvents events;
    // 在HookEvents中不包含的事件类型
    private List<String> nativeEvents;
    private Boolean active;
    private Boolean skipVerify;
    // 监听仓库的相对路径
    private String path;
}
