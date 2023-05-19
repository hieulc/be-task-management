package com.tms.services;

import java.util.List;
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
	Project addMemberToProject(String username, Project project);
//	List<Project> findByMembers_Email(String email);
	ProjectPagedList findAllProjectsByEmail(String email, Pageable pageable);
	void deleteProject(int id);
}
