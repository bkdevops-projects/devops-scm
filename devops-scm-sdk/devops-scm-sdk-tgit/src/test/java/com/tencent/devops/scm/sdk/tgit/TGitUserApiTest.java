package com.tencent.devops.scm.sdk.tgit;

import static org.mockito.Mockito.when;

import com.tencent.devops.scm.sdk.tgit.pojo.TGitUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TGitUserApiTest extends AbstractTGitTest {

    private static TGitApi tGitApi;
    private static TGitUserApi userApi;

    public TGitUserApiTest() {
        super();
    }

    @BeforeAll
    public static void testSetup() {
        tGitApi = mockTGitApi();
        when(tGitApi.getUserApi()).thenReturn(Mockito.mock(TGitUserApi.class));
        userApi = tGitApi.getUserApi();

        when(userApi.getCurrentUser()).thenReturn(read("get_current_user.json", TGitUser.class));
    }

    @Test
    public void testGetCurrentUser() {
        TGitUser currentUser = userApi.getCurrentUser();

        Assertions.assertEquals(132102L, currentUser.getId());
        Assertions.assertEquals(
                "wx_a56dc86ef0f74feda385d4818e7c5cda@git.code.tencent.com",
                currentUser.getEmail()
        );
        Assertions.assertEquals("mingshewhe", currentUser.getUsername());
        Assertions.assertEquals(
                "https://git.code.tencent.com/u/mingshewhe",
                currentUser.getWebUrl()
        );
        Assertions.assertEquals("active", currentUser.getState());
        Assertions.assertFalse(currentUser.getAdmin());
    }
}
