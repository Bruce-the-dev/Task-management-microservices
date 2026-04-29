package com.studentservice.repository;

import com.studentservice.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepo extends MongoRepository<Student,String> {
}
