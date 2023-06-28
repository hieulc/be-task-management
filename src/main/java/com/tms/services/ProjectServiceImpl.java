package com.tms.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.entity.Employee;
import com.tms.entity.ListTask;
import com.tms.entity.Project;
import com.tms.entity.Task;
import com.tms.models.ProjectPagedList;
import com.tms.repositories.EmployeeRepository;
import com.tms.repositories.ListTaskRepository;
import com.tms.repositories.ProjectRepository;
import com.tms.repositories.TaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

	private final ProjectRepository projectRepository;
	private final EmployeeRepository employeeRepository;
	private final ListTaskRepository listTaskRepository;
	private final TaskRepository taskRepository;

	@Override
	public ProjectPagedList getAllProject(String username, Pageable pageable) {
		Employee employee = employeeRepository.findByEmail(username);
		if (employee != null) {
			Page<Project> page = projectRepository.findAllByCreatedBy(username, pageable);

			PageRequest pageRequest = getPageRequestFrom(page);
			List<Project> projectList = getProjectListFrom(page);
			long totalElements = getTotalElementsFrom(page);

			return new ProjectPagedList(projectList, pageRequest, totalElements);
		}
		return null;
		
	}

	private PageRequest getPageRequestFrom(Page<Project> page) {
		Pageable pageable = page.getPageable();
		return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
	}

	private List<Project> getProjectListFrom(Page<Project> page) {
		return page.getContent().stream().collect(Collectors.toList());
	}

	private long getTotalElementsFrom(Page<Project> page) {
		return page.getTotalElements();
	}

	@Override
	public Optional<Project> findProjectByProjectName(String projectName) {
		return projectRepository.findByProjectName(projectName);
	}

	@Override
	@Transactional
	public Project saveProject(Project project) {
		Employee employee = employeeRepository.findByEmail(project.getCreatedBy());
		project.addMember(employee);
//		employeeRepository.save(employee);
		return projectRepository.save(project);
	}

	@Override
	public Project updateProject(Project updatedProject) {
		return projectRepository.save(updatedProject);
	}

	@Override
	public void deleteProject(int id) {
		Optional<Project> projectOptional = projectRepository.findById(id);
		
		projectRepository.deleteById(id);
	}
	
	
	

	@Override
	public Optional<Project> findById(int id) {
		return projectRepository.findById(id);
	}

	@Override
	public Integer findUniqueProject(String createdBy, String projectName) {
		return projectRepository.findUniqueProject(createdBy, projectName);
	}

	@Override
	public Project addMemberToProject(String username, Project project) {
		Employee employee = employeeRepository.findByEmail(username);
		project.addMember(employee);
		
		employeeRepository.save(employee);
		return projectRepository.save(project);
		
	}
	
	@Override
	@Transactional
	public Project removeMemberFromProject(String username, Integer projectId) {
		Optional<Project> projectOptional = projectRepository.findById(projectId);
		Employee employee = employeeRepository.findByEmail(username);
		if (projectOptional.isEmpty()) {
			return null;
		} 
		List<Task> assignedTasks = taskRepository.findByAssignees_Email(username);
		
		Project updatedProject = projectOptional.get();
		for (Task task : assignedTasks) {
			if (!updatedProject.getCreatedBy().equals(employee.getEmail())) {
				task.removeAssigneeToTask(employee);
				taskRepository.save(task);
			}
		}
		updatedProject.removeMember(employee);
		projectRepository.save(updatedProject);
		return updatedProject;
		
	}

//	@Override
//	public List<Project> findByMembers_Email(String email) {
//		return projectRepository.findByMembers_Email(email);
//	}

	@Override
	public ProjectPagedList findAllProjectsByEmail(String email, Pageable pageable) {
		Page<Project> page = projectRepository.findByMembers_Email(email, pageable);

		PageRequest pageRequest = getPageRequestFrom(page);
		List<Project> projectList = getProjectListFrom(page);
		long totalElements = getTotalElementsFrom(page);

		return new ProjectPagedList(projectList, pageRequest, totalElements);
	}

	@Override
	@Transactional
	public Project addListTaskToProject(String listName, Project project, Integer pos) {
		ListTask tasks = ListTask.builder().listName(listName).build();
		tasks.setPos(pos);
		
		Optional<Project> optional = projectRepository.findById(project.getProjectId());
		if (optional.isEmpty()) {
			return null;
		} else {
			Project updatedProject = optional.get();
			updatedProject.addListTask(tasks);
			listTaskRepository.save(tasks);
			projectRepository.save(updatedProject);
			return updatedProject;
		}	
	}

	@Override
	public Project findProjectByListId(UUID listId) {
		Optional<Project> optional = projectRepository.findByListOfTasks_ListId(listId);
		if (optional.isEmpty()) {
			return null;
		}
		return optional.get();
	}

	@Override
	public Project changeColumnOrder(Integer projectId, UUID sourceId, UUID desId) {
		
		ListTask sourceCol = listTaskRepository.findById(sourceId).get();
		ListTask desCol = listTaskRepository.findById(desId).get();
		
		int tmp = sourceCol.getPos();
		sourceCol.setPos(desCol.getPos());
		desCol.setPos(tmp);
		
		listTaskRepository.save(sourceCol);
		listTaskRepository.save(desCol);
		
		Optional<Project> optional = projectRepository.findById(projectId);
		
		if (optional.isEmpty()) return null;
		
		
		return optional.get();
	}

	@Override
	public ProjectPagedList getProjectsByCreatedBy(String username, Pageable pageable) {
		Page<Project> page = projectRepository.findAllByCreatedBy(username, pageable);

		PageRequest pageRequest = getPageRequestFrom(page);
		List<Project> projectList = getProjectListFrom(page);
		long totalElements = getTotalElementsFrom(page);

		return new ProjectPagedList(projectList, pageRequest, totalElements);

	}
	
	
	
	

}
