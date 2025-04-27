package com.tencent.devops.scm.api.util;

public class SvnUtils {
    /**
     * 从SVN URL中提取项目名称
     * @param svnUrl SVN地址
     * @return 项目名称
     */
    public static String getSvnProjectName(String svnUrl) {
        String[] urlSplitArray = svnUrl.split("//");
        if (urlSplitArray.length < 2) {
            throw new IllegalArgumentException("Invalid svn url(" + svnUrl + ")");
        }
        // urlSplitArray[0] -> 协议  urlSplitArray[1] -> repo路径
        String path = urlSplitArray[1];
        String[] pathArray = path.split("/");
        if (pathArray.length < 2) {
            throw new IllegalArgumentException("Invalid svn url(" + svnUrl + ")");
        }
        // pathArray[0] -> 域名
        if (pathArray.length >= 4 && pathArray[3].endsWith("_proj")) {
            // 兼容旧工蜂svn
            return pathArray[1] + "/" + pathArray[2] + "/" + pathArray[3];
        } else {
            return pathArray[1] + "/" + pathArray[2];
        }
    }
}
