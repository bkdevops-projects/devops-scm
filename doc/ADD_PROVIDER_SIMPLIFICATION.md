# 新增源码管理平台

当前版本已内置支持腾讯TGIT代码库，如已有其他平台SDK，则仅需补充适配模块即可，本文以[giteeSdk](https://gitee.com/sdk/gitee5j.git)为例，演示操作流程

参考：[项目结构介绍](./PROJECT_STRUCTURE.md)

## 1.指定服务提供者标识
````java
/**
 * 源码管理平台提供者
 */
public enum ScmProviderCodes {
    TGIT("tgit"),
    TSVN("tsvn"),
    GITHUB("github"),
    GITLAB("gitlab"),
    // 增加Gitee提供者标识
    GITEE("gitee"),
    ;
    public final String value;

    ScmProviderCodes(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
````

## 2. 平台适配模块开发
此模块主要用于将对 [ devops-scm-sdk-{scmCode}] 模块与 [devops-scm-api] 模块以及第三方服务进行平滑对接，主要包含以下功能：
- [devops-scm-sdk] 模块的原始请求数据转化为 [devops-scm-api] 模块所定义的数据类型，
- 同时用于解析 webhook元数据 ，组装webhook触发参数以及关联的要素信息
- 将蓝盾平台的凭证转化为sdk支持的授权提供者

### 2.1 新建module
增加 devops-scm-provider-gitee 模块，基于[devops-scm-provider/devops-scm-provider-git]目录右键 'New' -> 'Module'

参考: [增加 devops-scm-sdk-gitee 模块](#211-增加-devops-scm-sdk-gitee-模块)

### 2.2 增加授权适配器

### 2.5 服务功能验证

### 2.6 Webhook支持

## 3. spring-boot自动装配

## 4. 服务打包