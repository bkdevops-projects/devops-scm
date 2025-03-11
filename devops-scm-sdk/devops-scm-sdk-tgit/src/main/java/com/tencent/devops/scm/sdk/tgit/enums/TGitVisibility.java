package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 项目可见性
 *
 * <pre>工蜂Git系统的项目级别有私有、内部、公共三种。你能够通过定义项目visibility_level属性来决定项目级别。
 * 1. 内部版本
 *  - 私有项目，visibility_level = 0，项目的访问必须显式授予每个用户
 *  - 公共项目，visibility_level = 10，任何登录用户都可以克隆该项目
 * 2. 非内部版本
 *  - 私有项目，visibility_level = 0，项目的访问必须显式授予每个用户
 *  - 内部项目，visibility_level = 10，任何登录用户都可以克隆该项目
 *  - 公共项目，visibility_level = 20，可以不经任何身份验证克隆该项目。
 *  </pre>
 */
public enum TGitVisibility {
    // 私有项目,项目的访问必须显式授予每个用户
    PRIVATE(0),
    // 内部项目
    INTERNAL(10),
    // 公共项目
    PUBLIC(20);

    private final Integer value;

    TGitVisibility(int value) {
        this.value = value;
    }

    @JsonValue
    public Integer toValue() {
        return value;
    }

    public static TGitVisibility forValue(Integer value) {
        for (TGitVisibility visibility : TGitVisibility.values()) {
            if (visibility.value.equals(value)) {
                return visibility;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
