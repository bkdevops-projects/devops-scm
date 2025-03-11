package com.tencent.devops.scm.provider.svn.common;

import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.auth.UserPassScmAuth;
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository;

public class AbstractSvnServiceTest {

    protected static final String TEST_SVN_URL = "TEST_SVN_URL";
    protected static final String TEST_SVN_USER_NAME = "TEST_SVN_USERNAME";
    protected static final String TEST_SVN_PASSWORD = "TEST_SVN_PASSWORD";


    protected SvnScmProviderRepository providerRepository;

    protected AbstractSvnServiceTest() {
        providerRepository = createProviderRepository();
    }

    protected SvnScmProviderRepository createProviderRepository() {
        String url = getProperty(TEST_SVN_URL);
        String userName = getProperty(TEST_SVN_USER_NAME);
        String password = getProperty(TEST_SVN_PASSWORD);
        IScmAuth auth = new UserPassScmAuth(userName, password);
        return new SvnScmProviderRepository()
                .withUrl(url)
                .withAuth(auth);
    }

    protected static String getProperty(String key) {
        String value = System.getProperty(key);

        if (value == null) {
            value = System.getenv(key);
        }
        return value;
    }
}
