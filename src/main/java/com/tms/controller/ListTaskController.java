package com.tms.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.entity.ListTask;
import com.tms.services.ListTaskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("projects/listTasks")
@RequiredArgsConstructor
public class ListTaskController {
	private final ListTaskService listTaskService;
 	
	@GetMapping("/{listId}")
	public ResponseEntity<ListTask> findListTaskById(@PathVariable("listId") UUID listId) {
		Optional<ListTask> optional = listTaskService.findListTaskById(listId);
		if (optional.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		else {
			ListTask listTask = optional.get();
			return new ResponseEntity<ListTask>(listTask, HttpStatus.OK);
		}
	}
	
	
}
