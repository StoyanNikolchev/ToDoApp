package com.nikolchev98.todoapp.services.schedulers;

import com.nikolchev98.todoapp.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeleteFinishedTasksScheduler {

    private final TaskService taskService;

    @Autowired
    public DeleteFinishedTasksScheduler(TaskService taskService) {
        this.taskService = taskService;
    }

    @Scheduled(cron = "0 0 0 * * 0")
    public void deleteFinishedTasks() {
        this.taskService.deleteFinishedTasks();
    }
}
