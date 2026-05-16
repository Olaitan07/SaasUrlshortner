package com.org.saasurlshortner.service;

import com.org.saasurlshortner.dto.response.ResponseWrapper;
import com.org.saasurlshortner.dto.response.SessionResponse;
import com.org.saasurlshortner.model.UserModel;
import com.org.saasurlshortner.model.UserSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SessionService {
    UserSession createSession(UserModel user, String token, String deviceInfo, boolean rememberMe, LocalDateTime expiresAt);
    boolean isSessionActive(String token);
    void updateLastActive(String token);
    ResponseWrapper<List<SessionResponse>> getActiveSessions(String email);
    ResponseWrapper<Void> revokeSession(UUID sessionId);
    ResponseWrapper<Void> revokeAllSessions(String email);
    ResponseWrapper<Void> logoutCurrentSession(String token);
}
