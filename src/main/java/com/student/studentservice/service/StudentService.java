package com.student.studentservice.service;

import com.student.studentservice.dto.StudentRequestDTO;
import com.student.studentservice.dto.StudentResponseDTO;

import java.util.ArrayList;
import java.util.List;

public interface StudentService {
    String saveStudent(StudentRequestDTO requestDTO);
    List<StudentResponseDTO> getAllStudents();
    StudentResponseDTO getStudentById(Long id);
    String deleteStudent(Long id);
}
