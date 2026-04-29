package com.studentservice.service;

import com.studentservice.dto.Mapper;
import com.studentservice.dto.StudentDTO;
import com.studentservice.dto.StudentRequest;
import com.studentservice.event.StudentEventPublisher;
import com.studentservice.exception.ResourceNotFoundException;
import com.studentservice.model.Student;
import com.studentservice.repository.StudentRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepo repo;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final Mapper mapper;
    private final StudentEventPublisher studentEventPublisher;

    public StudentDTO getById(String id) {
        return mapper.mapToDTO(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id)));
    }

    public List<StudentDTO> getAll() {
        return repo.findAll().stream().map(mapper::mapToDTO).collect(Collectors.toList());
    }

    public StudentDTO save(StudentRequest request) {
        Student student = mapper.mapToStudent(request);
        return mapper.mapToDTO(repo.save(student));
    }

    @Transactional
    public StudentDTO updateStudent(String id, StudentRequest request) {
        Student existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        return mapper.mapToDTO(repo.save(existing));
    }

    @Transactional
    public void deleteStudent(String id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }

        repo.deleteById(id);
        studentEventPublisher.publishStudentDeleted(id);
        logger.info("Deleted student {} and published deletion event", id);
    }
}
