package com.tms.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(	        
	        joinColumns = {
	            @JoinColumn(name = "project_id")
	        },
	        inverseJoinColumns = {
	            @JoinColumn(name = "id")
	        }
	    )
	Set<Employee> members = new HashSet<>();
	
	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	Set<ListTask> listOfTasks = new HashSet<>();

	public void removeMember(Employee e) {
		this.members.remove(e);
	}

	public void addMember(Employee e) {
		this.members.add(e);
	}
	
	public void addListTask(ListTask list) {
		this.listOfTasks.add(list);
	}
	
	public void removeListTask(ListTask list) {
		this.listOfTasks.remove(list);
	}

}
