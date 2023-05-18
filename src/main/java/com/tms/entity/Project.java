package com.tms.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Project extends BaseEntity {
	
	@NotEmpty(message = "Project must have a name")
	private String projectName;
	
	@Column(updatable = false)
	@NotEmpty(message = "Need to provided creator email")
	private String createdBy;
	
	@Builder
	public Project(int id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String projectName) {
		super(id, version, createdDate, lastModifiedDate);
		this.projectName = projectName;
	}
	
	

}
