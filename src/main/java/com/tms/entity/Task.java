package com.tms.entity;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Task {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Type(type = "org.hibernate.type.UUIDCharType")
	@Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
	private UUID taskId;

	@NotEmpty(message = "Task title is required")
	private String taskTitle;

	private String taskDescr;
	
	@Builder.Default
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Employee> assignees = new LinkedHashSet<>();

	@CreationTimestamp
	@Column(updatable = false)
	private Timestamp createdDate;

	@UpdateTimestamp
	private Timestamp lastModifiedDate;
	
	private Timestamp dueDate;
	
	private Integer pos;
	
	private boolean isCompleted;
	private boolean isOverDue;
	private boolean isArchived;
	private TaskPriorityEnum taskPriority; 

//	@Version
//	private long version;

	public void addAssigneeToTask(Employee e) {
		this.assignees.add(e);
	}
	
	public void removeAssigneeToTask(Employee e) {
		this.assignees.remove(e);
	}
}
