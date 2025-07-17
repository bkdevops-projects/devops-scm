package com.tencent.devops.scm.api.pojo.repository.git

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GitRepositoryUrlTest {

    @Test
    @DisplayName("测试https域名")
    fun test1() {
        val url = "https://git.example.com/mygroup/myproject.git"
        val repositoryUrl = GitRepositoryUrl(url)
        Assertions.assertEquals(url, repositoryUrl.url)
        Assertions.assertEquals("https", repositoryUrl.protocol)
        Assertions.assertEquals("git.example.com", repositoryUrl.host)
        Assertions.assertNull(repositoryUrl.port)
        Assertions.assertEquals("mygroup/myproject", repositoryUrl.fullName)
        Assertions.assertEquals("mygroup", repositoryUrl.group)
        Assertions.assertEquals("myproject", repositoryUrl.name)
    }

    @Test
    @DisplayName("测试http端口")
    fun test2() {
        val url = "http://127.0.0.0:8080/mygroup/myproject.git"
        val repositoryUrl = GitRepositoryUrl(url)
        Assertions.assertEquals(url, repositoryUrl.url)
        Assertions.assertEquals("http", repositoryUrl.protocol)
        Assertions.assertEquals("127.0.0.0", repositoryUrl.host)
        Assertions.assertEquals("8080", repositoryUrl.port)
        Assertions.assertEquals("mygroup/myproject", repositoryUrl.fullName)
        Assertions.assertEquals("mygroup", repositoryUrl.group)
        Assertions.assertEquals("myproject", repositoryUrl.name)
    }

    @Test
    @DisplayName("测试没有.git后缀")
    fun test3() {
        val url = "https://git.example.com/mygroup/myproject"
        val repositoryUrl = GitRepositoryUrl(url)
        Assertions.assertEquals(url, repositoryUrl.url)
        Assertions.assertEquals("https", repositoryUrl.protocol)
        Assertions.assertEquals("git.example.com", repositoryUrl.host)
        Assertions.assertNull(repositoryUrl.port)
        Assertions.assertEquals("mygroup/myproject", repositoryUrl.fullName)
        Assertions.assertEquals("mygroup", repositoryUrl.group)
        Assertions.assertEquals("myproject", repositoryUrl.name)
    }

    @Test
    @DisplayName("测试ssh")
    fun test4() {
        val url = "git@git.example.com:mygroup/myproject.git"
        val repositoryUrl = GitRepositoryUrl(url)
        Assertions.assertEquals(url, repositoryUrl.url)
        Assertions.assertEquals("git", repositoryUrl.protocol)
        Assertions.assertEquals("git.example.com", repositoryUrl.host)
        Assertions.assertNull(repositoryUrl.port)
        Assertions.assertEquals("mygroup/myproject", repositoryUrl.fullName)
        Assertions.assertEquals("mygroup", repositoryUrl.group)
        Assertions.assertEquals("myproject", repositoryUrl.name)
    }

    @Test
    @DisplayName("测试http域名")
    fun test5() {
        val url = "https://git.example.com/mygroup/mygroup1/myproject.git"
        val repositoryUrl = GitRepositoryUrl(url)
        Assertions.assertEquals(url, repositoryUrl.url)
        Assertions.assertEquals("https", repositoryUrl.protocol)
        Assertions.assertEquals("git.example.com", repositoryUrl.host)
        Assertions.assertNull(repositoryUrl.port)
        Assertions.assertEquals("mygroup/mygroup1/myproject", repositoryUrl.fullName)
        Assertions.assertEquals("mygroup/mygroup1", repositoryUrl.group)
        Assertions.assertEquals("myproject", repositoryUrl.name)
    }
}
