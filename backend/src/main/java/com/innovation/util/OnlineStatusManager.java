package com.innovation.util;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OnlineStatusManager {

    private final ConcurrentHashMap<Integer, Set<String>> onlineUsers = new ConcurrentHashMap<>();

    public void addSession(Integer userId, String sessionId) {
        onlineUsers.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    public void removeSession(Integer userId, String sessionId) {
        Set<String> sessions = onlineUsers.get(userId);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                onlineUsers.remove(userId);
            }
        }
    }

    public boolean isOnline(Integer userId) {
        Set<String> sessions = onlineUsers.get(userId);
        return sessions != null && !sessions.isEmpty();
    }

    public Set<Integer> getOnlineUserIds() {
        return Collections.unmodifiableSet(onlineUsers.keySet());
    }
}
