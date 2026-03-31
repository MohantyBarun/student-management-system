package com.student.studentservice.service;

import com.student.studentservice.dto.StudentRequestDTO;
import com.student.studentservice.dto.StudentResponseDTO;
import com.student.studentservice.entity.Student;
import com.student.studentservice.exception.DuplicateEmailException;
import com.student.studentservice.exception.ResourceNotFoundException;
import com.student.studentservice.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @Override
    public List<StudentResponseDTO> getAllStudents(){
        List<Student> studentsList= studentRepository.findAll();
        List<StudentResponseDTO> studentResponseDTOList = new ArrayList<>();
        for(Student student: studentsList){
            StudentResponseDTO responseDTO=new StudentResponseDTO();
            responseDTO.setId(student.getId());
            responseDTO.setName(student.getName());
            responseDTO.setEmail(student.getEmail());
            responseDTO.setAge(student.getAge());
            studentResponseDTOList.add(responseDTO);
        }
        return studentResponseDTOList;
    }
    @Override
    public StudentResponseDTO getStudentById(Long id){
        Student student= studentRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Student not found with id: "+ id));
        StudentResponseDTO studentResponseDTO=new StudentResponseDTO();
        studentResponseDTO.setId(student.getId());
        studentResponseDTO.setName(student.getName());
        studentResponseDTO.setEmail(student.getEmail());
        studentResponseDTO.setAge(student.getAge());
        return  studentResponseDTO;
    }
    @Override
    public String deleteStudent(Long id){
        studentRepository.deleteById(id);
        return ("Student deleted successfully");

    }
    }
