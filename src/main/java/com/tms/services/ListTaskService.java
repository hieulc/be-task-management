package com.tms.services;

import java.util.Optional;
import java.util.UUID;

import com.tms.entity.ListTask;

public interface ListTaskService {
	ListTask addTaskToListTask(String taskTitle, ListTask listTask, Integer pos);
	Optional<ListTask> findListTaskById(UUID listId);
	void moveTask(UUID assignedList, UUID unassignedList, UUID taskId, int dropPlace);
	void swapTask(UUID listId, UUID movedTaskId, UUID desTaskId);
}