package com.task.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequest {

    @NotNull
    private String title;
    @NotNull
    private String description;
    private String courseId;
    @NotNull
    private String studentId;

}