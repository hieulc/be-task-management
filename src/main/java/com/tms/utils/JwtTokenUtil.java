package com.tms.utils;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtTokenUtil {
	
	private static final long EXPIRE_DURATION_OF_ACCESS_TOKEN = 24 * 60 * 60 * 1000;
	private static final long EXPIRE_DURATION_OF_REFRESH_TOKEN = 15 * 24 * 60 * 60 * 1000;
	
	private static String SECRET_KEY = "hle223";
	private static Algorithm HMAC256 = Algorithm.HMAC256(SECRET_KEY.getBytes());
	
	public static String generateAccessToken(HttpServletRequest request, String username, List<String> authorities) {
		
		 String accessToken = JWT.create()
				 .withSubject(username)
				 .withIssuer(request.getRequestURI().toString())
				 .withClaim("roles", authorities)
				 .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_DURATION_OF_ACCESS_TOKEN))
				 .sign(HMAC256);
		 
		 return accessToken;
	}
	
	public static String generateRefreshToken(HttpServletRequest request, String username) {
		
		String refreshToken = JWT.create()
				.withSubject(username)
				.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_DURATION_OF_REFRESH_TOKEN))
				.withIssuer(request.getRequestURI().toString())
				.sign(HMAC256);
		
		return refreshToken;
	}
	
	public static DecodedJWT decode(String token) throws Exception {
		JWTVerifier verifier = JWT.require(HMAC256).build();
		DecodedJWT decodedToken = verifier.verify(token);
		return decodedToken;
	}
	
	public static String getSubject(DecodedJWT decodedToken) {
		return decodedToken.getSubject();
	}
	
	public static String[] getClaim(DecodedJWT decodedToken) {
		return decodedToken.getClaim("roles").asArray(String.class);
	}
	
	
}
