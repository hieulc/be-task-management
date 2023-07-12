package com.tms.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tms.entity.Employee;
import com.tms.entity.Task;
import com.tms.models.TableData;
import com.tms.services.EmployeeService;
import com.tms.services.TaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
	
	private final TaskService taskService;
	private final EmployeeService employeeService;
	
	
	@GetMapping("/{taskId}")
	public ResponseEntity<Task> findTaskById(@PathVariable("taskId") UUID taskId) {
		Task task = taskService.findTaskById(taskId);
		if (task == null) {
			return ResponseEntity.notFound().build();
		}
		
		return new ResponseEntity<Task>(task, HttpStatus.OK);
	}
	
	@GetMapping("/assignee/{taskId}")
	public ResponseEntity<Task> addAssigneeToTask(@PathVariable("taskId") UUID taskId, @RequestParam("assigneeId") Integer assigneeId) {
		Task updatedTask = taskService.findTaskById(taskId);
		Optional<Employee> optional = employeeService.getEmployeeById(assigneeId);
		if (optional.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		updatedTask.addAssigneeToTask(optional.get());
		
		taskService.updateTask(updatedTask);
		
		return new ResponseEntity<Task>(updatedTask, HttpStatus.OK);
	}
	
	@DeleteMapping("/assignee/{taskId}")
	public ResponseEntity<Task> removeAssignee(@PathVariable("taskId") UUID taskId, @RequestParam("assigneeId") Integer assigneeId) {
		Task updatedTask = taskService.findTaskById(taskId);
		Optional<Employee> optional = employeeService.getEmployeeById(assigneeId);	
		updatedTask.removeAssigneeToTask(optional.get());
		taskService.updateTask(updatedTask);
		return new ResponseEntity<Task>(updatedTask, HttpStatus.OK);
	}
	
	
	@PostMapping("/due-date/{taskId}")
	public ResponseEntity<Task> addDueDateToTask(@PathVariable("taskId") UUID taskId, @RequestBody Task task) {
		Task updatedTask = taskService.findTaskById(task.getTaskId());
		
		updatedTask.setDueDate(task.getDueDate());
//		updatedTask.setWorkingDays(task.getWorkingDays());
	
		Timestamp now = new Timestamp(System.currentTimeMillis());
		updatedTask.setDateAddDueDate(now);
		
		if (now.compareTo(task.getDueDate()) > 0) {
			updatedTask.setOverDue(true);
		} else {
			updatedTask.setOverDue(false);
		}
		taskService.updateTask(updatedTask);
		return new ResponseEntity<Task>(updatedTask, HttpStatus.OK);
	}
	
	@PostMapping("/working-days")
	public ResponseEntity<Task> addWorkingDays(@RequestBody Task task) {
		Task updatedTask = taskService.findTaskById(task.getTaskId());
		
		updatedTask.setWorkingDays(task.getWorkingDays());
		
		taskService.updateTask(updatedTask);
		return new ResponseEntity<Task>(updatedTask, HttpStatus.OK);
	}
	
	@GetMapping("/assigned/{email}")
	public ResponseEntity<List<Task>> findAssignedTasksByEmail(@PathVariable("email") String email) {
		List<Task> assignedTasks = taskService.findTasksByEmail(email);
		return new ResponseEntity<List<Task>>(assignedTasks, HttpStatus.OK);
	}
	
	
	
	@GetMapping("/archive/{taskId}")
	public ResponseEntity<Task> setArchive(@PathVariable("taskId") UUID taskId) {
		Task updatedTask = taskService.findTaskById(taskId);
		updatedTask.setArchived(true);
		
		taskService.updateTask(updatedTask);
		return new ResponseEntity<Task>(updatedTask, HttpStatus.OK);
	}
	
	@GetMapping("/completed/{taskId}")
	public ResponseEntity<Task> setTaskCompleted(@PathVariable("taskId") UUID taskId, @RequestParam("completed") boolean isCompleted, @RequestParam("hoursToComplete") int hoursToComplete) {
		Task updatedTask = taskService.findTaskById(taskId);
		
		updatedTask.setCompleted(isCompleted);
		updatedTask.setHoursToComplete(hoursToComplete);
		taskService.updateTask(updatedTask);
		return new ResponseEntity<Task>(updatedTask, HttpStatus.OK);
	}
}
