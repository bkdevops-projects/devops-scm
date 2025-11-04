package com.tencent.devops.scm.sdk.bkcode;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeAuthor;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommit;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommitter;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodePage;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeResult;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeTag;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BkCodeTagsApiTest extends AbstractBkCodeTest {

    private static final String TEST_TAG_NAME_0 = "v1.0.0";

    public BkCodeTagsApiTest() {
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
        when(bkCodeApi.getTagApi()).thenReturn(Mockito.mock(BkCodeTagApi.class));
        when(
                bkCodeApi.getTagApi().getTag(TEST_PROJECT_NAME, TEST_TAG_NAME_0)
        ).thenReturn(
                read(
                        "get_tag_detail.json",
                        new TypeReference<BkCodeResult<BkCodeTag>>() {}
                ).getData()
        );
        when(
                bkCodeApi.getTagApi().getTags(TEST_PROJECT_NAME)
        ).thenReturn(
                read(
                        "get_tag_list.json",
                        new TypeReference<BkCodeResult<BkCodePage<BkCodeTag>>>() {}
                ).getData()
        );
    }

    @Test
    public void testGetTag() {
        BkCodeTag tag = bkCodeApi.getTagApi().getTag(TEST_PROJECT_NAME, TEST_TAG_NAME_0);

        Assertions.assertEquals(TEST_TAG_NAME_0, tag.getName());
        Assertions.assertEquals("First stable release", tag.getMessage());

        BkCodeCommit commit = tag.getCommit();
        Assertions.assertNotNull(commit);
        Assertions.assertEquals("7eaa451cdfd155338f113f1bb2d57527f34fc657", commit.getId());
        Assertions.assertEquals(
                "init repo\n",
                commit.getMessage()
        );
        BkCodeCommitter committer = commit.getCommitter();
        Assertions.assertEquals("zhangsan", committer.getName());
        Assertions.assertEquals(
                "zhangsan@tencent.com",
                committer.getEmail()
        );
        BkCodeAuthor author = commit.getAuthor();
        Assertions.assertEquals("zhangsan", author.getName());
        Assertions.assertEquals(
                "zhangsan@tencent.com",
                author.getEmail()
        );
    }

    @Test
    public void testGetTags() {
        List<BkCodeTag> tags = bkCodeApi.getTagApi().getTags(TEST_PROJECT_NAME).getRecords();

        Assertions.assertFalse(tags.isEmpty());
        Assertions.assertEquals(TEST_TAG_NAME_0, tags.get(0).getName());
    }
}
