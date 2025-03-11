package com.tencent.devops.scm.sdk.tgit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.common.connector.okhttp3.OkHttpScmConnector;
import com.tencent.devops.scm.sdk.tgit.auth.TGitTokenAuthProvider;
import com.tencent.devops.scm.sdk.tgit.util.TGitJsonUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FileUtils;
import org.mockito.Mockito;

/**
 * 基础的tgit测试类
 */
public class AbstractTGitTest {

    protected static final String TEST_PROJECT_NAME = "mingshewhe/webhook_test3";
    protected static final String TEST_DEFAULT_BRANCH = "master";
    protected static final String TEST_TGIT_API_URL = "TEST_TGIT_API_URL";
    protected static final String TEST_TGIT_PRIVATE_TOKEN = "TEST_TGIT_PRIVATE_TOKEN";

    protected static TGitApi mockTGitApi() {
        return Mockito.mock(TGitApi.class);
    }

    protected static TGitApi createTGitApi() {
        ScmConnector connector = new OkHttpScmConnector(new OkHttpClient.Builder().build());
        String apiUrl = getProperty(TEST_TGIT_API_URL);
        String privateToken = getProperty(TEST_TGIT_PRIVATE_TOKEN);
        HttpAuthProvider authorizationProvider =
                TGitTokenAuthProvider.fromPrivateToken(privateToken);
        return new TGitApi(apiUrl, connector, authorizationProvider);
    }

    protected static <T> T read(String fileName, Class<T> clazz) {
        try {
            String filePath = AbstractTGitTest.class.getClassLoader().getResource(fileName).getFile();
            String jsonString = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            return TGitJsonUtil.fromJson(jsonString, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static <T> T read(String fileName, TypeReference<T> typeReference) {
        try {
            String filePath = AbstractTGitTest.class.getClassLoader().getResource(fileName).getFile();
            String jsonString = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            return TGitJsonUtil.fromJson(jsonString, typeReference);
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
