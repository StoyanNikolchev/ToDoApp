package com.nikolchev98.todoapp.domain.dtos.responses;

import lombok.Data;

@Data
public class RegisterResponseDto {
    private String username;
    private String email;

    public RegisterResponseDto(String username, String email) {
        this.email = email;
        this.username = username;
    }
}
