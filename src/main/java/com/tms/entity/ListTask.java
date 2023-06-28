package com.tms.entity;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class ListTask {
	
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Type(type = "org.hibernate.type.UUIDCharType")
	@Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
	private UUID listId;
	
	private String listName;
	
	private Integer pos;
	
	

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	Set<Task> tasks = new LinkedHashSet<>();
	
	
	
	public void addTask(Task task) {
		this.tasks.add(task);
	}
	
	public void removeTask(Task task) {
		this.tasks.remove(task);
	}
}
