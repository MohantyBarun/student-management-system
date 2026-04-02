package com.student.studentservice.dto;

import com.student.studentservice.service.Roles;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentRequestDTO {
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email cannot be null")
    @Email(message = "Invalid format of email")
    private String email;

    @NotBlank(message = "Department cannot be empty")
    private String department;

    @NotBlank(message = "Branch cannot be empty")
    private String branch;

    @NotNull(message = "Date of Birth cannot be null")
    private LocalDate dateOfBirth;

    @NotNull(message = "Role cannot be blank")
    @Enumerated(EnumType.STRING)
    private Roles role= Roles.STUDENT;

    private String presentAddress;

    private String permanentAddress;

}
