package com.tms.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.models.EmployeePagedList;
import com.tms.entity.Employee;
import com.tms.entity.Role;
import com.tms.entity.RoleNameEnum;
import com.tms.repositories.EmployeeRepository;
import com.tms.repositories.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService, UserDetailsService {

	private final EmployeeRepository employeeRepository;
	private final RoleRepository roleRepository;

	public EmployeePagedList getEmployees(Pageable pageable) {

		Page<Employee> page = employeeRepository.findAll(pageable);

		PageRequest pageRequest = getPageRequestFrom(page);
		List<Employee> employeeList = getEmployeeListFrom(page);
		long totalElements = getTotalElementsFrom(page);

		return new EmployeePagedList(employeeList, pageRequest, totalElements);
	}

	private PageRequest getPageRequestFrom(Page<Employee> page) {
		Pageable pageable = page.getPageable();
		return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
	}

	private List<Employee> getEmployeeListFrom(Page<Employee> page) {
		return page.getContent().stream().collect(Collectors.toList());
	}

	private long getTotalElementsFrom(Page<Employee> page) {
		return page.getTotalElements();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Employee employee = employeeRepository.findByEmail(username);

		if (employee == null) {
			log.error("User not found in database");
			throw new UsernameNotFoundException("User not found in database");
		} else {
			log.error("User found in database " + username);
		}

		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		employee.getEmpRoles()
				.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getRoleName().toString())));

		return new User(employee.getEmail(), employee.getPassword(), authorities);
	}

	@Override
	@Transactional
	public Employee saveEmployee(Employee employee) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		List<RoleNameEnum> userRoles = populateRoleList(employee.getEmpRoles());

		Set<Role> roleForUser = new HashSet<>();

		if (userRoles.size() == 0) {
			roleForUser = addDefaultRoleToSet(userRoles);
		} else {
			roleForUser = addRolesToSet(userRoles);
		}

		employee.setEmpRoles(roleForUser);
		employee.setPassword(encoder.encode(employee.getPassword()));
		return employeeRepository.save(employee);

	}

	private List<RoleNameEnum> populateRoleList(Set<Role> employeeRoles) {
		List<RoleNameEnum> userRoles = new ArrayList<>();
		for (Role employeeRole : employeeRoles) {
			userRoles.add(employeeRole.getRoleName());
		}

		return userRoles;
	}

	private Set<Role> addDefaultRoleToSet(List<RoleNameEnum> userRoles) {
		Role defaultRole = roleRepository.findByRoleName(RoleNameEnum.USER);
		Set<Role> setOfDefaultRole = new HashSet<>();
		setOfDefaultRole.add(defaultRole);
		return setOfDefaultRole;
	}

	private Set<Role> addRolesToSet(List<RoleNameEnum> userRoles) {
		Set<Role> setOfRoles = new HashSet<>();
		for (RoleNameEnum roleNameEnum : userRoles) {
			Role userRole = roleRepository.findByRoleName(roleNameEnum);
			setOfRoles.add(userRole);
		}

		return setOfRoles;
	}

	@Override
	@Transactional
	public Role save(Role userRole) {
		return roleRepository.save(userRole);
	}

	@Override
	public void addRoleToEmployee(String email, RoleNameEnum roleName) {
		Employee employee = employeeRepository.findByEmail(email);
		Role userRole = roleRepository.findByRoleName(roleName);

		Set<Role> employeeRoles = employee.getEmpRoles();
		if (employeeRoles.contains(userRole)) {
			return;
		}
		employeeRoles.add(userRole);
		employee.setEmpRoles(employeeRoles);
		employeeRepository.save(employee);
	}

	@Override
	public Employee getEmployeeBy(String email) {
		return employeeRepository.findByEmail(email);
	}

	@Override
	public Optional<Employee> getEmployeeById(int id) {
		return employeeRepository.findById(id);
	}

	@Override
	public Employee updateEmployee(Employee updatedEmployee) {
		return employeeRepository.save(updatedEmployee);
	}

	@Override
	public Integer existEmail(String email) {
		return employeeRepository.existEmail(email);
	}

}
