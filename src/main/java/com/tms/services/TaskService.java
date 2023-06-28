package com.tms.services;

import java.util.List;
import java.util.UUID;

import com.tms.entity.Task;

public interface TaskService {
	Task findTaskById(UUID taskId);
	
	Task updateTask(Task updatedTask);
	
	List<Task> findTasksByEmail(String email);
	
	
	
}
