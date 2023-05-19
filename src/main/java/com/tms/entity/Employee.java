package com.tms.entity;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(
		scope = Employee.class,
		generator = ObjectIdGenerators.PropertyGenerator.class, 
		property = "id")
public class Employee extends BaseEntity {

	@Builder
	public Employee(int id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String email,
			String firstName, String lastName, String password, Set<Role> empRoles) {
		super(id, version, createdDate, lastModifiedDate);
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.empRoles = empRoles;
	}

	@Column(unique = true)
	@Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
	@NotEmpty(message = "Email can not be empty")
	private String email;

	@NotEmpty(message = "FirstName can not be empty")
	private String firstName;

	@NotEmpty(message = "LastName can not be empty")
	private String lastName;

	@Column(nullable = false, length = 60, columnDefinition = "varchar(60)")
	private String password;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Role> empRoles = new HashSet<>();
	
	@ManyToMany
	@JoinTable(	        
	        joinColumns = {
	            @JoinColumn(name = "id")
	        },
	        inverseJoinColumns = {
	            @JoinColumn(name = "project_id")
	        }
	    )
	private Set<Project> joinedProjects = new HashSet<>();
	
	public void removeProject(Project p) {
		this.joinedProjects.remove(p);
		p.getMembers().remove(this);
	}

}
