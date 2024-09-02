package com.nikolchev98.todoapp.domain.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskDto {

    @NotNull
    @Size(min = 5, max = 30)
    private String title;

    @NotNull
    @Size(min = 5, max = 500)
    private String text;
}
