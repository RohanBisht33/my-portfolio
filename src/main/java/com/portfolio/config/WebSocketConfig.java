package com.portfolio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocket configuration for the live execution terminal.
 * Provides a /ws/terminal endpoint that the frontend terminal
 * module connects to for real-time command output streaming.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new TerminalWebSocketHandler(), "/ws/terminal")
                .setAllowedOrigins("*");
    }

    /**
     * Simple handler that echoes commands back.
     * In production, this would dispatch to a sandboxed executor.
     */
    static class TerminalWebSocketHandler extends TextWebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            session.sendMessage(new TextMessage("[system] Terminal session established."));
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String payload = message.getPayload();
            // Echo the command back as acknowledgment
            session.sendMessage(new TextMessage("[exec] Received: " + payload));
            session.sendMessage(new TextMessage("[info] Command processing is handled by the server executor."));
        }
    }
}
