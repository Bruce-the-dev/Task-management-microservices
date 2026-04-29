package com.task.service.service;

import com.task.service.dto.StudentResponseDTO;
import com.task.service.dto.TaskDTO;
import com.task.service.dto.TaskRequest;
import com.task.service.exception.ResourceNotFoundException;
import com.task.service.exception.ServiceUnavailableException;
import com.task.service.feign.StudentClient;
import com.task.service.model.Task;
import com.task.service.repository.TaskRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private StudentClient studentClient;


    @InjectMocks
    private TaskService taskService;

    // Real mapper — no need to mock it, it has no dependencies
//    @Spy
//    private final Mapper mapper = new Mapper();

    // ---- getTaskById ----

    @Test
    void getTaskById_returnsTaskWithStudent_whenStudentExists() {
        // Arrange
        Task task = new Task();
        task.setId("task-1");
        task.setTitle("Finish homework");
        task.setStudentId("student-1");

        StudentResponseDTO student = new StudentResponseDTO();
        student.setId("student-1");
        student.setName("Alice");

        when(taskRepository.findById("task-1")).thenReturn(Optional.of(task));
        when(studentClient.getStudentById("student-1")).thenReturn(student);

        // Act
        TaskDTO result = taskService.getTaskById("task-1");

        // Assert
        assertThat(result.getTitle()).isEqualTo("Finish homework");
        assertThat(result.getStudentName()).isEqualTo("Alice");
    }

    @Test
    void getTaskById_returnsTaskWithUnknownStudent_whenStudentNotFound() {
        // Arrange
        Task task = new Task();
        task.setId("task-1");
        task.setTitle("Finish homework");
        task.setStudentId("student-1");

        when(taskRepository.findById("task-1")).thenReturn(Optional.of(task));
        when(studentClient.getStudentById("student-1")).thenThrow(FeignException.NotFound.class);

        // Act
        TaskDTO result = taskService.getTaskById("task-1");

        // Assert
        assertThat(result.getTitle()).isEqualTo("Finish homework");
        assertThat(result.getStudentName()).isEqualTo("Unknown");
    }

    @Test
    void getTaskById_throwsResourceNotFoundException_whenTaskDoesNotExist() {
        // Arrange
        when(taskRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Assert + Act
        assertThatThrownBy(() -> taskService.getTaskById("nonexistent")).isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("nonexistent");
    }

    @Test
    void getTaskById_throwsServiceUnavailableException_whenStudentServiceIsDown() {
        // Arrange
        Task task = new Task();
        task.setId("task-1");
        task.setTitle("Finish homework");
        task.setStudentId("student-1");

        when(taskRepository.findById("task-1")).thenReturn(Optional.of(task));
        when(studentClient.getStudentById("student-1")).thenThrow(FeignException.class);  // generic FeignException = service down

        // Assert
        assertThatThrownBy(() -> taskService.getTaskById("task-1")).isInstanceOf(ServiceUnavailableException.class);
    }

    @Test
    void deleteTask_deletesSuccessfully_whenTaskExists() {
        Task task = new Task();
        task.setId("task-1");
        task.setTitle("Finish homework");
        task.setStudentId("student-1");

        when(taskRepository.findById("task-1")).thenReturn(Optional.of(task));
        // Act
        taskService.deleteTask("task-1");

        // Assert — verify the repository was actually told to delete the task
        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_throwsResourceNotFoundException_whenTaskDoesNotExist() {
        // Arrange
        when(taskRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Assert + Act
        assertThatThrownBy(() -> taskService.deleteTask("nonexistent")).isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("Task not found with id");

    }

    @Test
    void createTask_createsSuccessfully_whenStudentExists() {
        // Arrange
        TaskRequest newTask = new TaskRequest();
        newTask.setTitle("Finish homework");
        newTask.setStudentId("student-1");
        newTask.setDescription("Words");

        StudentResponseDTO student = new StudentResponseDTO();
        student.setId("student-1");

        Task savedTask = new Task();
        savedTask.setId("task-1");
        savedTask.setTitle("Finish homework");
        savedTask.setStudentId("student-1");

        when(studentClient.getStudentById("student-1")).thenReturn(student);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        TaskDTO result = taskService.createTask(newTask);

        // Assert (result)
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("task-1");
        assertThat(result.getTitle()).isEqualTo("Finish homework");
        assertThat(result.getStudentId()).isEqualTo("student-1");

        // Assert (what was saved)
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture()); // capture what was passed

        Task taskToSave = captor.getValue(); // now inspect it
        assertThat(taskToSave.getTitle()).isEqualTo("Finish homework");
        assertThat(taskToSave.getStudentId()).isEqualTo("student-1");
        assertThat(taskToSave.getDescription()).isEqualTo("Words");

        // Verify interactions
        verify(studentClient).getStudentById("student-1");
    }

    @Test
    void createTask_throwsResourceNotFoundException_whenStudentDoesNotExist() {
        TaskRequest Newtask = new TaskRequest();
        Newtask.setTitle("Finish homework");
        Newtask.setStudentId("student-1");
        Newtask.setDescription("Words");


        when(studentClient.getStudentById("student-1")).thenThrow(FeignException.NotFound.class);

        assertThatThrownBy(() -> taskService.createTask(Newtask)).isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("Student does not exist");

    }

    @Test
    void createTask_throwsIllegalArgumentException_whenTitleIsBlank() {
        TaskRequest Newtask = new TaskRequest();
        Newtask.setTitle(" ");
        Newtask.setDescription("Words");
        Newtask.setStudentId("student-1");

        assertThatThrownBy(() -> taskService.createTask(Newtask)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Task title cannot be empty");

    }

    @Test
    void createTask_doesNotSave_whenStudentDoesNotExist() {
        // Arrange
        TaskRequest newTask = new TaskRequest();
        newTask.setTitle("Finish homework");
        newTask.setStudentId("student-1");

        when(studentClient.getStudentById("student-1")).thenThrow(mock(FeignException.NotFound.class));

        // Act + Assert
        assertThatThrownBy(() -> taskService.createTask(newTask)).isInstanceOf(ResourceNotFoundException.class);

        verify(taskRepository, never()).save(any());
    }
}