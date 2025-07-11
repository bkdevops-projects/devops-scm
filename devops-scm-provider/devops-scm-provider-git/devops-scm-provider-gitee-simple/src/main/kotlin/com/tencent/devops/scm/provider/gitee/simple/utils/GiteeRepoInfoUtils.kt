package com.tencent.devops.scm.provider.gitee.simple.utils

object GiteeRepoInfoUtils {

    fun convertRepoName(repoName: Any): Pair<String, String> {
        return when (repoName) {
            is String -> {
                val repoNameArr = repoName.split("/")
                if (repoNameArr.size == 2) {
                    Pair(repoNameArr[0], repoNameArr[1])
                } else {
                    null
                }
            }

            else -> null
        } ?: throw IllegalArgumentException("repoName($repoName) is not supported")
    }
}
