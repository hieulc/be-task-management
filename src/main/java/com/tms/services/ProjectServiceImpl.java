package com.tms.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tms.entity.Employee;
import com.tms.entity.Project;
import com.tms.models.ProjectPagedList;
import com.tms.repositories.EmployeeRepository;
import com.tms.repositories.ProjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

	private final ProjectRepository projectRepository;
	private final EmployeeRepository employeeRepository;

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
		log.debug("Project name: " + projectName);
		return projectRepository.findByProjectName(projectName);
	}

	@Override
	public Project saveProject(Project project) {
		return projectRepository.save(project);
	}

	@Override
	public Project updateProject(Project updatedProject) {
		return projectRepository.save(updatedProject);
	}

	@Override
	public void deleteProject(int id) {
		projectRepository.deleteById(id);
	}

	@Override
	public Optional<Project> findById(int id) {
		log.debug("ID: " + id);
		return projectRepository.findById(id);
	}

	@Override
	public Integer findUniqueProject(String createdBy, String projectName) {
		return projectRepository.findUniqueProject(createdBy, projectName);
	}

}
