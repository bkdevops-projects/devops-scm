package com.tencent.devops.scm.provider.gitee.simple.utils;

import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;

public class GiteeRepoInfoUtils {

    public static Pair<String, String> convertRepoName(@NonNull Object repoName) {
        Pair<String, String> result = null;
        if (repoName instanceof String) {
            String[] repoNameArr = ((String) repoName).split("/");
            if (repoNameArr.length == 2) {
                result = Pair.of(repoNameArr[0], repoNameArr[1]);
            }
        }
        return result;
    }
}
