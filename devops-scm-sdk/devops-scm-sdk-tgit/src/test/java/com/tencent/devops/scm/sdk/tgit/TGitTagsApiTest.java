package com.tencent.devops.scm.sdk.tgit;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommit;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitTag;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TGitTagsApiTest extends AbstractTGitTest {

    private static final String TEST_TAG_NAME_0 = "v1.0.1";

    private static TGitApi tGitApi;

    public TGitTagsApiTest() {
        super();
    }

    @BeforeAll
    public static void testSetup() {
        tGitApi = mockTGitApi();
        when(tGitApi.getTagsApi()).thenReturn(Mockito.mock(TGitTagsApi.class));
        when(tGitApi.getTagsApi().getTag(TEST_PROJECT_NAME, TEST_TAG_NAME_0))
                .thenReturn(read("get_tag.json", TGitTag.class));
        when(tGitApi.getTagsApi().getTags(TEST_PROJECT_NAME))
                .thenReturn(read("list_tags.json", new TypeReference<List<TGitTag>>() {
                }));
    }

    @Test
    public void testGetTag() {
        TGitTag tag = tGitApi.getTagsApi().getTag(TEST_PROJECT_NAME, TEST_TAG_NAME_0);

        Assertions.assertEquals(TEST_TAG_NAME_0, tag.getName());
        Assertions.assertNull(tag.getMessage());

        TGitCommit commit = tag.getCommit();
        Assertions.assertNotNull(commit);
        Assertions.assertEquals("87acd380f4a91ba1eb200a082ad60f394f3062a5", commit.getId());
        Assertions.assertEquals("87acd380", commit.getShortId());
        Assertions.assertEquals(
                "Merge branch 'mr_test' into 'master' (merge request !6)",
                commit.getMessage()
        );
        Assertions.assertEquals("mingshewhe", commit.getCommitterName());
        Assertions.assertEquals(
                "wx_a56dc86ef0f74feda385d4818e7c5cda@git.code.tencent.com",
                commit.getCommitterEmail()
        );
        Assertions.assertEquals("mingshewhe", commit.getAuthorName());
        Assertions.assertEquals(
                "wx_a56dc86ef0f74feda385d4818e7c5cda@git.code.tencent.com",
                commit.getAuthorEmail()
        );
    }

    @Test
    public void testGetTags() {
        List<TGitTag> tags = tGitApi.getTagsApi().getTags(TEST_PROJECT_NAME);

        Assertions.assertFalse(tags.isEmpty());
        Assertions.assertEquals("test", tags.get(0).getName());
        Assertions.assertEquals("v1.0.1", tags.get(1).getName());
    }
}
