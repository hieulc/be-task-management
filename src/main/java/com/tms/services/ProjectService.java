package com.tms.services;

import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.tms.entity.Project;
import com.tms.models.ProjectPagedList;

public interface ProjectService {
	ProjectPagedList getAllProject(String username, Pageable pageable);
	Optional<Project> findById(int id);
	Optional<Project> findProjectByProjectName(String projectName);
	Project saveProject(Project project);
	Project updateProject(Project updatedProject);
	Integer findUniqueProject(String createdBy, String projectName);
	void deleteProject(int id);
}
