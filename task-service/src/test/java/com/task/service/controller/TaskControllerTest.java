package com.task.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.service.dto.TaskDTO;
import com.task.service.dto.TaskRequest;
import com.task.service.exception.ResourceNotFoundException;
import com.task.service.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    void getAllTasks_returns200WithTaskList() throws Exception {
        // Arrange
        TaskDTO task = new TaskDTO();
        task.setId("task-1");
        task.setTitle("Finish homework");
        task.setStudentName("Alice");

        when(taskService.getAllTasks()).thenReturn(List.of(task));

        // Act + Assert
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("task-1"))
                .andExpect(jsonPath("$[0].title").value("Finish homework"))
                .andExpect(jsonPath("$[0].studentName").value("Alice"));
    }
    @Test
    void getTaskById_returns200_whenTaskExists() throws Exception {
        // Arrange
        TaskDTO task = new TaskDTO();
        task.setId("task-1");
        task.setTitle("Finish homework");

        when(taskService.getTaskById("task-1")).thenReturn(task);

        // Act + Assert
        mockMvc.perform(get("/tasks/task-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("task-1"))
                .andExpect(jsonPath("$.title").value("Finish homework"));
    }

    @Test
    void getTaskById_returns404_whenTaskDoesNotExist() throws Exception {
        // Arrange
        when(taskService.getTaskById("nonexistent"))
                .thenThrow(new ResourceNotFoundException("Task not found with id: nonexistent"));

        // Act + Assert
        mockMvc.perform(get("/tasks/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTask_returns201_whenValid() throws Exception {
        // Arrange
        TaskRequest request = new TaskRequest();
        request.setTitle("New task");
        request.setDescription("Do something");
        request.setStudentId("student-1"); // add this

        TaskDTO saved = new TaskDTO();
        saved.setId("task-1");
        saved.setTitle("New task");

        when(taskService.createTask(any(TaskRequest.class))).thenReturn(saved);

        // Act + Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("task-1"))
                .andExpect(jsonPath("$.title").value("New task"));
    }

    @Test
    void deleteTask_returns204_whenTaskExists() throws Exception {
        // Act + Assert
        mockMvc.perform(delete("/tasks/task-1"))
                .andExpect(status().isNoContent());
    }
}