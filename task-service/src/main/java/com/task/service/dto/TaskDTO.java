package com.task.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {
    private String id;
    private String title;
    private String description;
    private String studentId;
    private String studentName;
    private String courseId;
}
