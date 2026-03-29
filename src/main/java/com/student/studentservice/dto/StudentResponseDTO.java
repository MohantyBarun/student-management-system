package com.student.studentservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentResponseDTO {
    private Long id;
    private String email;
    private String name;
    private Integer age;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
