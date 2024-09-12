package com.nikolchev98.todoapp.domain.dtos.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponseDto {
    private String title;
    private String text;
    private LocalDateTime created;
    private LocalDateTime deadline;
}
