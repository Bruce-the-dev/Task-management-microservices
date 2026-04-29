package com.frontend_service.controller;

import com.frontend_service.dto.StudentDTO;
import com.frontend_service.dto.TaskDTO;
import com.frontend_service.exception.ServiceUnavailableException;
import com.frontend_service.exception.StudentNotFoundException;
import com.frontend_service.service.StudentService;
import com.frontend_service.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final TaskService taskService;

    @GetMapping
    public String listStudents(Model model) {
        List<StudentDTO> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        return "home"; // OR a separate student list page
    }

    @GetMapping("/create")
    public String showStudentForm() {
        return "create-student";
    }

    @PostMapping("/create")
    public String createStudent(@ModelAttribute StudentDTO student,
                                RedirectAttributes redirectAttributes) {
        try {
            studentService.createStudent(student);
            redirectAttributes.addFlashAttribute("success", "Student created successfully!");
            return "redirect:/";
        } catch (ServiceUnavailableException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/students/create";
        }
    }

    @GetMapping("/{id}")
    public String getStudentDetails(@PathVariable String id, Model model) {
        StudentDTO student = studentService.getStudentById(id);
        if (student == null) {
            model.addAttribute("message", "Student not found");
            return "error";
        }
        List<TaskDTO> tasks = taskService.getAllTasks();

        model.addAttribute("student", student);
        model.addAttribute("tasks", tasks);
        return "studentDetail"; // Thymeleaf template
    }

    @GetMapping("/edit/{id}")
    public String editStudent(@PathVariable String id, Model model) {
        StudentDTO student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        return "edit-student";
    }

    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable String id, @ModelAttribute StudentDTO student) {
        studentService.updateStudent(id, student); // 👈 sends PUT internally
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("success", "Student deleted successfully!");
        } catch (StudentNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred while deleting the student.");
        }
        return "redirect:/";
    }
}
