package com.tms.models;

import java.io.Serializable;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TaskRequestPayload implements Serializable{

	
	private static final long serialVersionUID = -6152959401076409958L;
	private UUID assignedListTaskId;
	private UUID unassignedListTaskId;
	private UUID movedTaskId;
	private UUID desTaskId;
	private int dropPlace;

}
