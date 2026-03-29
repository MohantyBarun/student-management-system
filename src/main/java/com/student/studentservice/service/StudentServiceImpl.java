package com.student.studentservice.service;

import com.student.studentservice.dto.StudentRequestDTO;
import com.student.studentservice.entity.Student;
import com.student.studentservice.exception.DuplicateEmailException;
import com.student.studentservice.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StudentServiceImpl implements  StudentService{
    @Autowired
    private StudentRepository studentRepository;
    @Override
    public String saveStudent(StudentRequestDTO requestDTO) {

        log.info("Attempting to save student with email: {}", requestDTO.getEmail()); // fix #9

        // fix #6 - duplicate email check
        if (studentRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateEmailException(requestDTO.getEmail());
        }
        Student student = new Student();
        student.setName(requestDTO.getName());
        student.setEmail(requestDTO.getEmail());
        student.setAge(requestDTO.getAge());

        Student savedStudent = studentRepository.save(student);

        log.info("Student saved successfully with id: {}", savedStudent.getId());

        return "Student created successfully with id: " + savedStudent.getId();
    }
    }
