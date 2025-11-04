package com.tencent.devops.scm.sdk.bkcode.enums;

public enum BkCodeDiffFileRevRange {
    /**
     * 双点语法，表示两个提交之间的差异（不包含起点）
     */
    DOUBLE_DOT(".."),

    /**
     * 三点语法，表示两个提交的共同祖先到终点的差异
     */
    TRIPLE_DOT("...");

    private final String value;

    BkCodeDiffFileRevRange(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 根据字符串值获取对应的枚举实例
     * @param value 版本范围字符串（如".."、"..."）
     * @return 对应的枚举实例，若未匹配则返回null
     */
    public static BkCodeDiffFileRevRange fromValue(String value) {
        for (BkCodeDiffFileRevRange range : BkCodeDiffFileRevRange.values()) {
            if (range.value.equals(value)) {
                return range;
            }
        }
        return null;
    }
}
