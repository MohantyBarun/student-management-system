package com.student.studentservice.service;

import com.student.studentservice.dto.StudentRequestDTO;
import com.student.studentservice.dto.StudentResponseDTO;
import com.student.studentservice.entity.LoginSecurity;
import com.student.studentservice.entity.Persons;
import com.student.studentservice.entity.Student;
import com.student.studentservice.entity.User;
import com.student.studentservice.exception.DuplicateEmailException;
import com.student.studentservice.exception.ResourceNotFoundException;
import com.student.studentservice.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements  StudentService{
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final LoginSecurityRepository loginSecurityRepository;
    private final PersonsRepository personsRepository;
    @Transactional
    @Override
    public String registerStudent(StudentRequestDTO requestDTO) {

        log.info("Attempting to save student with email: {}", requestDTO.getEmail());

        if (personsRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateEmailException(requestDTO.getEmail());
        }
        Persons person=new Persons();
        Student student = new Student();
        person.setName(requestDTO.getName());
        person.setEmail(requestDTO.getEmail());
        student.setBranch(requestDTO.getBranch());
        student.setDepartment(requestDTO.getDepartment());
        student.setPermanentAddress(requestDTO.getPermanentAddress());
        student.setPresentAddress(requestDTO.getPresentAddress());
        student.setDateOfBirth(requestDTO.getDateOfBirth());

        Persons savedPerson= personsRepository.save(person);
        personsRepository.flush();
        student.setPersons(savedPerson);
        Student savedStudent = studentRepository.save(student);
        studentRepository.flush();
        String tempPassword=generateTempPassword();

        User user=new User();
//        user.setId(savedStudent.getId());
        user.setEmail(savedPerson.getEmail());
        user.setRole(requestDTO.getRole());
        user.setTempPassword(passwordEncoder.encode(tempPassword));
        user.setTempPasswordExpiry(LocalDateTime.now().plusHours(48));
        user.setTempPasswordAttemptsCount(0);
        user.setIsFirstTimeLogin(true);
        user.setIsActive(true);
        user.setIsEmailVerified(false);
        user.setPersons(savedPerson);
        user.setPassword(null);
        userRepository.save(user);

        LoginSecurity loginSecurity=new LoginSecurity();
//        loginSecurity.setId(savedStudent.getId());
        loginSecurity.setAccountStatus(AccountStatus.ACTIVE);
        loginSecurity.setWrongPasswordCount(0);
        loginSecurity.setPermanentBlock(false);
        loginSecurity.setUser(user);
        loginSecurityRepository.save(loginSecurity);

        log.info("Student registered successfully with id: {} and temp password sent to: {}",
                savedPerson.getId(), savedPerson.getEmail());
        //Temporary fix in order to allow login until sending email functionality is implemented
        log.info("Password generated is: "+ tempPassword);

        return "Student registered successfully with id: " + savedPerson.getId()
                + ". Temp password sent to: " + savedPerson.getEmail();
    }
    @Override
    public List<StudentResponseDTO> getAllStudents(){
        List<Student> studentsList= studentRepository.findAll();
        List<StudentResponseDTO> studentResponseDTOList = new ArrayList<>();
        for(Student student: studentsList){
            StudentResponseDTO responseDTO=new StudentResponseDTO();
            responseDTO.setId(student.getId());
//            responseDTO.setName(student.getName());
//            responseDTO.setEmail(student.getEmail());
            studentResponseDTOList.add(responseDTO);
        }
        return studentResponseDTOList;
    }
    @Override
    public StudentResponseDTO getStudentById(Long id){
        Student student= studentRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Student not found with id: "+ id));
        StudentResponseDTO studentResponseDTO=new StudentResponseDTO();
        studentResponseDTO.setId(student.getId());
//        studentResponseDTO.setName(student.getName());
//        studentResponseDTO.setEmail(student.getEmail());
        return  studentResponseDTO;
    }
    @Override
    public String deleteStudent(Long id){
        studentRepository.deleteById(id);
        return ("Student deleted successfully");

    }
    private String generateTempPassword() {
        // generates a random 8 character password like "Xk9#mP2q"
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$!";
        StringBuilder tempPassword = new StringBuilder();
        Random random = new SecureRandom(); // SecureRandom — more secure than Random!
        for (int i = 0; i < 8; i++) {
            tempPassword.append(chars.charAt(random.nextInt(chars.length())));
        }
        return tempPassword.toString();
    }
    }
