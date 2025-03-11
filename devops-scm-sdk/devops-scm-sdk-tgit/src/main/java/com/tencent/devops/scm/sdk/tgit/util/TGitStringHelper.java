package com.tencent.devops.scm.sdk.tgit.util;

import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class TGitStringHelper {

    public static String formatDate(Date date) {
        return date == null ? null : TGitDateUtils.format(date, TGitDateUtils.YYYY_MM_DD_T_HH_MM_SSZ);
    }

    public static <T> String join(List<T> data) {
        return data == null ? null : StringUtils.join(data, ",");
    }
}
