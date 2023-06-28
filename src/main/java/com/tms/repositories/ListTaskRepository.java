package com.tms.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.entity.ListTask;

public interface ListTaskRepository extends JpaRepository<ListTask, UUID>{

}
