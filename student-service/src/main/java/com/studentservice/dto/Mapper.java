package com.studentservice.dto;

import com.studentservice.model.Student;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public StudentDTO mapToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setEmail(student.getEmail());
        dto.setName(student.getName());
        return dto;
    }

    public Student mapToEntity(StudentDTO dto) {
        Student student = new Student();
        student.setId(dto.getId());
        student.setName(dto.getName());
        student.setName(dto.getName());
        return student;
    }

    public Student mapToStudent(StudentRequest request) {
        Student student = new Student();
        if (student.getId() != null) {
            student.setId(request.getId());
        }
        student.setEmail(request.getEmail());
        student.setName(request.getName());
        return student;
    }
}
