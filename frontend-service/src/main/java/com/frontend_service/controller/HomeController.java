package com.frontend_service.controller;

import com.frontend_service.dto.StudentDTO;
import com.frontend_service.dto.TaskDTO;
import com.frontend_service.service.StudentService;
import com.frontend_service.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final StudentService studentService;
    private final TaskService taskService;

@GetMapping("/")
public String home(Model model){
    List<TaskDTO> task = taskService.getAllTasks();
    List<StudentDTO> students =studentService.getAllStudents();
    // Build id → name map
    Map<String, String> studentMap = students.stream()
            .collect(Collectors.toMap(StudentDTO::getId, StudentDTO::getName));

    model.addAttribute("tasks",task);
    model.addAttribute("studentMap", studentMap);
    model.addAttribute("students",students);
    return "home";
}
}

