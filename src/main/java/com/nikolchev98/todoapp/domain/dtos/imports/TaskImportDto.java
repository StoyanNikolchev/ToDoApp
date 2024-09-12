package com.nikolchev98.todoapp.domain.dtos.imports;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskImportDto {

    @NotNull
    @Size(min = 5, max = 30, message = "The title must be between 5 and 30 characters long.")
    private String title;

    @NotNull
    @Size(min = 5, max = 500, message = "The text must be between 5 and 500 characters long.")
    private String text;

    @Future(message = "The deadline must be in the future.")
    private LocalDateTime deadline;
}
