package com.tms.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tms.entity.Task;
import com.tms.repositories.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService{ 
	
	private final TaskRepository taskRepository;

	@Override
	public Task findTaskById(UUID taskId) {
		Optional<Task> optional = taskRepository.findById(taskId);
		if (optional.isEmpty()) {
			return null;
		}
		
		return optional.get();
	}

	@Override
	public Task updateTask(Task updatedTask) {
		return taskRepository.save(updatedTask);
	}

	@Override
	public List<Task> findTasksByEmail(String email) {
		List<Task> assignedTasks = taskRepository.findByAssignees_Email(email);
		return assignedTasks;
	}

	
	
}
