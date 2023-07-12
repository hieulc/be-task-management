package com.tms.services;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.entity.ListTask;
import com.tms.entity.Task;
import com.tms.repositories.ListTaskRepository;
import com.tms.repositories.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListTaskServiceImpl implements ListTaskService {
	
	private final ListTaskRepository listTaskRepository;
	private final TaskRepository taskRepository;

	@Override
	@Transactional
	public ListTask addTaskToListTask(String taskTitle, ListTask listTask, Integer pos) {
		Task task = Task.builder().taskTitle(taskTitle).build();
		task.setCompleted(false);
		task.setOverDue(false);
		task.setArchived(false);
		task.setPos(pos);
		task.setWorkingDays(1);
		
		Optional<ListTask> optional = listTaskRepository.findById(listTask.getListId());
		if (optional.isEmpty()) {
			return null;
		} else {
			ListTask updatedListTask = optional.get();
			updatedListTask.addTask(task);
			taskRepository.save(task);
			listTaskRepository.save(updatedListTask);
			return updatedListTask;
		}
	}

	@Override
	public Optional<ListTask> findListTaskById(UUID listId) {
		return listTaskRepository.findById(listId);
	}

	@Override
	public void moveTask(UUID assignedListId, UUID unassignedListId, UUID taskId, int dropPlace) {
		unassignedTask(unassignedListId, taskId);
		assignedTask(assignedListId, taskId, dropPlace);
	}

	
	private void unassignedTask(UUID unassignedListId, UUID taskId) {
		ListTask unassignedListTask = listTaskRepository.findById(unassignedListId).get();
		Task task = taskRepository.findById(taskId).get();
		int draggedPos = task.getPos();
		int highestPos = unassignedListTask.getTasks().stream().max(Comparator.comparing(Task::getPos)).get().getPos();
		int lowestPos = unassignedListTask.getTasks().stream().min(Comparator.comparing(Task::getPos)).get().getPos();
		
		unassignedListTask.removeTask(task);
		
		if (unassignedListTask.getTasks().size() == 1) {
			System.out.println("CALLED");
			for (Task t : unassignedListTask.getTasks()) {
				int newPos = 1;
				t.setPos(newPos);
				taskRepository.save(t);
			}
		}
		
		else if (draggedPos == lowestPos) {
			for (Task t : unassignedListTask.getTasks()) {
				int newPos = t.getPos() - 1;
				t.setPos(newPos);
				taskRepository.save(t);
			}
		} else if (draggedPos < highestPos && draggedPos > lowestPos) {
			for (Task t : unassignedListTask.getTasks()) {
				if (t.getPos() > draggedPos) {
					int newPos = t.getPos() - 1;
					t.setPos(newPos);
					taskRepository.save(t);
				}
			}
		}
		
		listTaskRepository.save(unassignedListTask);
	}
	
	
	private void assignedTask(UUID assignedListId, UUID taskId, int dropPlace) {
		ListTask assignedListTask = listTaskRepository.findById(assignedListId).get();
		Task updatedTask = taskRepository.findById(taskId).get();
		updatedTask.setPos(dropPlace);
		taskRepository.save(updatedTask);
		assignedListTask.addTask(updatedTask);
		
		int highestPos = assignedListTask.getTasks().stream().max(Comparator.comparing(Task::getPos)).get().getPos();
		int lowestPos = assignedListTask.getTasks().stream().min(Comparator.comparing(Task::getPos)).get().getPos();
		
		if (dropPlace == lowestPos) {
			for (Task t : assignedListTask.getTasks()) {
				if (!t.getTaskId().equals(updatedTask.getTaskId())) {
					int newPos = t.getPos() + 1;
					t.setPos(newPos);
					taskRepository.save(t);
				}
			}
		} else if (dropPlace > lowestPos && dropPlace <= highestPos) {
			for (Task t : assignedListTask.getTasks()) {
				if (t.getPos() >= dropPlace && !t.getTaskId().equals(updatedTask.getTaskId())) {
					int newPos = t.getPos() + 1;
					t.setPos(newPos);
					taskRepository.save(t);
				}
			}
		}
		

		listTaskRepository.save(assignedListTask);
		
	}

	@Override
	public void swapTask(UUID listId, UUID movedTaskId, UUID desTaskId) {
		Task movedTask = taskRepository.findById(movedTaskId).get();
		Task desTask = taskRepository.findById(desTaskId).get();
		ListTask listTask = listTaskRepository.findById(listId).get();
		
		int sourcePos = movedTask.getPos();
		int desPos = desTask.getPos();
		
		if (sourcePos - desPos == 1 || desPos - sourcePos == 1) {
			int tmp = movedTask.getPos();
			movedTask.setPos(desTask.getPos());
			desTask.setPos(tmp);
			
			taskRepository.save(movedTask);
			taskRepository.save(desTask);
		} else {
			if (desPos - sourcePos > 1) {
				for (Task task : listTask.getTasks()) {
					if (task.getPos() <= desPos && task.getPos() != sourcePos) {
						int newPos = task.getPos() - 1;
						task.setPos(newPos);
						taskRepository.save(task);
					}
					
					if (task.getTaskId().equals(movedTaskId)) {
						task.setPos(desPos);
						taskRepository.save(task);
					}
				}
			} else {
				for (Task task : listTask.getTasks()) {
					if (task.getPos() >= desPos && task.getPos() != sourcePos) {
						int newPos = task.getPos() + 1;
						task.setPos(newPos);
						taskRepository.save(task);
					}
					
					if (task.getTaskId().equals(movedTaskId)) {
						task.setPos(desPos);
						taskRepository.save(task);
					}
				}
			}
		}
		
		
		
		
	}
	

}
