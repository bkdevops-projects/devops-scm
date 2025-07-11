package com.tencent.devops.scm.provider.git.utils

object CollectionUtils {
    /**
     * 填充多个key-value
     * @param value 填充值
     * @param keys key列表
     */
    fun MutableMap<String, Any>.putMultipleKeys(value: Any, keys: Set<String>) {
        keys.forEach { this[it] = value }
    }
}