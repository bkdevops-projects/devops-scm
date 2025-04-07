package com.tencent.devops.scm.sdk.tgit;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCommitCompare;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TGitFileApiTest extends AbstractGiteeTest {
    private static GiteeApi giteeApi;

    public TGitFileApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() { giteeApi = createTGitApi(); }

    @Test
    public void testCommitCompare() throws JsonProcessingException {
        GiteeCommitCompare commitCompare = giteeApi.getFileApi().commitCompare(
                TEST_PROJECT_NAME,
                "0eef37e3df386d5c2d114e19db9e55e30225fe7f",
                "a62ce767b308f416de932b77183a963312f926a7",
                false
        );
        System.out.println(ScmJsonUtil.getJsonFactory().toJson(commitCompare));
    }

}
