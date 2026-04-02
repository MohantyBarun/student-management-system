package com.student.studentservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {
    @NotBlank(message = "Name cannot be blank")
    @Size(min =8, max = 20, message = "Name must be between 8 and 20 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message ="Password must contain at least one lowercase, one uppercase, one digit and one special character"
    )
    private String password;

    @NotBlank(message = "Email cannot be null")
    @Email(message = "Invalid format of email")
    private String email;

}
