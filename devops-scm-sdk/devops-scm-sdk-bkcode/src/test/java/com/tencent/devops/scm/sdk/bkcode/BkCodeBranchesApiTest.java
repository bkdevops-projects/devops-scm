package com.tencent.devops.scm.sdk.bkcode;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeBranch;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodePage;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BkCodeBranchesApiTest extends AbstractBkCodeTest {

    private static final String TEST_BRANCH_NAME = "main";
    private static final String TEST_BRANCH_SEARCH_TERM = "mr_test";
    private static final String TEST_CREATE_BRANCH_NAME = "dev";
    private static final String TEST_CREATE_BRANCH_DESC = "Created from main branch";

    public BkCodeBranchesApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        if (MOCK_DATA) {
            mock();
        } else {
            bkCodeApi = createBkCodeApi();
        }
    }

    private static void mock() {
        bkCodeApi = mockBkCodeApi();
        when(bkCodeApi.getBranchesApi()).thenReturn(Mockito.mock(BkCodeBranchesApi.class));
        when(
                bkCodeApi.getBranchesApi().getBranch(
                        TEST_PROJECT_NAME,
                        TEST_BRANCH_NAME
                )
        ).thenReturn(
                read(
                        "get_branch_detail.json",
                        new TypeReference<BkCodeResult<BkCodeBranch>>() {}
                ).getData()
        );
        when(
                bkCodeApi.getBranchesApi().getBranches(
                        TEST_PROJECT_NAME,
                        TEST_BRANCH_SEARCH_TERM,
                        BkCodeConstants.DEFAULT_PAGE,
                        BkCodeConstants.DEFAULT_PER_PAGE
                )
        ).thenReturn(
                read(
                        "get_branch_list.json",
                        new TypeReference<BkCodeResult<BkCodePage<BkCodeBranch>>>() {}
                ).getData()
        );
        when(
                bkCodeApi.getBranchesApi().createBranch(
                        TEST_PROJECT_NAME,
                        TEST_CREATE_BRANCH_NAME,
                        TEST_BRANCH_NAME,
                        TEST_CREATE_BRANCH_DESC
                )
        ).thenReturn(
                read(
                        "create_branch_result.json",
                        new TypeReference<BkCodeResult<BkCodeBranch>>() {}
                ).getData()
        );
    }

    @Test
    public void testGetBranch() {
        BkCodeBranch branch = bkCodeApi.getBranchesApi().getBranch(TEST_PROJECT_NAME, TEST_BRANCH_NAME);
        Assertions.assertEquals(TEST_BRANCH_NAME, branch.getName());
        Assertions.assertEquals("6675f7a78364181b1b1182b341c8db828fdc1d64", branch.getCommit().getId());
    }

    @Test
    public void testSearchBranch() {
        BkCodePage<BkCodeBranch> branches = bkCodeApi.getBranchesApi()
                .getBranches(
                        TEST_PROJECT_NAME,
                        TEST_BRANCH_SEARCH_TERM,
                        BkCodeConstants.DEFAULT_PAGE,
                        BkCodeConstants.DEFAULT_PER_PAGE
                );
        Assertions.assertEquals(2, branches.getCount());
        BkCodeBranch bkCodeBranch = branches.getRecords().get(1);
        Assertions.assertEquals("feat_0", bkCodeBranch.getName());
    }

    @Test
    public void testCreateBranch() {
        BkCodeBranch branch = bkCodeApi.getBranchesApi()
                .createBranch(
                        TEST_PROJECT_NAME,
                        TEST_CREATE_BRANCH_NAME,
                        TEST_BRANCH_NAME,
                        TEST_CREATE_BRANCH_DESC
                );
        Assertions.assertEquals(TEST_CREATE_BRANCH_NAME, branch.getName());
        Assertions.assertEquals("7eaa451cdfd155338f113f1bb2d57527f34fc657", branch.getCommit().getId());
        Assertions.assertEquals(TEST_CREATE_BRANCH_DESC, branch.getDescription());
    }

    @Test
    public void testDelBranch() {
        bkCodeApi.getBranchesApi().delBranch(
                        TEST_PROJECT_NAME,
                        TEST_CREATE_BRANCH_NAME
                );
    }
}
