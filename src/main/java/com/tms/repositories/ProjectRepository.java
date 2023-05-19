package com.tms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tms.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Integer>{
	
	Page<Project> findAllByCreatedBy(String username, Pageable pageable);
	
//	@Query("select p from Project p join Employee e where e.email = :email")
//	List<Project> findByMembers_Email(@Param("email") String email);
	
	Page<Project> findByMembers_Email(String email, Pageable pageable);

	Optional<Project> findByProjectName(String projectName);
	
	
	@Query("SELECT 1 FROM Project p WHERE p.createdBy=?1 AND p.projectName=?2")
	Integer findUniqueProject(String createdBy, String projectName);
}
