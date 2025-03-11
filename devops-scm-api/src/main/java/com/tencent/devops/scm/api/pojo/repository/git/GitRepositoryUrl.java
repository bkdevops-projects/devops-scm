package com.tencent.devops.scm.api.pojo.repository.git;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;

@Data
public class GitRepositoryUrl {
    public static final String PROTOCOL_HTTP_SEPARATOR = "://";

    public static final String PROTOCOL_GIT_SEPARATOR = "@";

    public static final String PROTOCOL_GIT = "git";

    public static final String PROTOCOL_SSH = "ssh";

    public static final String PROTOCOL_HTTP = "http";

    public static final String PROTOCOL_HTTPS = "https";

    public static final String PROTOCOL_NONE = "";

    private static final Pattern HOST_AND_PORT_EXTRACTOR =
        Pattern.compile("([^:/\\\\~]*)(?::(\\d*))?(?:([:/\\\\~])(.*))?");

    private String url;

    private String protocol;

    private String userName;

    private String password;

    private String host;

    private String port;
    // 代码库组
    private String group;
    // 代码库名
    private String name;
    // 代码库全名,组+名
    private String fullName;

    public GitRepositoryUrl(String url) {
        this.url = url;

        url = parseProtocol(url);
        url = parseUserInfo(url);
        url = parseHostAndPort(url);
        parseGroupAndName(url);
    }

    private String parseProtocol(String url) {
        if (url.startsWith(PROTOCOL_HTTPS + PROTOCOL_HTTP_SEPARATOR)) {
            this.protocol = PROTOCOL_HTTPS;
            url = url.substring(protocol.length() + 3);
        } else if (url.startsWith(PROTOCOL_HTTP + PROTOCOL_HTTP_SEPARATOR)) {
            this.protocol = PROTOCOL_HTTP;
            url = url.substring(protocol.length() + 3);
        } else if (url.startsWith(PROTOCOL_SSH + PROTOCOL_GIT_SEPARATOR)) {
            this.protocol = PROTOCOL_SSH;
            url = url.substring(protocol.length() + 1);
        } else if (url.startsWith(PROTOCOL_GIT + PROTOCOL_GIT_SEPARATOR)) {
            this.protocol = PROTOCOL_GIT;
            url = url.substring(protocol.length() + 1);
        } else {
            this.protocol = PROTOCOL_NONE;
            return url;
        }

        return url;
    }

    private String parseUserInfo(String url) {
        int indexAt = url.lastIndexOf('@');
        if (indexAt >= 0) {
            String userInfo = url.substring(0, indexAt);
            int indexPwdSep = userInfo.indexOf(':');
            if (indexPwdSep < 0) {
                this.userName = userInfo;
            } else {
                this.userName = userInfo.substring(0, indexPwdSep);
                this.password = userInfo.substring(indexPwdSep + 1);
            }

            url = url.substring(indexAt + 1);
        }
        return url;
    }

    private String parseHostAndPort(String url) {

        Matcher hostAndPortMatcher = HOST_AND_PORT_EXTRACTOR.matcher(url);
        if (hostAndPortMatcher.matches()) {
            if (hostAndPortMatcher.groupCount() > 1 && hostAndPortMatcher.group(1) != null) {
                this.host = hostAndPortMatcher.group(1);
            }
            if (hostAndPortMatcher.groupCount() > 2 && hostAndPortMatcher.group(2) != null) {
                this.port = hostAndPortMatcher.group(2);
            }

            StringBuilder computedUrl = new StringBuilder();
            if (hostAndPortMatcher.group(hostAndPortMatcher.groupCount()) != null) {
                computedUrl.append(hostAndPortMatcher.group(hostAndPortMatcher.groupCount()));
            }
            return computedUrl.toString();
        }
        return url;
    }

    private void parseGroupAndName(String url) {
        int indexAt = url.lastIndexOf(".git");
        if (indexAt >= 0) {
            this.fullName = url.substring(0, indexAt);
        } else {
            this.fullName = url;
        }
        int indexNameAt = fullName.lastIndexOf("/");
        this.group = fullName.substring(0, indexNameAt);
        this.name = fullName.substring(indexNameAt + 1);
    }
}
