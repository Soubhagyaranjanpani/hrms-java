package com.hrms.task.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public  class CommentResponse {
    private Long id;
    private String author;      // firstName of createdBy
    private String comment;
    private LocalDateTime createdAt;
}
