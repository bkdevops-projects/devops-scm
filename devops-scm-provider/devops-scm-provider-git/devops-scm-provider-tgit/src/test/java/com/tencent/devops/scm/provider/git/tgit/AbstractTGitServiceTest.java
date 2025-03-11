package com.tencent.devops.scm.provider.git.tgit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.auth.PrivateTokenScmAuth;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository;
import com.tencent.devops.scm.provider.git.tgit.auth.TGitAuthProviderFactory;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.common.connector.okhttp3.OkHttpScmConnector;
import com.tencent.devops.scm.sdk.tgit.TGitApi;
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory;
import com.tencent.devops.scm.sdk.tgit.util.TGitJsonUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FileUtils;
import org.mockito.Mockito;

public class AbstractTGitServiceTest {
    protected static final String TEST_PROJECT_NAME = "mingshewhe/webhook_test3";
    protected static final String TEST_TGIT_API_URL = "TEST_TGIT_API_URL";
    protected static final String TEST_TGIT_PRIVATE_TOKEN = "TEST_TGIT_PRIVATE_TOKEN";

    protected GitScmProviderRepository providerRepository;
    protected TGitApiFactory apiFactory;
    protected TGitApi tGitApi;

    protected AbstractTGitServiceTest() {
        providerRepository = createProviderRepository();

        apiFactory = mockTGitApiFactory();
        when(apiFactory.fromAuthProvider(any())).thenReturn(mock(TGitApi.class));
        tGitApi = apiFactory.fromAuthProvider(TGitAuthProviderFactory.create(providerRepository.getAuth()));
    }


    protected static TGitApiFactory mockTGitApiFactory() {
        return Mockito.mock(TGitApiFactory.class);
    }

    protected static TGitApiFactory createTGitApiFactory() {
        ScmConnector connector = new OkHttpScmConnector(new OkHttpClient.Builder().build());
        String apiUrl = getProperty(TEST_TGIT_API_URL);
        return new TGitApiFactory(apiUrl, connector);
    }

    protected static GitScmProviderRepository createProviderRepository() {
        String privateToken = getProperty(TEST_TGIT_PRIVATE_TOKEN);
        IScmAuth auth = new PrivateTokenScmAuth(privateToken);
        return new GitScmProviderRepository()
                .withAuth(auth)
                .withProjectIdOrPath(TEST_PROJECT_NAME);
    }

    protected static <T> T read(String fileName, Class<T> clazz) {
        try {
            String filePath = AbstractTGitServiceTest.class.getClassLoader().getResource(fileName).getFile();
            String jsonString = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            return TGitJsonUtil.fromJson(jsonString, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static <T> T read(String fileName, TypeReference<T> typeReference) {
        try {
            String filePath = AbstractTGitServiceTest.class.getClassLoader().getResource(fileName).getFile();
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
