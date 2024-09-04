package com.nikolchev98.todoapp.domain.dtos.responses;

import lombok.Data;

@Data
public class TaskResponseDto {
    private String title;
    private String text;
}
