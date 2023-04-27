package com.tms.models;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationResponse implements Serializable{	
	
	static final long serialVersionUID = -44917519950680294L;
	private static final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	public String username;
	public String access_token;
	public String refresh_token;
	public String createDate;
	public List<String> authorities;	
	
	public AuthenticationResponse(
			String username,
			String access_token,
			String refresh_token,
			List<String> authorities
			) {
		super();
		this.username = username;
		this.access_token = access_token;
		this.refresh_token = refresh_token;
		this.authorities = authorities;
		this.createDate = formatDate.format(new Timestamp(System.currentTimeMillis()));
	}

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public AuthenticationResponse(
			@JsonProperty String username,
			@JsonProperty String access_token,
			@JsonProperty String refresh_token,
			@JsonProperty String createDate,
			@JsonProperty("role") List<String> authorities) {
		this.username = username;
		this.access_token = access_token;
		this.refresh_token = refresh_token;
		this.createDate = createDate;
		this.authorities = authorities;
	}
	
}
