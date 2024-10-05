package com.nikolchev98.todoapp.domain.views;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskView {
    private UUID id;
    private String title;
    private String text;
    private Boolean done;
    private LocalDateTime deadline;
    private String priority;
}
