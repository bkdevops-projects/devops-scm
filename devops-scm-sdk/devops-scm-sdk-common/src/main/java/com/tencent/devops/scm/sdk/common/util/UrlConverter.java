package com.tencent.devops.scm.sdk.common.util;

/**
 * URL 转换
 */
public class UrlConverter {

    public static final String GIT_HTTPS_PREFIX = "https://";
    public static final String GIT_HTTP_PREFIX = "http://";
    public static final String HTTP_REGEX = "^(" + GIT_HTTP_PREFIX + "|" + GIT_HTTPS_PREFIX + ")";
    public static final String GIT_SSH_PREFIX = "git@";
    public static final String GIT_PATH_SEPARATOR = ":";
    public static final String GIT_FILE_SUFFIX = ".git";

    /**
     * 将HTTP格式的Git链接转换为SSH协议的链接
     *
     * @param httpUrl HTTP格式的Git仓库链接
     * @return SSH格式的Git仓库链接
     */
    public static String gitHttp2Ssh(String httpUrl) {
        if (httpUrl == null || httpUrl.isEmpty()) {
            throw new IllegalArgumentException("HTTP URL cannot be null or empty");
        }

        // 移除http://或https://前缀
        String urlWithoutProtocol = httpUrl.replaceFirst(HTTP_REGEX, "");

        // 替换为ssh://格式，并添加git@前缀
        String sshUrl = GIT_SSH_PREFIX + urlWithoutProtocol.replaceFirst("/", GIT_PATH_SEPARATOR);
        if (!sshUrl.endsWith(GIT_FILE_SUFFIX)) {
            sshUrl = sshUrl + GIT_FILE_SUFFIX;
        }
        return sshUrl;
    }
}
