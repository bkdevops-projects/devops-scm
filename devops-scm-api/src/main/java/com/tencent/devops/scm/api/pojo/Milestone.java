package com.tencent.devops.scm.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Milestone {
    private Integer id;
    private String title;
    private String state;
    private Integer iid;
    private Date dueDate;
    private String description;
}
