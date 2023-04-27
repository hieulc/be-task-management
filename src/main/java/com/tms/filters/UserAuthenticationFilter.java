package com.tms.filters;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.models.AuthenticationResponse;
import com.tms.utils.JwtTokenUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
public class UserAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private final AuthenticationManager authenManager;
	

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			log.info("Username is: {}", username);
			log.info("Password is: {}", password);
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
			return authenManager.authenticate(token);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		User user = (User) authResult.getPrincipal();
		String username = user.getUsername();
		List<String> authorities = getAuthorities(user);
		
		String accessToken = JwtTokenUtil.generateAccessToken(request, username, authorities);
		
		String refreshToken = JwtTokenUtil.generateRefreshToken(request, username);
		
		AuthenticationResponse authResponse = new AuthenticationResponse(username, accessToken, refreshToken, authorities);
		sendAuthenticationResponse(response, authResponse);
	}
	
	private List<String> getAuthorities(User user) {
		return user.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());
	}
	
	private void sendAuthenticationResponse(HttpServletResponse response, AuthenticationResponse authResponse)
			throws StreamWriteException, DatabindException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getOutputStream(), authResponse);
	}
	
	

}
