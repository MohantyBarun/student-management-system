package com.student.studentservice.controller;

import com.student.studentservice.dto.StudentRequestDTO;
import com.student.studentservice.dto.StudentResponseDTO;
import com.student.studentservice.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/getAll")
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents(){
        log.info("GET /students/getAll called");
        List<StudentResponseDTO> students= studentService.getAllStudents();
        return ResponseEntity.status(HttpStatus.OK).body(students);
    }
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable Long id){
        log.info("GET /students/{} called", id);
        StudentResponseDTO studentResponseDTO=studentService.getStudentById(id);
        return ResponseEntity.status(HttpStatus.OK).body(studentResponseDTO);
    }

}


