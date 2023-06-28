package com.tms.controller;

import java.io.IOException;
import java.net.URI;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.models.AuthenticationResponse;
import com.tms.models.EmployeeDtoPagedList;
import com.tms.models.EmployeePagedList;
import com.tms.entity.Employee;
import com.tms.entity.Project;
import com.tms.entity.Role;
import com.tms.filters.UserAuthenticationFilter;
import com.tms.services.EmployeeService;
import com.tms.utils.ErrorUtil;
import com.tms.utils.JwtTokenUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

	private final EmployeeService employeeService;
	private static final int DEFAULT_PAGE_NUMBER = 0;
	private static final int DEFAULT_PAGE_SIZE = 10;

	@GetMapping
	public EmployeePagedList getEmployees(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {

		if (pageNumber == null || pageNumber < 0) {
			pageNumber = DEFAULT_PAGE_NUMBER;
		}

		if (pageSize == null || pageSize < 0) {
			pageSize = DEFAULT_PAGE_SIZE;
		}

		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		return employeeService.getEmployees(pageable);
	}

//	@GetMapping("/dto")
//	public EmployeeDtoPagedList getEmployeesDto(
//			@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
//			@RequestParam(value = "pageSize", required = false) Integer pageSize) {
//		if (pageNumber == null || pageNumber < 0) {
//			pageNumber = DEFAULT_PAGE_NUMBER;
//		}
//
//		if (pageSize == null || pageSize < 0) {
//			pageSize = DEFAULT_PAGE_SIZE;
//		}
//
//		Pageable pageable = PageRequest.of(pageNumber, pageSize);
//
//		return employeeService.getEmployeesDto(pageable);
//	}

	@PostMapping(value = "/save", produces = "application/json; charset=utf-8")
	@Operation(summary = "Save employee", description = "Save Employee")
	@SecurityRequirement(name = "Bearer Authentication")
	public ResponseEntity<Employee> saveEmployee(@RequestBody @Validated Employee employee) {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/save").toUriString());
		return ResponseEntity.created(uri).body(employeeService.saveEmployee(employee));
	}

	@PostMapping("/role/save")
	public ResponseEntity<Role> saveUserRole(@RequestBody Role userRole) {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/role/save").toUriString());
		return ResponseEntity.created(uri).body(employeeService.save(userRole));
	}

	@GetMapping("/refresh_token")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			try {
				String refreshToken = authorizationHeader.substring("Bearer ".length());

				DecodedJWT decodedJWT = JwtTokenUtil.decode(refreshToken);

				String username = decodedJWT.getSubject();

				Employee tokenSubject = findTokenOwner(username);
				List<String> authorities = getAuthorities(tokenSubject);

				String accessToken = JwtTokenUtil.generateAccessToken(request, username, authorities);
				String refresh_token = JwtTokenUtil.generateRefreshToken(request, username);

				AuthenticationResponse authenResponse = new AuthenticationResponse(username, accessToken, refresh_token,
						authorities);

				sendAuthenticationResponse(response, authenResponse);

			} catch (Exception e) {
				ErrorUtil.responseErrorMessage(response, e);
			}
		} else {
			throw new RuntimeException("Refresh Token is missing");
		}
	}

//	@GetMapping(value = "/{email}", produces = "application/json; charset=utf-8")
//	public ResponseEntity<Employee> findByEmail(@PathVariable("email") String email) {
//		Employee employee = employeeService.getEmployeeBy(email);
//		if (employee == null) {
//			return new ResponseEntity<Employee>(HttpStatus.NOT_FOUND);
//		}
//		return new ResponseEntity<Employee>(employee, HttpStatus.OK);
//	}

	@GetMapping(value = "/exist/{email}", produces = "application/json; charset=utf-8")
	public Integer findExistEmail(@PathVariable("email") String email) {
		log.info(email);
		return employeeService.existEmail(email);
	}

	private Employee findTokenOwner(String username) {
		return employeeService.getEmployeeBy(username);
	}

	private List<String> getAuthorities(Employee tokenSubject) {
		return tokenSubject.getEmpRoles().stream().map(role -> role.getRoleName().toString())
				.collect(Collectors.toList());
	}

	private void sendAuthenticationResponse(HttpServletResponse response, AuthenticationResponse authResponse)
			throws StreamWriteException, DatabindException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getOutputStream(), authResponse);
	}

	@PutMapping(value = "/{id}", produces = "application/json; charset=utf-8")
	@Operation(summary = "Update User", description = "Update User")
	@SecurityRequirement(name = "Bearer Authentication")
	public ResponseEntity<Employee> updateEmployee(@PathVariable("id") int id,
			@RequestBody @Validated Employee employee) {
		return employeeService.getEmployeeById(id).map(savedEmployee -> {
			savedEmployee.setEmail(employee.getEmail());
			savedEmployee.setFirstName(employee.getFirstName());
			savedEmployee.setLastName(employee.getLastName());

			Employee updatedEmployee = employeeService.updateEmployee(savedEmployee);
			return new ResponseEntity<>(updatedEmployee, HttpStatus.NO_CONTENT);
		}).orElseGet(() -> ResponseEntity.notFound().build());

	}

}
