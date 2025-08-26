package com.tencent.devops.scm.sdk.tgit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.common.connector.okhttp3.OkHttpScmConnector;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.GiteeConstants.TokenType;
import com.tencent.devops.scm.sdk.gitee.auth.GiteeTokenAuthProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;

/**
 * 基础的tgit测试类
 */
public class AbstractGiteeTest {
    // 测试仓库
    protected static final String TEST_PROJECT_NAME = "hejieehe/private_repo";
    // 测试分支名
    protected static final String TEST_DEFAULT_BRANCH = "master";
    // [环境变量KEY]接口地址
    protected static final String TEST_GITEE_API_URL = "TEST_GITEE_API_URL";
    // [环境变量KEY]授权token
    protected static final String TEST_GITEE_PRIVATE_TOKEN = "TEST_GITEE_PRIVATE_TOKEN";

    // mock gitee api
    protected static GiteeApi mockGiteeApi() {
        return Mockito.mock(GiteeApi.class);
    }

    // 读取环境变量构建gitee api
    protected static GiteeApi createGiteeApi() {
        ScmConnector connector = new OkHttpScmConnector(new OkHttpClient.Builder().build());
        String apiUrl = StringUtils.defaultIfBlank(getProperty(TEST_GITEE_API_URL), "");
        String privateToken = StringUtils.defaultIfBlank(getProperty(TEST_GITEE_PRIVATE_TOKEN), "");
        HttpAuthProvider authorizationProvider =
                GiteeTokenAuthProvider.fromTokenType(TokenType.PERSONAL_ACCESS, privateToken);
        return new GiteeApi(apiUrl, connector, authorizationProvider);
    }

    protected static <T> T read(String fileName, Class<T> clazz) {
        try {
            String filePath = AbstractGiteeTest.class.getClassLoader().getResource(fileName).getFile();
            String jsonString = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            return ScmJsonUtil.fromJson(jsonString, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static <T> T read(String fileName, TypeReference<T> typeReference) {
        try {
            String filePath = AbstractGiteeTest.class.getClassLoader().getResource(fileName).getFile();
            String jsonString = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            return ScmJsonUtil.fromJson(jsonString, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static String getProperty(String key) {
        String value = System.getProperty(key);

        if (value == null) {
            value = System.getenv(key);
        }
        return value;
    }
}
