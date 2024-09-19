package com.nikolchev98.todoapp.domain.dtos.responses;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String token;
    private String tokenType = "Bearer ";

    public AuthResponseDto(String token) {
        this.token = token;
    }
}
