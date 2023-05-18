package com.tms.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.tms.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer>{

	Employee findByEmail(String email);	
	
	@Query("SELECT 1 FROM Employee e WHERE e.email = ?1")
	Integer existEmail(String email);
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO Employee(email, password, first_name, last_name) VALUES (?1, ?2, ?3, ?4)", nativeQuery = true)
	int saveUser(String username, String password, String firstName, String lastName);

}