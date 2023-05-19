package com.tms.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonIdentityInfo(scope = Project.class,
generator = ObjectIdGenerators.PropertyGenerator.class,
property = "projectId")
public class Project implements Serializable {

	static final long serialVersionUID = -449175192950680293L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int projectId;

	@Version
	private Long version;

	@CreationTimestamp
	@Column(updatable = false)
	private Timestamp createdDate;

	@UpdateTimestamp
	private Timestamp lastModifiedDate;

	@NotEmpty(message = "Project must have a name")
	private String projectName;

	@Column(updatable = false)
	@NotEmpty(message = "Need to provided creator email")
	private String createdBy;

	@Builder.Default
	@ManyToMany(mappedBy = "joinedProjects")
	Set<Employee> members = new HashSet<>();

	public void removeMember(Employee e) {
		this.members.remove(e);
		e.getJoinedProjects().remove(this);
	}

	public void addMember(Employee e) {
		this.members.add(e);
		e.getJoinedProjects().add(this);
	}

}
