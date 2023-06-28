package com.tms.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.entity.Task;

public interface TaskRepository extends JpaRepository<Task, UUID> {
	List<Task> findByAssignees_Email(String email);
}
