package com.nikolchev98.todoapp.domain.dtos.imports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginFormDto {
    private String username;
    private String password;
}
