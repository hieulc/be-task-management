package com.tms.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorUtil {

	public static void responseErrorMessage(HttpServletResponse response, Exception e) throws StreamWriteException, DatabindException, IOException {
		log.error("Error logging in: {}", e.getMessage());
		response.setStatus(HttpStatus.FORBIDDEN.value());
		Map<String, String> errors = new HashMap<>();
		errors.put("error_message", e.getMessage());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), errors);
	}
}
