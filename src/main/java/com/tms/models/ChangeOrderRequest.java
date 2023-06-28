package com.tms.models;

import java.io.Serializable;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangeOrderRequest implements Serializable{

	private static final long serialVersionUID = -939451542611995877L;
	
	private Integer projectId;
	private UUID sourceId;
	private UUID desId;
	

}
