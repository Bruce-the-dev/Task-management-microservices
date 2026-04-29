package com.frontend_service.feign;

import com.frontend_service.dto.TaskDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "task-service", path = "/tasks")
public interface TaskClient {

    @GetMapping
    List<TaskDTO> getAllTasks();

    @GetMapping("/{id}")
    TaskDTO getTaskById(@PathVariable String id);

    @PostMapping
    TaskDTO createTask(@RequestBody TaskDTO task);

    @PutMapping("/{id}")
    TaskDTO updateTask(@PathVariable String id, @RequestBody TaskDTO task);

    @DeleteMapping("/{id}")
    void deleteTask(@PathVariable String id);
}