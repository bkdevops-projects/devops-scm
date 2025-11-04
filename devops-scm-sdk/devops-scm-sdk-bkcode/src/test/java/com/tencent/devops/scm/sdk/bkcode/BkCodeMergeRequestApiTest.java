package com.tencent.devops.scm.sdk.bkcode;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeBranch;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeMergeRequest;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodePage;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BkCodeMergeRequestApiTest extends AbstractBkCodeTest {

    private static final Long TEST_MR_ID = 5L;
    private static final Integer TEST_MR_NUMBER = 2;

    public BkCodeMergeRequestApiTest() {
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
        when(bkCodeApi.getMergeRequestApi()).thenReturn(Mockito.mock(BkCodeMergeRequestApi.class));
        when(
                bkCodeApi.getMergeRequestApi().getMergeRequestByNumber(
                        TEST_PROJECT_NAME,
                        TEST_MR_NUMBER
                )
        ).thenReturn(
                read(
                        "get_mr_detail.json",
                        new TypeReference<BkCodeResult<BkCodeMergeRequest>>() {}
                ).getData()
        );
        when(
                bkCodeApi.getMergeRequestApi().getMergeRequestById(
                        TEST_PROJECT_NAME,
                        TEST_MR_ID
                )
        ).thenReturn(
                read(
                        "get_mr_detail.json",
                        new TypeReference<BkCodeResult<BkCodeMergeRequest>>() {}
                ).getData()
        );
    }

    @Test
    public void getMergeRequestByNumber() {
        BkCodeMergeRequest mergeRequest = bkCodeApi.getMergeRequestApi()
                .getMergeRequestByNumber(
                        TEST_PROJECT_NAME,
                        TEST_MR_NUMBER
                );
        Assertions.assertNotNull(mergeRequest);
    }

    @Test
    public void getMergeRequestById() {
        BkCodeMergeRequest mergeRequest = bkCodeApi.getMergeRequestApi()
                .getMergeRequestById(
                        TEST_PROJECT_NAME,
                        TEST_MR_ID
                );
        Assertions.assertNotNull(mergeRequest);
    }
}
