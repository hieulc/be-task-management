package com.tms.dtos.mappers;

import org.mapstruct.Mapper;

import com.tms.dtos.EmployeeDto;
import com.tms.entity.Employee;

@Mapper(uses = {DateMapper.class, ProjectMapper.class}, componentModel = "spring")
public interface EmployeeMapper {
	
	EmployeeDto employeeToEmployeeDto(Employee employee);
	
	Employee employeeDtoToEmployee(EmployeeDto employeeDto);
	
}
