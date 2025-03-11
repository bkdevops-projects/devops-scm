package com.tencent.devops.scm.api.pojo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Issue {
    private Long id;
    private Integer number;
    private String title;
    private String body;
    private String link;
    private List<String> labels;
    private Boolean closed;
    private Boolean locked;
    private User author;
    private Date created;
    private Date updated;
    private String milestoneId;
    private String state;
}
