package com.nikolchev98.todoapp.services;

import com.nikolchev98.todoapp.domain.dtos.imports.TaskImportDto;
import com.nikolchev98.todoapp.domain.dtos.responses.TaskResponseDto;
import com.nikolchev98.todoapp.domain.entities.TaskEntity;
import com.nikolchev98.todoapp.domain.entities.UserEntity;
import com.nikolchev98.todoapp.domain.views.TaskView;
import com.nikolchev98.todoapp.repositories.TaskRepository;
import com.nikolchev98.todoapp.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    public ResponseEntity<Page<TaskView>> getUserTasks(String username, Pageable pageable) {
        Page<TaskEntity> taskEntitiesPage = this.taskRepository.findAllByOwnerUsername(username, pageable);
        Page<TaskView> taskViewsPage = taskEntitiesPage.map(entity -> this.modelMapper.map(entity, TaskView.class));
        return new ResponseEntity<>(taskViewsPage, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<TaskResponseDto> createTask(TaskImportDto taskImportDto, String username) {
        TaskEntity taskEntity = this.modelMapper.map(taskImportDto, TaskEntity.class);
        UserEntity userEntity = this.userRepository.findByUsername(username).get();

        taskEntity.setOwner(userEntity);
        taskEntity.setDone(false);
        taskEntity.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        this.taskRepository.save(taskEntity);

        TaskResponseDto taskResponse = this.modelMapper.map(taskImportDto, TaskResponseDto.class);
        taskResponse.setCreated(taskEntity.getCreated());
        return new ResponseEntity<>(taskResponse, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<String> updateTask(TaskView taskView, String username) {
        TaskEntity taskEntity = this.taskRepository.findByIdAndOwnerUsername(taskView.getId(), username)
                .orElse(null);

        if (taskEntity == null) {
            return new ResponseEntity<>("Invalid task.", HttpStatus.BAD_REQUEST);
        }

        taskEntity.setTitle(taskView.getTitle());
        taskEntity.setText(taskView.getText());
        taskEntity.setDone(taskView.getDone());
        taskEntity.setDeadline(taskView.getDeadline());
        this.taskRepository.save(taskEntity);

        return new ResponseEntity<>("Task updated successfully.", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> deleteTask(TaskView taskView, String username) {
        TaskEntity taskEntity = this.taskRepository.findByIdAndOwnerUsername(taskView.getId(), username)
                .orElse(null);

        if (taskEntity == null) {
            return new ResponseEntity<>("Invalid task.", HttpStatus.BAD_REQUEST);
        }

        this.taskRepository.delete(taskEntity);
        return new ResponseEntity<>("Task deleted successfully.", HttpStatus.OK);
    }
}