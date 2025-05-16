## Overview
devops-scm提供了一套统一的源代码接口，使得开发者能够方便地调用各大源代码管理平台的接口和接收源码管理平台的webhook事件。这样开发者就无需熟悉每个平台的API文档和webhook事件请求体,只需通过devops-scm提供的标准接口即可实现对源代码平台的管理。

目前支持的提供商有-工蜂。后续会陆续对接github、gitlab、gitee、apache svn

## 代码目录说明
- devops-scm-api: api接口定义
- devops-scm-provider: 不同源代码管理平台对api接口的实现
- devops-scm-sdk: 封装源代码管理平台的原生api接口
- devops-scm-spring-boot-started: 集成spring boot
- devops-scm-sample: 使用示例
- devops-scm-test: 测试工具类

[详情介绍](./doc/PROJECT_STRUCTURE.md)

## 快速开始
### 引包
- Maven: pom.xml
    ```
    <dependency>
        <groupId>com.tencent.bk.devops.scm</groupId>
        <artifactId>devops-scm-spring-boot-starter</artifactId>
        <version>${version}</version>
    </dependency>
    ```
- Gradle: build.gradle.kts
    ```
    api("com.tencent.bk.devops.scm:devops-scm-spring-boot-starter:${version}")
    ```

### 使用示例

#### 1. 注入bean
  ```java
  @Autowired
  private ScmProviderManager scmProviderManager;
  ```
    
#### 2. 初始化提供者配置
  ```java
  private ScmProviderProperties initProviderProperties() {
        HttpClientProperties httpClientProperties = HttpClientProperties.builder()
                .apiUrl("https://api.github.com")
                .build();
        return ScmProviderProperties.builder()
                .providerCode(ScmProviderCodes.GITHUB.name())
                .httpClientProperties(httpClientProperties)
                .build();
    }
  ```
   
#### 3. 初始化代码库
  ```java
   private GitScmProviderRepository initProviderRepository() {
      IScmAuth auth = new PersonalAccessTokenScmAuth("YOUR_PERSONAL_ACCESS_TOKEN");
    
      return new GitScmProviderRepository()
              .withAuth(auth)
              .withUrl("https://github.com/bkdevops-projects/devops-scm.git");
  }
  ```

#### 4. 接口调用
- 获取代码库信息
    ```java
    public void getRepository() {
        ScmProviderProperties providerProperties = initProviderProperties();
        GitScmProviderRepository providerRepository = initProviderRepository();
        scmProviderManager.repositories(providerProperties).find(providerRepository);
    }
    ```
- 获取分支信息
    ```java
    public void getBranch() {
        ScmProviderProperties providerProperties = initProviderProperties();
        GitScmProviderRepository providerRepository = initProviderRepository();
        scmProviderManager.refs(providerProperties).findBranch(providerRepository, "master");
    }
    ```
- 获取tag信息
    ```java
    public void getTag() {
        ScmProviderProperties providerProperties = initProviderProperties();
        GitScmProviderRepository providerRepository = initProviderRepository();
        scmProviderManager.refs(providerProperties).findTag(providerRepository, "v1.0.0");
    }
    ```
- 获取issue信息
    ```java
    public void getIssue() {
        ScmProviderProperties providerProperties = initProviderProperties();
        GitScmProviderRepository providerRepository = initProviderRepository();
        scmProviderManager.issues(providerProperties).find(providerRepository, 1);
    }
    ```
- 获取pr信息
    ```java
     public void getPullRequest() {
        ScmProviderProperties providerProperties = initProviderProperties();
        GitScmProviderRepository providerRepository = initProviderRepository();
        scmProviderManager.pullRequests(providerProperties).find(providerRepository, 1);
    }
    ```
- 获取用户信息
    ```java
    public void getUser() {
        ScmProviderProperties providerProperties = initProviderProperties();
        IScmAuth auth = new PersonalAccessTokenScmAuth("YOUR_PERSONAL_ACCESS_TOKEN");
        scmProviderManager.users(providerProperties).find(auth);
    }
    ```

## 版本日志
[CHANGELOG](CHANGELOG.md)

## 新增源码管理平台
- [从零开始接入源码管理平台](./doc/ADD_PROVIDER.md)


- [已有sdk,补充适配模块](./doc/ADD_PROVIDER_SIMPLIFICATION.md)


## Contributing
- [腾讯开源激励计划](https://opensource.tencent.com/contribution) 鼓励开发者的参与和贡献，期待你的加入

## License
devops-scm 是基于 MIT 协议， 详细请参考 [LICENSE](LICENSE)

我们承诺未来不会更改适用于交付给任何人的当前项目版本的开源许可证（MIT 协议）。
