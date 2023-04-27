package com.tms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.entity.Role;
import com.tms.entity.RoleNameEnum;

public interface RoleRepository extends JpaRepository<Role, Long>{

	Role findByRoleName(RoleNameEnum roleName);
}
