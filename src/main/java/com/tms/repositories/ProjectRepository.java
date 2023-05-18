package com.tms.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tms.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Integer>{
	
	Page<Project> findAllByCreatedBy(String username, Pageable pageable);

	Optional<Project> findByProjectName(String projectName);
	
	
	@Query("SELECT 1 FROM Project p WHERE p.createdBy=?1 AND p.projectName=?2")
	Integer findUniqueProject(String createdBy, String projectName);
}
