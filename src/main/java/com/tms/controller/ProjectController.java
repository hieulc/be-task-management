package com.tms.controller;

import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tms.entity.Employee;
import com.tms.entity.ListTask;
import com.tms.entity.Project;
import com.tms.entity.Task;
import com.tms.models.ChangeOrderRequest;
import com.tms.models.ProjectPagedList;
import com.tms.models.TableData;
import com.tms.models.TaskRequestPayload;
import com.tms.services.EmployeeService;
import com.tms.services.ListTaskService;
import com.tms.services.ProjectService;
import com.tms.services.TaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

	private static final int DEFAULT_PAGE_NUMBER = 0;
	private static final int DEFAULT_PAGE_SIZE = 5;

	private final ProjectService projectService;
	private final ListTaskService listTaskService;
	private final TaskService taskService;
	private final EmployeeService employeeService;

	@GetMapping
	public ProjectPagedList getProjects(
			@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "username", required = true) String username) {

		if (pageNumber == null || pageNumber < 0) {
			pageNumber = DEFAULT_PAGE_NUMBER;
		}

		if (pageSize == null || pageSize < 0) {
			pageSize = DEFAULT_PAGE_SIZE;
		}

		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		return projectService.getAllProject(username, pageable);
	}
	
	@GetMapping("/project/{id}")
	public ResponseEntity<Project> getProjectById(@PathVariable("id") int id) {
		Optional<Project> project =  projectService.findById(id);
		if (project.isPresent()) {
			return new ResponseEntity<Project>(project.get(), HttpStatus.OK);
		}
		
		return ResponseEntity.notFound().build();
	}

	@PostMapping
	public ResponseEntity<Project> saveProject(@RequestBody @Validated Project project) {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/projects").toUriString());
		return ResponseEntity.created(uri).body(projectService.saveProject(project));
	}

	@PutMapping(value = "/{id}", produces = "application/json; charset=utf-8")
	public ResponseEntity<Project> updateProject(@PathVariable("id") int id, @RequestBody Project project) {
		return projectService.findById(id).map(savedProject -> {
			savedProject.setProjectName(project.getProjectName());
			Project updatedProject = projectService.updateProject(savedProject);
			return new ResponseEntity<>(updatedProject, HttpStatus.OK);
		}).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping(value = "/{id}", produces = "application/json; charset=utf-8")
	public ResponseEntity<String> deleteProject(@PathVariable("id") int id) {
		projectService.deleteProject(id);
		return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
	}
	
	@GetMapping(value = "/{projectName}")
	public ResponseEntity<Project> findByProjectName(@PathVariable("projectName") String projectName) {
		Optional<Project> projectOptional = projectService.findProjectByProjectName(projectName);
		if (projectOptional.isPresent()) {
			return new ResponseEntity<>(projectOptional.get(), HttpStatus.FOUND);
		} 
		
		return ResponseEntity.notFound().build();
	}
	
	
	@GetMapping("/exist")
	public Integer findUniqueProject(@RequestParam("createdBy") String createdBy, @RequestParam("projectName") String projectName) {
		return projectService.findUniqueProject(createdBy, projectName);
	}
	
	@PostMapping("/members/{username}")
	public ResponseEntity<Project> addMemberToProject(@PathVariable("username") String username, @RequestBody Project project) {
		Project updatedProject = projectService.addMemberToProject(username, project);
		return new ResponseEntity<Project>(updatedProject, HttpStatus.OK);
	}
	
	@DeleteMapping("/member/{username}")
	public ResponseEntity<Project> removeMemberFromProject(@PathVariable("username") String username, @RequestParam("projectId") Integer projectId) {
		Project updatedProject = projectService.removeMemberFromProject(username, projectId);
		if (updatedProject == null) {
			return ResponseEntity.notFound().build();
		}
		
		return new ResponseEntity<Project>(updatedProject, HttpStatus.OK);
	}
	
	@GetMapping("/list")
	public ResponseEntity<Project> addTaskListToProject(
			@RequestParam("listName") String listName,
			@RequestParam("pos") Integer pos,
			@RequestParam("projectId") Integer projectId) {
		Optional<Project> projectOptional = projectService.findById(projectId);
		if (projectOptional.isEmpty()) {
			return ResponseEntity.notFound().build();
		} else {
			Project updatedProject = projectService.addListTaskToProject(listName, projectOptional.get(), pos);
			return new ResponseEntity<Project>(updatedProject, HttpStatus.OK);
		}
	}
	
	
	
	@GetMapping("/list/task")
	public ResponseEntity<Project> addTaskToListTask(
			@RequestParam("taskTitle") String taskTitle,
			@RequestParam("pos") Integer pos,
			@RequestParam("listId") UUID listId) {
		Optional<ListTask> listTaskOptional = listTaskService.findListTaskById(listId);
		if (listTaskOptional.isEmpty()) {
			return ResponseEntity.notFound().build();
		} else {
			ListTask listTask = listTaskService.addTaskToListTask(taskTitle, listTaskOptional.get(), pos);
			Project project = projectService.findProjectByListId(listId);
			return new ResponseEntity<Project>(project, HttpStatus.OK);
//			return new ResponseEntity<ListTask>(listTask, HttpStatus.OK);
		}
		
	}
	
	@PostMapping("/list/task/{listId}")
	public ResponseEntity<Project> updateTask(@PathVariable("listId") UUID listId, @RequestBody Task task) {
			Task updatedTask = taskService.findTaskById(task.getTaskId());
			updatedTask.setTaskDescr(task.getTaskDescr());
			updatedTask.setTaskTitle(task.getTaskTitle());
			taskService.updateTask(updatedTask);
			
			Project updatedProject = projectService.findProjectByListId(listId);
			return new ResponseEntity<Project>(updatedProject, HttpStatus.OK);
		
	}
	
	@GetMapping("/list/task/assignee")
	public ResponseEntity<Project> addAssigneeToTask(@RequestParam("listId") UUID listId, @RequestParam("employeeId") Integer emplId, @RequestParam("taskId") UUID taskId) {
		Task task = taskService.findTaskById(taskId);
		Optional<Employee> optional = employeeService.getEmployeeById(emplId);
		if (optional.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		else {
			Employee employee = optional.get();
			task.addAssigneeToTask(employee);
			taskService.updateTask(task);
			Project updatedProject = projectService.findProjectByListId(listId);
			return new ResponseEntity<Project>(updatedProject, HttpStatus.OK);
		}
	}
	
	@GetMapping("/list/task/assignees/assignee")
	public ResponseEntity<Project> removeAssignee(@RequestParam("listId") UUID listId, @RequestParam("assigneeId") Integer assigneeId, @RequestParam("taskId") UUID taskId) {
		Task updatedTask = taskService.findTaskById(taskId);
		Optional<Employee> optional = employeeService.getEmployeeById(assigneeId);
		updatedTask.removeAssigneeToTask(optional.get());
		taskService.updateTask(updatedTask);
		Project updatedProject = projectService.findProjectByListId(listId);
		return new ResponseEntity<Project>(updatedProject, HttpStatus.OK);
		
	}
	
	@PostMapping("/list/task/due-date/{listId}")
	public ResponseEntity<Project> addDueDateToTask(@PathVariable("listId") UUID listID, @RequestBody Task task) {
		Task updatedTask = taskService.findTaskById(task.getTaskId());
		updatedTask.setDueDate(task.getDueDate());
		Timestamp now = new Timestamp(System.currentTimeMillis());
		if (now.compareTo(task.getDueDate()) > 0) {
			updatedTask.setOverDue(true);
		} else {
			updatedTask.setOverDue(false);
		}
		taskService.updateTask(updatedTask);
		Project updatedProject = projectService.findProjectByListId(listID);
		return new ResponseEntity<Project>(updatedProject, HttpStatus.OK);
	}
	
	@PostMapping("/list/task/priority/{listId}")
	public ResponseEntity<Project> addPriority(@PathVariable("listId") UUID listID,
			@RequestBody Task task) {
		log.info(task.getTaskPriority().toString());
		Task updatedTask = taskService.findTaskById(task.getTaskId());
		updatedTask.setTaskPriority(task.getTaskPriority());
		taskService.updateTask(updatedTask);
		Project updatedProject = projectService.findProjectByListId(listID);
		return new ResponseEntity<Project>(updatedProject, HttpStatus.OK);
	}
			
	
//	@GetMapping("/employees/{email}")
//	public List<Project> findByMembers_Email(@PathVariable("email") String email) {
//		return projectService.findByMembers_Email(email);
//	}
	
	@GetMapping("/employees/pages")
	public ProjectPagedList getAllProjectsByEmail(
			@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "username", required = true) String email) {

		if (pageNumber == null || pageNumber < 0) {
			pageNumber = DEFAULT_PAGE_NUMBER;
		}

		if (pageSize == null || pageSize < 0) {
			pageSize = DEFAULT_PAGE_SIZE;
		}

		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		return projectService.findAllProjectsByEmail(email, pageable);
	}
	
	@GetMapping("project/listTask/{listId}")
	public ResponseEntity<Project> getProjectByListId(@PathVariable("listId") UUID listId) {
		Project project = projectService.findProjectByListId(listId);
		if (project == null) {
			return ResponseEntity.notFound().build();
		} else {
			return new ResponseEntity<Project>(project, HttpStatus.OK);
		}
	}
	
	@PostMapping("/project/listTask/movedTask")
	public ResponseEntity<Project> moveTask(@RequestBody TaskRequestPayload payload) {
		UUID assignedListTaskId = payload.getAssignedListTaskId();
		UUID unassignedListTaskId = payload.getUnassignedListTaskId();
		UUID movedTaskId = payload.getMovedTaskId();
		if (assignedListTaskId.equals(unassignedListTaskId)) {
			UUID desTaskId = payload.getDesTaskId();
			listTaskService.swapTask(assignedListTaskId, movedTaskId, desTaskId);
		} else {
			int dropPlace = payload.getDropPlace();
			listTaskService.moveTask(assignedListTaskId, unassignedListTaskId, movedTaskId, dropPlace);
		}
		Project updatedProject = projectService.findProjectByListId(assignedListTaskId);
		return new ResponseEntity<Project>(updatedProject, HttpStatus.OK);
	}
	
	@PostMapping("/project/listTask/order")
	public ResponseEntity<Project> changeOrderColumn(@RequestBody ChangeOrderRequest request) {
		Integer projectId = request.getProjectId();
		UUID sourceId = request.getSourceId();
		UUID desId = request.getDesId();
		
		Project updatedProject = projectService.changeColumnOrder(projectId, sourceId, desId);
		if (updatedProject == null) {
			return ResponseEntity.notFound().build();
		}
		
		return new ResponseEntity<Project>(updatedProject, HttpStatus.OK);
	}
	
	@GetMapping("/createdBy")
	public ProjectPagedList getAllProjectsByCreatedBy(
			@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "username", required = true) String email) {

		if (pageNumber == null || pageNumber < 0) {
			pageNumber = DEFAULT_PAGE_NUMBER;
		}

		if (pageSize == null || pageSize < 0) {
			pageSize = DEFAULT_PAGE_SIZE;
		}

		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		return projectService.getProjectsByCreatedBy(email, pageable);
	} 
	
	@GetMapping("/tableData/{projectId}")
	public ResponseEntity<List<TableData>> fetchTableData(@PathVariable("projectId") int projectId) {
		Project project = projectService.findById(projectId).get();
		Set<Employee> members = project.getMembers();
		Set<ListTask> listTasks = project.getListOfTasks();
		
		Set<Task> tasks = new HashSet<>();
		
		for (ListTask list : listTasks) {
			tasks.addAll(list.getTasks());
		}
		
		List<TableData> tableData2 = new ArrayList<>();
		
		for (Task task : tasks) {
			for (Employee member : members) {
				for (Employee assignee : task.getAssignees()) {
					if (assignee.getEmail().equals(member.getEmail())) {
						if (tableData2.size() == 0) {
							List<UUID> listTasksId = new ArrayList<>();
							if (task.isCompleted()) {
								listTasksId.add(task.getTaskId());
								TableData firstData = TableData.builder()
										.username(member.getEmail())
										.workingDays(task.getWorkingDays())
										.completedTasks(1)
										.assignedTasksId(listTasksId)
										.build();
								tableData2.add(firstData);
							}
							else {
								TableData firstData = TableData.builder()
										.username(member.getEmail())
										.workingDays(task.getWorkingDays())
										.completedTasks(0)
										.assignedTasksId(listTasksId)
										.build();
								tableData2.add(firstData);
							}
							
						} else {
							for (int i = 0; i < tableData2.size(); i++) {
								TableData data = tableData2.get(i);
								
								if (member.getEmail().equals(data.getUsername()) && !data.getAssignedTasksId().contains(task.getTaskId())) {
									
											if (task.isCompleted()) {
												int completedTasks = data.getCompletedTasks() + 1;
												int workingDays = data.getWorkingDays() + task.getWorkingDays();
												data.setCompletedTasks(completedTasks);
												data.setWorkingDays(workingDays);
											} else {
												int workingDays = data.getWorkingDays() + task.getWorkingDays();
												data.setWorkingDays(workingDays);
											}
									
									
								}
								else {
									if (task.isCompleted()) {
										TableData newData = TableData.builder()
												.username(member.getEmail())
												.completedTasks(1)
												.workingDays(task.getWorkingDays())
												.build();
										tableData2.add(newData);
									} else {
										TableData newData = TableData.builder()
												.username(member.getEmail())
												.completedTasks(0)
												.workingDays(task.getWorkingDays())
												.build();
										tableData2.add(newData);
									}
											
								}
							}
						}
						
					}
				}
			}
		}
		
		List<TableData> tableData = new ArrayList<>();
		
		
		
		
		for (Employee employee : members) {
			int completedTasks = 0;
			int workingDays = 0;
			List<Task> assignedTasks = taskService.findTasksByEmail(employee.getEmail());
			if (assignedTasks.size() != 0) {
				for (Task task : assignedTasks) {
					if (task.isCompleted()) {
						completedTasks += 1;
					}
					
					workingDays += task.getWorkingDays();
				} 
				TableData data = TableData.builder()
						.username(employee.getEmail())
						.completedTasks(completedTasks)
						.workingDays(workingDays)
						.build();
				tableData.add(data);
			}
			
		}
		
		return new ResponseEntity<List<TableData>>(tableData, HttpStatus.OK);
		
	}
}
