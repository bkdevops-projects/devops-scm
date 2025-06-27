package com.tencent.devops.scm.api.pojo.repository.git

import java.util.regex.Pattern

/**
 * Git仓库URL解析类
 */
data class GitRepositoryUrl(val url: String) {
    companion object {
        const val PROTOCOL_HTTP_SEPARATOR = "://"
        const val PROTOCOL_GIT_SEPARATOR = "@"
        const val PROTOCOL_GIT = "git"
        const val PROTOCOL_SSH = "ssh"
        const val PROTOCOL_HTTP = "http"
        const val PROTOCOL_HTTPS = "https"
        const val PROTOCOL_NONE = ""

        private val HOST_AND_PORT_EXTRACTOR =
            Pattern.compile("([^:/\\\\~]*)(?::(\\d*))?(?:([:/\\\\~])(.*))?")
    }

    val protocol: String
    val userName: String?
    val password: String?
    val host: String
    val port: String?
    val group: String
    val name: String
    val fullName: String

    // 解析过程中的url
    private var processingUrl = url

    init {
        protocol = parseProtocol()
        val userInfo = parseUserInfo()
        userName = userInfo.first
        password = userInfo.second

        val hostInfo = parseHostAndPort()
        host = hostInfo.first
        port = hostInfo.second

        val nameInfo = parseNameInfo()
        fullName = nameInfo.first
        group = nameInfo.second
        name = nameInfo.third
    }

    private fun parseProtocol(): String {
        return when {
            url.startsWith(PROTOCOL_HTTPS + PROTOCOL_HTTP_SEPARATOR) -> {
                processingUrl = url.substring(PROTOCOL_HTTPS.length + PROTOCOL_HTTP_SEPARATOR.length)
                PROTOCOL_HTTPS
            }

            url.startsWith(PROTOCOL_HTTP + PROTOCOL_HTTP_SEPARATOR) -> {
                processingUrl = url.substring(PROTOCOL_HTTP.length + PROTOCOL_HTTP_SEPARATOR.length)
                PROTOCOL_HTTP
            }

            url.startsWith(PROTOCOL_SSH + PROTOCOL_GIT_SEPARATOR) -> {
                processingUrl = url.substring(PROTOCOL_SSH.length + PROTOCOL_GIT_SEPARATOR.length)
                PROTOCOL_SSH
            }

            url.startsWith(PROTOCOL_GIT + PROTOCOL_GIT_SEPARATOR) -> {
                processingUrl = url.substring(PROTOCOL_GIT.length + PROTOCOL_GIT_SEPARATOR.length)
                PROTOCOL_GIT
            }

            else -> {
                PROTOCOL_NONE
            }
        }
    }

    private fun parseUserInfo(): Pair<String?, String?> {
        val indexAt = processingUrl.lastIndexOf('@')
        if (indexAt >= 0) {
            val userInfo = processingUrl.substring(0, indexAt)
            val indexPwdSep = userInfo.indexOf(':')
            processingUrl = url.substring(indexAt + 1)
            if (indexPwdSep < 0) {
                Pair(userInfo, null)
            } else {
                val userName = userInfo.substring(0, indexPwdSep)
                val password = userInfo.substring(indexPwdSep + 1)
                Pair(userName, password)
            }
        }
        return Pair(null, null)
    }

    private fun parseHostAndPort(): Pair<String, String?> {
        val hostAndPortMatcher = HOST_AND_PORT_EXTRACTOR.matcher(processingUrl)
        if (hostAndPortMatcher.matches()) {
            val host = hostAndPortMatcher.group(1)
            val port = if (hostAndPortMatcher.groupCount() > 2 && hostAndPortMatcher.group(2) != null) {
                hostAndPortMatcher.group(2)
            } else {
                null
            }

            val computedUrl = StringBuilder()
            if (hostAndPortMatcher.group(hostAndPortMatcher.groupCount()) != null) {
                computedUrl.append(hostAndPortMatcher.group(hostAndPortMatcher.groupCount()))
            }
            processingUrl = computedUrl.toString()
            return Pair(host, port)
        } else {
            throw IllegalArgumentException("invalid url:$url")
        }
    }

    private fun parseNameInfo(): Triple<String, String, String> {
        val fullName = if (processingUrl.lastIndexOf(".git") >= 0) {
            processingUrl.substring(0, processingUrl.lastIndexOf(".git"))
        } else {
            processingUrl
        }

        val indexNameAt = fullName.lastIndexOf("/")
        val group = fullName.substring(0, indexNameAt)
        val name = fullName.substring(indexNameAt + 1)
        return Triple(fullName, group, name)
    }
}
