package com.tms.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.invocation.Invocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.tms.controller.EmployeeController;
import com.tms.entity.Employee;
import com.tms.entity.Role;
import com.tms.entity.RoleNameEnum;
import com.tms.models.EmployeePagedList;
import com.tms.services.EmployeeService;
import com.tms.utils.JwtTokenUtil;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.BDDMockito.*;




@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {
	
	@MockBean
	UserDetailsService userDetailsService;

	@MockBean
	EmployeeService employeeService;
	
	@MockBean
	PasswordEncoder passwordEncoder;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;
	

	@Mock
	EmployeePagedList employeePagedList;
	
	
	Pageable pageable;
	Employee employee;
	Employee invalidEmployee;
	Employee updatedEmployee;
	List<Employee> employeeList;
	String accessToken;
	
	@BeforeEach
	void setup() {
		employee = Employee.builder()
				.email("conghoa.le@dxc.com")
				.firstName("cong hoa")
				.lastName("le")
				.password(passwordEncoder.encode("password"))
				.empRoles(Set.of(Role.builder().roleName(RoleNameEnum.USER).build()))
				.build();
		
		invalidEmployee = Employee.builder()
				.email("conghoa.ledxc.com")
				.firstName("cong hoa")
				.lastName("le")
				.password(passwordEncoder.encode("password"))
				.empRoles(Set.of(Role.builder().roleName(RoleNameEnum.USER).build()))
				.build();
		
		updatedEmployee = Employee.builder()
				.email("conghoa.le1@dxc.com")
				.firstName("conghoa")
				.lastName("le")
				.password(passwordEncoder.encode("password"))
				.empRoles(Set.of(Role.builder().roleName(RoleNameEnum.USER).build()))
				.build();
		
		employeeList = new ArrayList<>();
		employeeList.add(employee);
		
		pageable = PageRequest.of(0, 10);
		
		employeePagedList = new EmployeePagedList(employeeList, pageable, 10);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.setRequestURI("/task-management/api/v1/login");
		
		List<String> authorities = new ArrayList<>();
		authorities.add("USER");
		
		accessToken = JwtTokenUtil.generateAccessToken(mockRequest, "hle223", authorities);
		
	}
	
	
	@Test
	void givenEmployeePagedList_whenGetEmployees_returnEmployeePagedList() throws Exception {
		
		
		given(employeeService.getEmployees(pageable)).willReturn(employeePagedList);
		
		
		mockMvc.perform(get("/employees"))
		.andExpect(status().isOk())
		.andDo(print())
		.andExpect(jsonPath("$.totalPages", is(employeePagedList.getTotalPages())))
		.andExpect(jsonPath("$.content", hasSize(1)))
		.andExpect(jsonPath("$.content[0].email", is(employee.getEmail())));
				
	}
	
	@Test
	void givenEmployeeOjbect_whenSaveEmployee_returnEmployeeObject() throws Exception {
		String employeeJson = objectMapper.writeValueAsString(employee);
		
		given(employeeService.saveEmployee(any())).willReturn(employee);
		
		
		mockMvc.perform(post("/employees/save")
				.header("AUTHORIZATION", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(employeeJson))
		.andDo(print())
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
		.andExpect(jsonPath("$.lastName", is(employee.getLastName())));
	}
	
	@Test
	void givenInvalidEmployee_whenSaveEmployee_returnBadRequest() throws Exception {
		String invalidEmplJson = objectMapper.writeValueAsString(invalidEmployee);
		
		given(employeeService.saveEmployee(any())).willReturn(invalidEmployee);
		
		mockMvc.perform(post("/employees/save")
				.header("AUTHORIZATION", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidEmplJson))
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.[0].defaultMessage", is("Email is not valid")));
	}
	
	@Test
	void givenEmployeeEmail_whenFindByEmail_returnEmployeeObject() throws Exception {
		
		given(employeeService.getEmployeeBy(any())).willReturn(employee);
		
		mockMvc.perform(get("/employees/{email}", employee.getEmail())
				.header("AUTHORIZATION", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andDo(print())
		.andExpect(jsonPath("$.email", is(employee.getEmail())))
		.andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
		.andExpect(jsonPath("$.lastName", is(employee.getLastName())));
		
	}
	
	@Test
	void givenInvalidEmail_whenFindByEmail_returnEmpty() throws Exception {
		
		String invalidEmail = "hle223";
		
		given(employeeService.getEmployeeBy(any())).willReturn(null);
		
		mockMvc.perform(get("/employees/{email}", invalidEmail)
				.header("AUTHORIZATION", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andDo(print());
		
	}
	
	@Test
	void givenUpdatedEmployee_whenUpdateEmployee_returnUpdatedEmployeeObject() throws Exception{
		int employeeId = 1;
		
		Employee originalEmployee = employee;
		String updatedEmployeeJson = objectMapper.writeValueAsString(updatedEmployee);
		
		given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.of(originalEmployee));
		given(employeeService.updateEmployee(any())).willAnswer(invocation -> invocation.getArgument(0));
		
		mockMvc.perform(put("/employees/{id}", employeeId)
				.header("AUTHORIZATION", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updatedEmployeeJson))
		.andExpect(status().isNoContent())
		.andDo(print())
		.andExpect(jsonPath("$.email", is(updatedEmployee.getEmail())))
		.andExpect(jsonPath("$.firstName", is(updatedEmployee.getFirstName())))
		.andExpect(jsonPath("$.lastName", is(updatedEmployee.getLastName())));
		
	}
	
	
	

}
