package com.nikolchev98.todoapp.domain.views;

import lombok.Data;

import java.util.UUID;

@Data
public class TaskView {
    private UUID uuid;
    private String title;
    private String text;
    private Boolean done;
}
