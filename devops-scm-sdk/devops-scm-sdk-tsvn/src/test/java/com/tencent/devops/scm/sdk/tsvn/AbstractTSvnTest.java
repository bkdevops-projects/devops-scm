package com.tencent.devops.scm.sdk.tsvn;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.common.connector.okhttp3.OkHttpScmConnector;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import com.tencent.devops.scm.sdk.tsvn.TSvnConstants.TokenType;
import com.tencent.devops.scm.sdk.tsvn.auth.TSvnTokenAuthProvider;
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
public class AbstractTSvnTest {
    // 测试仓库
    protected static final String TEST_PROJECT_NAME = "svn_group/svn_repo";
    // [环境变量KEY]接口地址
    protected static final String TEST_TSVN_API_URL = "TEST_TSVN_API_URL";
    // [环境变量KEY]授权token
    protected static final String TEST_TSVN_PRIVATE_TOKEN = "TEST_TSVN_PRIVATE_TOKEN";

    // mock gitee api
    protected static TSvnApi mockTSvnApi() {
        return Mockito.mock(TSvnApi.class);
    }

    // 读取环境变量构建gitee api
    protected static TSvnApi createTSvnApi() {
        ScmConnector connector = new OkHttpScmConnector(new OkHttpClient.Builder().build());
        String apiUrl = StringUtils.defaultIfBlank(getProperty(TEST_TSVN_API_URL), "");
        String privateToken = StringUtils.defaultIfBlank(getProperty(TEST_TSVN_PRIVATE_TOKEN), "");
        HttpAuthProvider authorizationProvider =
                TSvnTokenAuthProvider.fromTokenType(TokenType.PERSONAL_ACCESS, privateToken);
        return new TSvnApi(apiUrl, connector, authorizationProvider);
    }

    protected static <T> T read(String fileName, Class<T> clazz) {
        try {
            String filePath = AbstractTSvnTest.class.getClassLoader().getResource(fileName).getFile();
            String jsonString = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            return ScmJsonUtil.fromJson(jsonString, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static <T> T read(String fileName, TypeReference<T> typeReference) {
        try {
            String filePath = AbstractTSvnTest.class.getClassLoader().getResource(fileName).getFile();
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
