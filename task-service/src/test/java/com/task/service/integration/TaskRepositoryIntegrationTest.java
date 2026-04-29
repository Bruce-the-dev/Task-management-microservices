package com.task.service.integration;

import com.task.service.model.Task;
import com.task.service.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.TestPropertySource;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@TestPropertySource(properties = "spring.mongodb.uri=mongodb://localhost:27017/taskdb-test")
class TaskRepositoryIntegrationTest {

    @Autowired
    private TaskRepository taskRepository;

    @AfterEach
    void cleanUp() {
        taskRepository.deleteAll();
    }

    @Test
    void save_andFindById_works() {
        // Arrange
        Task task = new Task();
        task.setTitle("Finish homework");
        task.setStudentId("student-1");

        // Act
        Task saved = taskRepository.save(task);
        Task found = taskRepository.findById(saved.getId()).orElseThrow();

        // Assert
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getTitle()).isEqualTo("Finish homework");
        assertThat(found.getStudentId()).isEqualTo("student-1");
    }

    @Test
    void findByStudentId_returnsTasksForStudent() {
        // Arrange
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setStudentId("student-1");

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setStudentId("student-1");

        Task task3 = new Task();
        task3.setTitle("Task 3");
        task3.setStudentId("student-2");

        taskRepository.saveAll(List.of(task1, task2, task3));

        // Act
        List<Task> results = taskRepository.findByStudentId("student-1");

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results).allMatch(t -> t.getStudentId().equals("student-1"));
    }
}