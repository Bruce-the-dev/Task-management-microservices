package com.frontend_service.service;

import com.frontend_service.dto.StudentDTO;
import com.frontend_service.feign.StudentClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentClient studentClient;

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);


    public List<StudentDTO> getAllStudents() {
        logger.info("Fetching All Students");
        return studentClient.getAllStudents();
    }
    public StudentDTO getStudentById(String id) {
        logger.info("Fetching Student with ID: {}", id);
      return studentClient.getStudentById(id);
    }

    public void updateStudent(String id, StudentDTO student) {
        logger.info("Updating Student with ID: {}", id);
         studentClient.updateStudent(id,student);
    }

    public void deleteStudent(String id) {
        logger.info("Deleting Student with ID: {}", id);
        studentClient.deleteStudent(id);
    }

    public void createStudent(StudentDTO student) {
       logger.info("Creating Student with ID: {}", student.getId());
       studentClient.createStudent(student);
    }
}