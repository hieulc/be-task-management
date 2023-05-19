package com.tms.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.tms.entity.Employee;
import com.tms.entity.Project;
import com.tms.entity.Role;
import com.tms.entity.RoleNameEnum;
import com.tms.models.EmployeePagedList;

public interface EmployeeService {
	
	public EmployeePagedList getEmployees(Pageable pageable);
	public Employee saveEmployee(Employee employee);
	public Role save(Role userRole);
	public void addRoleToEmployee(String email, RoleNameEnum roleName);
	public Employee getEmployeeBy(String email);
	public Employee updateEmployee(Employee updatedEmployee);
	public Integer existEmail(String email);
	
	Optional<Employee> getEmployeeById(int id);
	
	
}
