package com.tms.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.tms.entity.Project;
import com.tms.models.ProjectPagedList;

public interface ProjectService {
	ProjectPagedList getAllProject(String username, Pageable pageable);
	ProjectPagedList getProjectsByCreatedBy(String username, Pageable pageable);
	Optional<Project> findById(int id);
	Optional<Project> findProjectByProjectName(String projectName);
	Project saveProject(Project project);
	Project updateProject(Project updatedProject);
	Integer findUniqueProject(String createdBy, String projectName);
	Project addMemberToProject(String username, Project project);
	Project removeMemberFromProject(String username, Integer projectId);
	Project findProjectByListId(UUID listId);
//	List<Project> findByMembers_Email(String email);
	ProjectPagedList findAllProjectsByEmail(String email, Pageable pageable);
	Project addListTaskToProject(String listName, Project project, Integer pos);
	Project changeColumnOrder(Integer projectId, UUID sourceId, UUID desId);
	void deleteProject(int id);
}
