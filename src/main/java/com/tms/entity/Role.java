package com.tms.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Role extends BaseEntity{
	
	@Enumerated(EnumType.STRING)
	private RoleNameEnum roleName;

	@Builder
	public Role(int id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, RoleNameEnum roleName) {
		super(id, version, createdDate, lastModifiedDate);
		this.roleName = roleName;
	}
	
	
	
	
}
