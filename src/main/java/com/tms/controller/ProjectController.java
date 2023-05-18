package com.tms.controller;

import java.net.URI;
import java.util.Optional;

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

import com.tms.entity.Project;
import com.tms.models.ProjectPagedList;
import com.tms.services.ProjectService;

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
}
