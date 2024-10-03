package com.nikolchev98.todoapp.web.restControllers;

import com.nikolchev98.todoapp.domain.dtos.imports.TaskImportDto;
import com.nikolchev98.todoapp.domain.views.TaskView;
import com.nikolchev98.todoapp.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskRESTController {
    private final TaskService taskService;

    @Autowired
    public TaskRESTController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<Page<TaskView>> getUserTasks(Principal principal,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        return this.taskService.getUserTasks(principal.getName(), pageable);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody @Valid TaskImportDto taskImportDto, BindingResult bindingResult, Principal principal) {
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
    public ResponseEntity<String> deleteTask(@RequestBody UUID taskID, Principal principal) {
        return this.taskService.deleteTask(taskID, principal.getName());
    }
}