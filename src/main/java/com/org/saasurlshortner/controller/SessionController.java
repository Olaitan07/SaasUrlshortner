package com.org.saasurlshortner.controller;

import com.org.saasurlshortner.dto.response.ResponseWrapper;
import com.org.saasurlshortner.dto.response.SessionResponse;
import com.org.saasurlshortner.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<SessionResponse>>> getActiveSessions(
            @AuthenticationPrincipal UserDetails userDetails) {
        ResponseWrapper<List<SessionResponse>> response = sessionService.getActiveSessions(userDetails.getUsername());
        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<ResponseWrapper<Void>> revokeSession(@PathVariable UUID sessionId) {
        ResponseWrapper<Void> response = sessionService.revokeSession(sessionId);
        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }

    @DeleteMapping("/all")
    public ResponseEntity<ResponseWrapper<Void>> revokeAllSessions(
            @AuthenticationPrincipal UserDetails userDetails) {
        ResponseWrapper<Void> response = sessionService.revokeAllSessions(userDetails.getUsername());
        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }
}
