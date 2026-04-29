package com.frontend_service.controller;

import com.frontend_service.dto.StudentDTO;
import com.frontend_service.dto.TaskDTO;
import com.frontend_service.exception.ServiceUnavailableException;
import com.frontend_service.exception.TaskNotFoundException;
import com.frontend_service.service.StudentService;
import com.frontend_service.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final StudentService studentService;
    private final TaskService taskService;

    @GetMapping
    public String listTasks(Model model) {

        model.addAttribute("tasks",taskService.getAllTasks());
        return "home"; // Shows combined dashboard OR task list page
    }

    @GetMapping("/create")
    public String showTaskForm(Model model) {
        model.addAttribute("students",studentService.getAllStudents());
        model.addAttribute("task", new TaskDTO());
        return "create-task";
    }

    @PostMapping("/create")
    public String createTask(@ModelAttribute TaskDTO task, Model model, RedirectAttributes redirectAttributes) {
        try {
            taskService.createTask(task);
            redirectAttributes.addFlashAttribute("success", "Task created!");
            return "redirect:/"; // Success → dashboard
        } catch (ServiceUnavailableException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("task", task); // keep entered data
            return "create-task";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("task", task);
            return "create-task";
        }
    }
    @GetMapping("/edit/{id}")
    public String editTask(@PathVariable String id, Model model) {
        TaskDTO task = taskService.getTaskById(id);
        if (task == null) {
            return "redirect:/";
        }
        model.addAttribute("task", task);
        model.addAttribute("students", studentService.getAllStudents());
        return "edit-task";
    }


    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable String id, @ModelAttribute TaskDTO task, RedirectAttributes redirectAttributes) {
//        System.out.println(id);
        try {
        taskService.updateTask(id, task); // 👈 sends PUT internally
        redirectAttributes.addFlashAttribute("success", "Task updated!");
        }catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }
    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            taskService.deleteTask(id);
            redirectAttributes.addFlashAttribute("success", "Task deleted successfully!");
        } catch (TaskNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred while deleting the task.");
        }
        return "redirect:/";
    }
}
