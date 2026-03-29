package com.student.studentservice.controller;

import com.student.studentservice.dto.StudentRequestDTO;
import com.student.studentservice.dto.StudentResponseDTO;
import com.student.studentservice.entity.Student;
import com.student.studentservice.service.StudentService;
import com.student.studentservice.service.StudentServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;


    @PostMapping("/save")
    public ResponseEntity<String> createStudent(
            @Valid @RequestBody StudentRequestDTO requestDTO) {

        log.info("POST /students/save called");

        String message = studentService.saveStudent(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(message);

    }

}


