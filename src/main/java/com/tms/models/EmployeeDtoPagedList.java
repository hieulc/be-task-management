package com.tms.models;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.tms.dtos.EmployeeDto;


public class EmployeeDtoPagedList extends PageImpl<EmployeeDto> implements Serializable {

	
	private static final long serialVersionUID = 5627728751525441257L;
	
	public EmployeeDtoPagedList(List<EmployeeDto> content, Pageable pageable, long total) {
		super(content, pageable, total);
	}
	
	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public EmployeeDtoPagedList(@JsonProperty("content") List<EmployeeDto> content,
			@JsonProperty("totalElements") long totalElements,
			@JsonProperty("number") int number,
			@JsonProperty("size") int size,
			@JsonProperty("sort") JsonNode sort,
			@JsonProperty("pageable") JsonNode pageable,
			@JsonProperty("last") boolean last,
			@JsonProperty("first") boolean first,
			@JsonProperty("numberOfElements") int numberOfElements) {
		super(content, PageRequest.of(numberOfElements, size), totalElements);
	}
	

}
