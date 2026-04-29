package com.task.service.dto;

import com.task.service.model.Task;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public TaskDTO mapToDTO(Task task, StudentResponseDTO student) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCourseId(task.getCourseId());
        dto.setStudentId(task.getStudentId());

        if (student != null) {
            dto.setStudentName(student.getName());
        } else {
            dto.setStudentName("Unknown");
        }

        return dto;
    }
    public Task mapToTask(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStudentId(request.getStudentId());
        task.setCourseId(request.getCourseId());
        return task;
    }
}
