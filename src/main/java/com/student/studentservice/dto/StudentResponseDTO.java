package com.student.studentservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentResponseDTO {
    private Long id;
    private String name; //person's table
    private String email; //person's table
    private String branch; //student
    private String department;
    private LocalDate dateOfBirth;
    private String presentAddress;
    private String permanentAddress;

}
