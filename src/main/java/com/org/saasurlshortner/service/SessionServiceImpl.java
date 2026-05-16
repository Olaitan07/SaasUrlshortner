package com.org.saasurlshortner.service;

import com.org.saasurlshortner.dto.response.ResponseWrapper;
import com.org.saasurlshortner.dto.response.SessionResponse;
import com.org.saasurlshortner.exceptions.ResourceNotFoundException;
import com.org.saasurlshortner.model.UserModel;
import com.org.saasurlshortner.model.UserSession;
import com.org.saasurlshortner.repository.UserModelRepository;
import com.org.saasurlshortner.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private static final int MAX_DEVICES = 2;

    private final UserSessionRepository userSessionRepository;
    private final UserModelRepository userModelRepository;

    @Override
    public UserSession createSession(UserModel user, String token, String deviceInfo, boolean rememberMe, LocalDateTime expiresAt) {
        long activeSessions = userSessionRepository.countByUserAndActiveTrue(user);

        if (activeSessions >= MAX_DEVICES) {
            userSessionRepository.findFirstByUserAndActiveTrueOrderByCreatedAtAsc(user)
                    .ifPresent(oldest -> {
                        oldest.setActive(false);
                        userSessionRepository.save(oldest);
                    });
        }

        UserSession session = UserSession.builder()
                .user(user)
                .token(token)
                .deviceInfo(deviceInfo != null ? deviceInfo : "Unknown device")
                .rememberMe(rememberMe)
                .active(true)
                .expiresAt(expiresAt)
                .lastActiveAt(LocalDateTime.now())
                .build();

        return userSessionRepository.save(session);
    }

    @Override
    public boolean isSessionActive(String token) {
        return userSessionRepository.findByToken(token)
                .map(UserSession::isActive)
                .orElse(false);
    }

    @Override
    public void updateLastActive(String token) {
        userSessionRepository.findByToken(token).ifPresent(session -> {
            session.setLastActiveAt(LocalDateTime.now());
            userSessionRepository.save(session);
        });
    }

    @Override
    public ResponseWrapper<List<SessionResponse>> getActiveSessions(String email) {
        UserModel user = userModelRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<SessionResponse> sessions = userSessionRepository.findAllByUserAndActiveTrue(user)
                .stream()
                .map(s -> SessionResponse.builder()
                        .id(s.getId())
                        .deviceInfo(s.getDeviceInfo())
                        .rememberMe(s.isRememberMe())
                        .active(s.isActive())
                        .createdAt(s.getCreatedAt())
                        .expiresAt(s.getExpiresAt())
                        .lastActiveAt(s.getLastActiveAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseWrapper.<List<SessionResponse>>builder()
                .data(sessions)
                .message("Active sessions retrieved")
                .httpStatusCode(HttpStatus.OK)
                .build();
    }

    @Override
    public ResponseWrapper<Void> revokeSession(UUID sessionId) {
        UserSession session = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        session.setActive(false);
        userSessionRepository.save(session);
        return ResponseWrapper.<Void>builder()
                .data(null)
                .message("Session revoked successfully")
                .httpStatusCode(HttpStatus.OK)
                .build();
    }

    @Override
    public ResponseWrapper<Void> logoutCurrentSession(String token) {
        userSessionRepository.findByToken(token).ifPresent(session -> {
            session.setActive(false);
            userSessionRepository.save(session);
        });
        return ResponseWrapper.<Void>builder()
                .data(null)
                .message("Logged out successfully")
                .httpStatusCode(HttpStatus.OK)
                .build();
    }

    @Override
    public ResponseWrapper<Void> revokeAllSessions(String email) {
        UserModel user = userModelRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<UserSession> sessions = userSessionRepository.findAllByUserAndActiveTrue(user);
        sessions.forEach(s -> s.setActive(false));
        userSessionRepository.saveAll(sessions);
        return ResponseWrapper.<Void>builder()
                .data(null)
                .message("All sessions revoked successfully")
                .httpStatusCode(HttpStatus.OK)
                .build();
    }
}
