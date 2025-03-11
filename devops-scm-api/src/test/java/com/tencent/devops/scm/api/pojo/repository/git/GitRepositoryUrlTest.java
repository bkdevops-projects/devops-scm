package com.tencent.devops.scm.api.pojo.repository.git;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GitRepositoryUrlTest {

    @Test
    @DisplayName("测试https域名")
    public void test_1() {
        String url = "https://git.example.com/mygroup/myproject.git";
        GitRepositoryUrl repositoryUrl = new GitRepositoryUrl(url);
        Assertions.assertEquals(url, repositoryUrl.getUrl());
        Assertions.assertEquals("https", repositoryUrl.getProtocol());
        Assertions.assertEquals("git.example.com", repositoryUrl.getHost());
        Assertions.assertNull(repositoryUrl.getPort());
        Assertions.assertEquals("mygroup/myproject", repositoryUrl.getFullName());
        Assertions.assertEquals("mygroup", repositoryUrl.getGroup());
        Assertions.assertEquals("myproject", repositoryUrl.getName());
    }

    @Test
    @DisplayName("测试http端口")
    public void test_2() {
        String url = "http://127.0.0.0:8080/mygroup/myproject.git";
        GitRepositoryUrl repositoryUrl = new GitRepositoryUrl(url);
        Assertions.assertEquals(url, repositoryUrl.getUrl());
        Assertions.assertEquals("http", repositoryUrl.getProtocol());
        Assertions.assertEquals("127.0.0.0", repositoryUrl.getHost());
        Assertions.assertEquals("8080", repositoryUrl.getPort());
        Assertions.assertEquals("mygroup/myproject", repositoryUrl.getFullName());
        Assertions.assertEquals("mygroup", repositoryUrl.getGroup());
        Assertions.assertEquals("myproject", repositoryUrl.getName());
    }

    @Test
    @DisplayName("测试没有.git后缀")
    public void test_3() {
        String url = "https://git.example.com/mygroup/myproject";
        GitRepositoryUrl repositoryUrl = new GitRepositoryUrl(url);
        Assertions.assertEquals(url, repositoryUrl.getUrl());
        Assertions.assertEquals("https", repositoryUrl.getProtocol());
        Assertions.assertEquals("git.example.com", repositoryUrl.getHost());
        Assertions.assertNull(repositoryUrl.getPort());
        Assertions.assertEquals("mygroup/myproject", repositoryUrl.getFullName());
        Assertions.assertEquals("mygroup", repositoryUrl.getGroup());
        Assertions.assertEquals("myproject", repositoryUrl.getName());
    }

    @Test
    @DisplayName("测试ssh")
    public void test_4() {
        String url = "git@git.example.com:mygroup/myproject.git";
        GitRepositoryUrl repositoryUrl = new GitRepositoryUrl(url);
        Assertions.assertEquals(url, repositoryUrl.getUrl());
        Assertions.assertEquals("git", repositoryUrl.getProtocol());
        Assertions.assertEquals("git.example.com", repositoryUrl.getHost());
        Assertions.assertNull(repositoryUrl.getPort());
        Assertions.assertEquals("mygroup/myproject", repositoryUrl.getFullName());
        Assertions.assertEquals("mygroup", repositoryUrl.getGroup());
        Assertions.assertEquals("myproject", repositoryUrl.getName());
    }

    @Test
    @DisplayName("测试http域名")
    public void test_5() {
        String url = "https://git.example.com/mygroup/mygroup1/myproject.git";
        GitRepositoryUrl repositoryUrl = new GitRepositoryUrl(url);
        Assertions.assertEquals(url, repositoryUrl.getUrl());
        Assertions.assertEquals("https", repositoryUrl.getProtocol());
        Assertions.assertEquals("git.example.com", repositoryUrl.getHost());
        Assertions.assertNull(repositoryUrl.getPort());
        Assertions.assertEquals("mygroup/mygroup1/myproject", repositoryUrl.getFullName());
        Assertions.assertEquals("mygroup/mygroup1", repositoryUrl.getGroup());
        Assertions.assertEquals("myproject", repositoryUrl.getName());
    }
}
