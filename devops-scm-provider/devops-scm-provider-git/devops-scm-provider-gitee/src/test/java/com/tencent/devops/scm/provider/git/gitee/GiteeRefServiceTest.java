package com.tencent.devops.scm.provider.git.gitee;

import com.tencent.devops.scm.api.pojo.Reference;
import java.util.List;
import org.junit.jupiter.api.Test;

public class GiteeRefServiceTest extends AbstractGiteeServiceTest {
    private static final String TEST_BRANCH_NAME = "master";
    private static final String TEST_BRANCH_SEARCH_TERM = "mr_test";
    private static final String TEST_TAG_NAME_0 = "v1.0.1";
    private static final String TEST_COMMIT_SHA = "b5f141f9c1b3f87d9b070157097130be7fb7563a";


    private final GiteeRefService refService = new GiteeRefService();;

    @Test
    public void testListBranches() {
        List<Reference> references = refService.listBranches(providerRepository, null);
        references.forEach(System.out::println);
    }
}
