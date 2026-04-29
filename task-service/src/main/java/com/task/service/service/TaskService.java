package com.task.service.service;

import com.task.service.dto.Mapper;
import com.task.service.dto.StudentResponseDTO;
import com.task.service.dto.TaskDTO;
import com.task.service.dto.TaskRequest;
import com.task.service.exception.ResourceNotFoundException;
import com.task.service.exception.ServiceUnavailableException;
import com.task.service.feign.StudentClient;
import com.task.service.model.Task;
import com.task.service.repository.TaskRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final StudentClient studentClient;
    private final Mapper mapper;

    // CREATE
    @Transactional
    public TaskDTO createTask(TaskRequest request) {
        Task task = mapper.mapToTask(request);

        validateFields(task);

        // Single Feign call — validates existence AND captures result for the response DTO
        StudentResponseDTO student = null;
        if (task.getStudentId() != null) {
            student = validateAndFetchStudent(task.getStudentId());
        }

        Task saved = taskRepository.save(task);
        logger.info("Created task with id: {} and title: {}", saved.getId(), saved.getTitle());

        return mapper.mapToDTO(saved, student);
    }

    // READ ALL
    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();

        // Collect unique studentIds — avoids one Feign call per task (N+1 problem)
        Set<String> studentIds = tasks.stream()
                .map(Task::getStudentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Batch fetch: one call per unique student
        Map<String, StudentResponseDTO> studentMap = new HashMap<>();
        for (String studentId : studentIds) {
            try {
                studentMap.put(studentId, studentClient.getStudentById(studentId));
            } catch (FeignException.NotFound e) {
                // Student was deleted after task was created — not a system error
                logger.warn("Student {} not found during getAllTasks — task will show no student", studentId);
            } catch (FeignException e) {
                // Student Service is down — fail the whole request explicitly
                logger.error("Student Service unavailable during getAllTasks");
                throw new ServiceUnavailableException("Student Service is unavailable. Task list cannot be loaded.");
            }
        }

        logger.info("Fetched {} tasks", tasks.size());

        return tasks.stream()
                .map(task -> mapper.mapToDTO(task, studentMap.get(task.getStudentId())))
                .collect(Collectors.toList());
    }

    // READ ONE
    @Transactional(readOnly = true)  // Fix: was missing @Transactional(readOnly = true)
    public TaskDTO getTaskById(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        StudentResponseDTO student = null;
        if (task.getStudentId() != null) {
            try {
                student = studentClient.getStudentById(task.getStudentId());
            } catch (FeignException.NotFound e) {
                logger.warn("Student not found for task {} — returning task without student info", id);
            } catch (FeignException e) {
                logger.error("Student Service unavailable when fetching task {}", id);
                throw new ServiceUnavailableException("Student Service is currently unavailable.");
            }
        }

        return mapper.mapToDTO(task, student);
    }

    // UPDATE
    @Transactional
    public TaskDTO updateTask(String id, TaskRequest request) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        Task updatedFields = mapper.mapToTask(request);
        validateFields(updatedFields);

        // Single Feign call — validates existence AND captures result for the response DTO
        StudentResponseDTO student = null;
        if (updatedFields.getStudentId() != null) {
            student = validateAndFetchStudent(updatedFields.getStudentId());
        }

        existingTask.setTitle(updatedFields.getTitle());
        existingTask.setDescription(updatedFields.getDescription());
        existingTask.setCourseId(updatedFields.getCourseId());
        existingTask.setStudentId(updatedFields.getStudentId());

        Task saved = taskRepository.save(existingTask);
        logger.info("Updated task with id: {}", saved.getId());

        return mapper.mapToDTO(saved, student);
    }

    // DELETE
    @Transactional
    public void deleteTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
        logger.info("Deleted task with id: {}", id);
    }

    // Called internally when a student is deleted — will become a RabbitMQ listener
    @Transactional
    public void nullifyTasksByStudent(String studentId) {
        List<Task> tasks = taskRepository.findByStudentId(studentId);
        tasks.forEach(task -> task.setStudentId(null));
        taskRepository.saveAll(tasks);
        logger.info("Nullified {} tasks for deleted student {}", tasks.size(), studentId);
    }

    // --- Private helpers ---

    // Fix: field validation separated from remote service validation
    private void validateFields(Task task) {
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
    }

    // Fix: validates student exists AND returns the DTO — eliminates the double Feign call
    private StudentResponseDTO validateAndFetchStudent(String studentId) {
        try {
            return studentClient.getStudentById(studentId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Student does not exist: " + studentId);
        } catch (FeignException e) {
            throw new ServiceUnavailableException("Student Service is currently unavailable. Cannot validate student.");
        }
    }
}