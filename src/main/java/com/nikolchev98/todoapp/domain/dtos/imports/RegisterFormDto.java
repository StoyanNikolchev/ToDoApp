package com.nikolchev98.todoapp.domain.dtos.imports;

import com.nikolchev98.todoapp.validations.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@FieldMatch(first = "password", second = "confirmPassword", message = "Passwords do not match.")
@Data
public class RegisterFormDto {

    @Size(min = 4, max = 15, message = "Username must be between 4 and 15 characters.")
    @NotBlank(message = "Username must be between 4 and 15 characters.")
    private String username;

    @Email(message = "Invalid email format.")
    @NotBlank(message = "Please enter your email.")
    private String email;

    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters.")
    @NotBlank(message = "Password must be between 8 and 30 characters.")
    private String password;

    private String confirmPassword;
}
