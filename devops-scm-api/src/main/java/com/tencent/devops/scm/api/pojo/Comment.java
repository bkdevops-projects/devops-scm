package com.tencent.devops.scm.api.pojo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private Long id;
    private String body;
    private String link;
    private User author;
    private Date created;
    private Date updated;
    private String type;
}
