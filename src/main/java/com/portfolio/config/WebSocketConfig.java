package com.portfolio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket configuration for the live execution terminal.
 * Provides a /ws/terminal endpoint for real-time command streaming.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new TerminalWebSocketHandler(), "/ws/terminal")
                .setAllowedOrigins("*");
    }

    static class TerminalWebSocketHandler extends TextWebSocketHandler {

        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            session.sendMessage(new TextMessage("[system] Terminal WebSocket connection established."));
            session.sendMessage(new TextMessage("[system] Connected to Rohan Bisht's Portfolio Server (Spring Boot 3.x)."));
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String payload = message.getPayload();
            session.sendMessage(new TextMessage("[exec] Command received: " + payload));

            if (payload.toLowerCase().contains("nexstore")) {
                session.sendMessage(new TextMessage("[spring] Initializing NexStore E-Commerce module..."));
                scheduler.schedule(() -> sendSafe(session, "[db] Engaging PostgreSQL pessimistic write locks on inventory..."), 300, TimeUnit.MILLISECONDS);
                scheduler.schedule(() -> sendSafe(session, "[docker] Pulling immutable image from Azure Container Registry..."), 600, TimeUnit.MILLISECONDS);
                scheduler.schedule(() -> sendSafe(session, "[azure] Hot restart verified on Azure App Service: 0 downtime."), 900, TimeUnit.MILLISECONDS);
                scheduler.schedule(() -> sendSafe(session, "[success] NexStore live at https://nexstore.azurewebsites.net"), 1200, TimeUnit.MILLISECONDS);
            } else if (payload.toLowerCase().contains("atm")) {
                session.sendMessage(new TextMessage("[java17] Booting Enterprise ATM Simulator banking engine..."));
                scheduler.schedule(() -> sendSafe(session, "[pattern] Command Pattern dispatch table: O(1) dynamic registry ready."), 300, TimeUnit.MILLISECONDS);
                scheduler.schedule(() -> sendSafe(session, "[jdbc] Relational transaction persistence verified with PostgreSQL."), 600, TimeUnit.MILLISECONDS);
                scheduler.schedule(() -> sendSafe(session, "[junit5] Running concurrent transaction stress tests: 28/28 PASSED."), 900, TimeUnit.MILLISECONDS);
                scheduler.schedule(() -> sendSafe(session, "[success] Enterprise ATM Simulator active on GitHub: RohanBisht33/atm-simulator"), 1200, TimeUnit.MILLISECONDS);
            } else {
                session.sendMessage(new TextMessage("[info] Type 'help' or run 'run nexstore' / 'run atm-simulator' to execute."));
            }
        }

        private void sendSafe(WebSocketSession session, String text) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(text));
                }
            } catch (Exception ignored) {}
        }
    }
}
