package com.nikolchev98.todoapp.domain.entities;

import com.nikolchev98.todoapp.domain.enums.Priority;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tasks")
public class TaskEntity extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false)
    private Boolean done;

    @ManyToOne
    private UserEntity owner;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column
    private LocalDateTime deadline;

    @Enumerated(value = EnumType.STRING)
    private Priority priority;
}