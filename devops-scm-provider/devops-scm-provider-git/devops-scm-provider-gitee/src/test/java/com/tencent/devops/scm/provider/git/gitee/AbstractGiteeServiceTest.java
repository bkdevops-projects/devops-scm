package com.tencent.devops.scm.provider.git.gitee;


import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository;
import com.tencent.devops.scm.provider.git.gitee.auth.GiteeTokenAuthProviderAdapter;
import com.tencent.devops.scm.sdk.common.connector.okhttp3.OkHttpScmConnector;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FileUtils;

public class AbstractGiteeServiceTest {
    protected static final String TEST_PROJECT_NAME = "Tencent-BlueKing/bk-ci";
    protected static final String TEST_TGIT_API_URL = "TEST_GITEE_API_URL";
    protected static final String TEST_TGIT_PRIVATE_TOKEN = "TEST_GITEE_PRIVATE_TOKEN";

    protected static GitScmProviderRepository providerRepository;

    protected static GiteeApiFactory giteeApiFactory;

    protected AbstractGiteeServiceTest() {
        providerRepository = createProviderRepository();
        giteeApiFactory = new GiteeApiFactory(
                getProperty(TEST_TGIT_API_URL),
                new OkHttpScmConnector(new OkHttpClient.Builder().build())
        );
    }

    protected static GitScmProviderRepository createProviderRepository() {
        String privateToken = getProperty(TEST_TGIT_PRIVATE_TOKEN);
        IScmAuth auth = new PersonalAccessTokenScmAuth(privateToken);
        return new GitScmProviderRepository()
                .withAuth(auth)
                .withProjectIdOrPath(TEST_PROJECT_NAME);
    }

    protected static <T> T read(String fileName, Class<T> clazz) {
        try {
            String filePath = AbstractGiteeServiceTest.class.getClassLoader().getResource(fileName).getFile();
            String jsonString = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            return ScmJsonUtil.fromJson(jsonString, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static <T> T read(String fileName, TypeReference<T> typeReference) {
        try {
            String filePath = AbstractGiteeServiceTest.class.getClassLoader().getResource(fileName).getFile();
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
