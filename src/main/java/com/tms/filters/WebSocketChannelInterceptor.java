package com.tms.filters;

import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import static java.util.Optional.ofNullable;

import java.time.LocalDateTime;

@Service
@Slf4j
public class WebSocketChannelInterceptor implements ChannelInterceptor {

	private static final String API_KEY_HEADER = "authToken";
	private static final String SESSION_KEY_HEADER = "sessionId";
	private static final String WS_ID_HEADER = "wsId";

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		final StompHeaderAccessor accessor = readHeaderAccessor(message);

		if (accessor.getCommand() == StompCommand.CONNECT) {

			String wsId = readWebSocketIdHeader(accessor);
			String apiKey = readAuthKeyHeader(accessor);
			String sessionId = readSessionId(accessor);

			
			UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(wsId, null);
			accessor.setUser(user);
			accessor.setHeader("connection-time", LocalDateTime.now().toString());
			log.info(
					"User with authKey '{}', ws-id {} session {} make a WebSocket connection and generated the user {}",
					apiKey, wsId, sessionId, user.toString());

		}

		return message;

	}

	private StompHeaderAccessor readHeaderAccessor(Message<?> message) {
		final StompHeaderAccessor accessor = getAccessor(message);
		if (accessor == null) {
			throw new AuthenticationCredentialsNotFoundException("Fail to read headers.");
		}
		return accessor;
	}

	private String readSessionId(StompHeaderAccessor accessor) {
		return ofNullable(accessor.getMessageHeaders().get(SESSION_KEY_HEADER))
				.orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Session header not found"))
				.toString();
	}

	private String readAuthKeyHeader(StompHeaderAccessor accessor) {
		final String authKey = accessor.getFirstNativeHeader(API_KEY_HEADER);
		if (authKey == null || authKey.trim().isEmpty())
			throw new AuthenticationCredentialsNotFoundException("Auth Key Not Found");
		return authKey;
	}

	private String readWebSocketIdHeader(StompHeaderAccessor accessor) {
		final String wsId = accessor.getFirstNativeHeader(WS_ID_HEADER);
		if (wsId == null || wsId.trim().isEmpty())
			throw new AuthenticationCredentialsNotFoundException("Web Socket ID Header not found");
		return wsId;
	}

	StompHeaderAccessor getAccessor(Message<?> message) {
		return MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
	}

}
