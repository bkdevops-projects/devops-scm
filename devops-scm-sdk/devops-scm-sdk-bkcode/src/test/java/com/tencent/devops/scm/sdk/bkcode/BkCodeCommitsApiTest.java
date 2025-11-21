package com.tencent.devops.scm.sdk.bkcode;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeDiffFileRevRange;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeAuthor;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommit;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommitDetail;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeDiff;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeDiffFile;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeResult;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BkCodeCommitsApiTest extends AbstractBkCodeTest {

    private static final String TEST_COMMIT_SHA = "7eaa451cdfd155338f113f1bb2d57527f34fc657";
    private static final String TEST_COMMIT_FROM_SHA = "6675f7a78364181b1b1182b341c8db828fdc1d64";

    public BkCodeCommitsApiTest() {
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

    public static void mock() {
        bkCodeApi = mockBkCodeApi();
        when(bkCodeApi.getCommitApi()).thenReturn(Mockito.mock(BkCodeCommitApi.class));
        when(bkCodeApi.getCommitApi().getCommit(TEST_PROJECT_NAME, TEST_COMMIT_SHA))
                .thenReturn(
                        read(
                                "get_commit_detail.json",
                                new TypeReference<BkCodeResult<BkCodeCommitDetail>>() {
                                }
                        ).getData()
                );
        when(
                bkCodeApi.getCommitApi()
                        .compare(
                                TEST_PROJECT_NAME,
                                TEST_COMMIT_SHA,
                                TEST_COMMIT_FROM_SHA,
                                BkCodeDiffFileRevRange.TRIPLE_DOT,
                                false
                        )
        ).thenReturn(
                read(
                        "get_commit_diff.json",
                        new TypeReference<BkCodeResult<BkCodeDiff>>() {
                        }
                ).getData()
        );
    }

    @Test
    public void testGetCommit() {
        BkCodeCommitDetail commitDetail = bkCodeApi.getCommitApi().getCommit(TEST_PROJECT_NAME, TEST_COMMIT_SHA);
        Assertions.assertNotNull(commitDetail);
        BkCodeCommit commit = commitDetail.getCommitInfo();
        Assertions.assertEquals("7eaa451cdfd155338f113f1bb2d57527f34fc657", commit.getId());
        Assertions.assertEquals("init repo", commit.getTitle());
        BkCodeAuthor author = commit.getAuthor();
        Assertions.assertEquals("zhangsan", author.getName());
        Assertions.assertEquals("zhangsan@tencent.com", author.getEmail());
    }

    @Test
    public void testGetDiff() {
        BkCodeDiff diff = bkCodeApi.getCommitApi().compare(
                TEST_PROJECT_NAME,
                TEST_COMMIT_SHA,
                TEST_COMMIT_FROM_SHA,
                BkCodeDiffFileRevRange.TRIPLE_DOT,
                false
        );
        Assertions.assertEquals(5, diff.getTotalFileCount());
        List<BkCodeDiffFile> diffFiles = diff.getDiffFiles();
        BkCodeDiffFile bkCodeDiffFile0 = diffFiles.get(0);
        Assertions.assertEquals(".gitignore", bkCodeDiffFile0.getDstPath());
        Assertions.assertTrue(bkCodeDiffFile0.created());

        BkCodeDiffFile bkCodeDiffFile1 = diffFiles.get(1);
        Assertions.assertEquals("a/a_1/a_1.txt", bkCodeDiffFile1.getSrcPath());
        Assertions.assertTrue(bkCodeDiffFile1.updated());

        BkCodeDiffFile bkCodeDiffFile2 = diffFiles.get(2);
        Assertions.assertEquals("a/a_2/a_2_1.txt", bkCodeDiffFile2.getDstPath());
        Assertions.assertTrue(bkCodeDiffFile2.created());

        BkCodeDiffFile bkCodeDiffFile3 = diffFiles.get(3);
        Assertions.assertEquals("a/a_3/a_3_1.txt", bkCodeDiffFile3.getSrcPath());
        Assertions.assertTrue(bkCodeDiffFile3.removed());

        BkCodeDiffFile bkCodeDiffFile4 = diffFiles.get(4);
        Assertions.assertEquals("a/a_2/a_2_1.txt", bkCodeDiffFile4.getSrcPath());
        Assertions.assertEquals("a/a_3/a_3.txt", bkCodeDiffFile4.getDstPath());
        Assertions.assertTrue(bkCodeDiffFile4.renamed());
    }
}
