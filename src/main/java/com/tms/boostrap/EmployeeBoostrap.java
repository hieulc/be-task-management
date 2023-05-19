package com.tms.boostrap;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tms.entity.Employee;
import com.tms.entity.Project;
import com.tms.entity.Role;
import com.tms.entity.RoleNameEnum;
import com.tms.repositories.EmployeeRepository;
import com.tms.repositories.ProjectRepository;
import com.tms.repositories.RoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmployeeBoostrap implements CommandLineRunner {
	
	private final EmployeeRepository employeeRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final ProjectRepository projectRepository;
	
	

	@Override
	public void run(String... args) throws Exception {
		loadEmployeeData();
	}
	
	@Transactional
	private void loadEmployeeData() {
		
		Role userRole = Role.builder().roleName(RoleNameEnum.USER).build();
		Role adminRole = Role.builder().roleName(RoleNameEnum.ADMIN).build();
		
		roleRepository.save(userRole);
		roleRepository.save(adminRole);
		
		if (employeeRepository.count() == 0) {
			Employee employee1 = Employee.builder()
					.email("hieucong.le@dxc.com")
					.firstName("hieu")
					.lastName("le")
					.password(passwordEncoder.encode("password"))
					.build();
			Employee employee2 = Employee.builder()
					.email("haminh.hoang@dxc.com")
					.firstName("hoang")
					.lastName("ha")
					.password(passwordEncoder.encode("password"))
					.build();
			Employee employee3 = Employee.builder()
					.email("vantien.nguyen@dxc.com")
					.firstName("tien")
					.lastName("nguyen")
					.password(passwordEncoder.encode("password"))
					.build();
			
			Set<Role> roleForUser = new HashSet<>();
			roleForUser.add(userRole);
			
			employee1.setEmpRoles(roleForUser);
			employee2.setEmpRoles(roleForUser);
			employee3.setEmpRoles(roleForUser);
			
//			employeeRepository.save(employee1);
//			
//			Project project = Project.builder().createdBy(employee1.getEmail()).projectName("FUN").build();
//			projectRepository.save(project);
//			
//			
//			Set<Project> joinedProjects = new HashSet<>();
//			joinedProjects.add(project);
			
//			employee1.setJoinedProjects(joinedProjects);
			employeeRepository.save(employee1);
			employeeRepository.save(employee2);
			employeeRepository.save(employee3);
			
		}
	}

}
