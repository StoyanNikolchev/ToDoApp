package com.nikolchev98.todoapp.web;

import com.nikolchev98.todoapp.domain.dtos.imports.TaskImportDto;
import com.nikolchev98.todoapp.domain.views.TaskView;
import com.nikolchev98.todoapp.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody @Valid TaskImportDto taskImportDto, Principal principal, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        return this.taskService.createTask(taskImportDto, principal.getName());
    }

    @PatchMapping
    public ResponseEntity<String> updateTask(@RequestBody TaskView taskView, Principal principal) {
        return this.taskService.updateTask(taskView, principal.getName());
    }

    @DeleteMapping
    public ResponseEntity<String> deleteTask(@RequestBody TaskView taskView, Principal principal) {
        return this.taskService.deleteTask(taskView, principal.getName());
    }
}