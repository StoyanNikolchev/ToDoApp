package com.nikolchev98.todoapp.repositories;

import com.nikolchev98.todoapp.domain.entities.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    Page<TaskEntity> findAllByOwnerUsername(String username, Pageable pageable);
    Optional<TaskEntity> findByIdAndOwnerUsername(UUID id, String username);
    void deleteAllByDoneIsTrue();
}
