package com.student.studentservice.service;

import com.student.studentservice.dto.StudentRequestDTO;
import com.student.studentservice.dto.StudentResponseDTO;

public interface StudentService {
    String saveStudent(StudentRequestDTO requestDTO);
}
