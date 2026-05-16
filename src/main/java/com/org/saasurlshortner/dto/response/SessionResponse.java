package com.org.saasurlshortner.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponse {
    private UUID id;
    private String deviceInfo;
    private boolean rememberMe;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastActiveAt;
}
