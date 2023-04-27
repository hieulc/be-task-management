package com.tms.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.utils.ErrorUtil;
import com.tms.utils.JwtTokenUtil;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAuthorizationFilter extends OncePerRequestFilter {
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		log.debug(request.getServletPath().toString());
		
		if (isAuthenRequest(request)) {
			filterChain.doFilter(request, response);
		} else if (isValidAuthenMethod(request)) {
			sendInvalidMethodResponse(request, response);
			return;
		} else {
			String authorizationHeader = getAuthorizationHeader(request);
			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
				try {
					String token = subtractTokenFrom(authorizationHeader);
					DecodedJWT decodedJWT = JwtTokenUtil.decode(token);
					String username = JwtTokenUtil.getSubject(decodedJWT);
					String[] roles = JwtTokenUtil.getClaim(decodedJWT);
					
					System.out.print(roles);
					
					setNewAuthentication(username, roles);
					filterChain.doFilter(request, response);
				} catch (Exception e) {
					ErrorUtil.responseErrorMessage(response, e);
				}
			} else {
				filterChain.doFilter(request, response);
			}
		}
		
	}
	
	private boolean isAuthenRequest(HttpServletRequest request) {
		 if (request.getServletPath().equals("/login") || request.getServletPath().equals("/employees/refresh_token")) {
			return true;
	}
		return false;
	}
		 
	private boolean isValidAuthenMethod(HttpServletRequest request) {
		if (request.getServletPath().equals("/login") && request.getMethod() == "POST") {
			return true;
		}
		return false;
	}
	
	private void sendInvalidMethodResponse(HttpServletRequest request, HttpServletResponse response) throws StreamWriteException, DatabindException, IOException  {
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		Map<String, String> errors = new HashMap<>();
		errors.put("error_message", "Can not use " + request.getMethod() + " method for login. Only POST is allowed");
		new ObjectMapper().writeValue(response.getOutputStream(), errors);
	}
	
	private String getAuthorizationHeader(HttpServletRequest request) {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		return authorizationHeader;
	}
	
	private String subtractTokenFrom(String authorizationHeader) {
		return authorizationHeader.substring("Bearer ".length());
	}
	
	private Collection<SimpleGrantedAuthority> getAuthorities(String[] roles) {
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		Arrays.stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
		return authorities;
	}
	
	private UsernamePasswordAuthenticationToken generateAuthentication(String username, String[] roles) {
		return new UsernamePasswordAuthenticationToken(username, null, getAuthorities(roles));
	}
	
	private void setNewAuthentication(String username, String[] roles) {
		SecurityContextHolder.getContext().setAuthentication(generateAuthentication(username, roles));
	}
	
	

}
