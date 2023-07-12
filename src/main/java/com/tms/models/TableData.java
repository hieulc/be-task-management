package com.tms.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.tms.entity.Task;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TableData implements Serializable{
	
	

	private static final long serialVersionUID = -7032152497450784178L;
	private String username;
	private int completedTasks;
	private int workingDays;
	
	@Builder.Default
	private List<UUID> assignedTasksId = new ArrayList<>();
	 

}
