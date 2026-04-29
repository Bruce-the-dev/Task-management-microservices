package com.frontend_service.feign;

import com.frontend_service.dto.StudentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "student-service", path = "/students")
public interface StudentClient {
    @GetMapping
    List<StudentDTO> getAllStudents();

    @GetMapping("/{id}")
    StudentDTO getStudentById(@PathVariable String id);

    @PostMapping
    StudentDTO createStudent(@RequestBody StudentDTO student);

    @PutMapping("/{id}")
    StudentDTO updateStudent(@PathVariable String id, @RequestBody StudentDTO student);

    @DeleteMapping("/{id}")
    void deleteStudent(@PathVariable String id);

}
