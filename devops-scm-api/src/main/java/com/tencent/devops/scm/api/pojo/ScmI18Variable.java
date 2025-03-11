package com.tencent.devops.scm.api.pojo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * scm i18变量,只声明code,118转换在bk-ci工程，运行时转换
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScmI18Variable {

    // i18 code
    private String code;
    // 转换参数
    private List<String> params;
    private String defaultMessage;
}
