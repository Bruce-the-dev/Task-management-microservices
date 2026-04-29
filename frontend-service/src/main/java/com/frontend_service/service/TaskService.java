package com.frontend_service.service;

import com.frontend_service.dto.TaskDTO;
import com.frontend_service.feign.TaskClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {


    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskClient taskClient;

    public List<TaskDTO> getAllTasks() {
        logger.info("Fetching all tasks");
        return taskClient.getAllTasks();
    }

    public TaskDTO getTaskById(String id) {
        logger.info("Fetching task by id: {}", id);
        return taskClient.getTaskById(id);
    }

    public void updateTask(String id, TaskDTO task) {
        logger.info("Updating task by id: {}", id);
        taskClient.updateTask(id, task);
    }

    public void deleteTask(String id) {
        logger.info("Deleting task by id: {}", id);
        taskClient.deleteTask(id);
    }

    public void createTask(TaskDTO task) {
        logger.info("Creating task: {}", task);
        taskClient.createTask(task);
    }
}