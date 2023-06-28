package com.tms.dtos.mappers;

import org.mapstruct.Mapper;

import com.tms.dtos.ProjectDto;
import com.tms.entity.Project;

@Mapper(uses = {DateMapper.class, EmployeeMapper.class})
public interface ProjectMapper {
	
	ProjectDto projectToProjectDto(Project project);
	Project projectDtoToProject(ProjectDto projectDto);
	

}
