package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BkCode 分页
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BkCodePage<T> {
    /**
     * 总记录行数
     */
    private Long count;
    /**
     * 第几页
     */
    private Integer page;
    /**
     * 每页多少条
     */
    @JsonProperty("pageSize")
    private Integer pageSize;
    /**
     * 总共多少页
     */
    @JsonProperty("totalPages")
    private Integer totalPages;
    /**
     * 数据
     */
    private List<T> records;
}
    