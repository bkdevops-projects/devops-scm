package com.tencent.devops.scm.sdk.bkcode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.bkcode.auth.BkCodeTokenAuthProvider;
import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.common.connector.okhttp3.OkHttpScmConnector;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FileUtils;
import org.mockito.Mockito;

/**
 * 基础的BkCode测试类
 */
public class AbstractBkCodeTest {

    protected static final String TEST_PROJECT_NAME = "11";
    protected static final String TEST_DEFAULT_BRANCH = "master";
    protected static final String TEST_BK_CODE_API_URL = "TEST_BK_CODE_API_URL";
    protected static final String TEST_BK_CODE_PRIVATE_TOKEN = "TEST_BK_CODE_PRIVATE_TOKEN";
    protected static final Boolean MOCK_DATA = true;
    protected static BkCodeApi bkCodeApi;

    protected static BkCodeApi mockBkCodeApi() {
        return Mockito.mock(BkCodeApi.class);
    }

    protected static BkCodeApi createBkCodeApi() {
        ScmConnector connector = new OkHttpScmConnector(new OkHttpClient.Builder().build());
        String apiUrl = getProperty(TEST_BK_CODE_API_URL);
        String privateToken = getProperty(TEST_BK_CODE_PRIVATE_TOKEN);
        HttpAuthProvider authorizationProvider =
                BkCodeTokenAuthProvider.fromPersonalAccessToken(privateToken);
        return new BkCodeApi(apiUrl, connector, authorizationProvider);
    }

    protected static <T> T read(String fileName, Class<T> clazz) {
        try {
            String filePath = AbstractBkCodeTest.class.getClassLoader().getResource(fileName).getFile();
            String jsonString = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            return ScmJsonUtil.fromJson(jsonString, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static <T> T read(String fileName, TypeReference<T> typeReference) {
        try {
            String filePath = AbstractBkCodeTest.class.getClassLoader().getResource(fileName).getFile();
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
