package com.innovation.config;

import com.innovation.util.OnlineStatusManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Slf4j
@Component
public class WebSocketEventListener {

    @Autowired
    private OnlineStatusManager onlineStatusManager;

    @EventListener
    public void handleSessionConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        if (principal != null) {
            Integer userId = Integer.parseInt(principal.getName());
            onlineStatusManager.addSession(userId, accessor.getSessionId());
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        if (principal != null) {
            Integer userId = Integer.parseInt(principal.getName());
            onlineStatusManager.removeSession(userId, accessor.getSessionId());
        }
    }
}
