package com.org.saasurlshortner.controller;

import com.org.saasurlshortner.dto.request.LoginRequest;
import com.org.saasurlshortner.dto.request.RegisterRequest;
import com.org.saasurlshortner.dto.response.AuthResponse;
import com.org.saasurlshortner.dto.response.ResponseWrapper;
import com.org.saasurlshortner.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseWrapper<AuthResponse>> register(@Valid @RequestBody RegisterRequest payload) {
        ResponseWrapper<AuthResponse> response = userService.register(payload);
        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<AuthResponse>> login(@Valid @RequestBody LoginRequest payload) {
        ResponseWrapper<AuthResponse> response = userService.login(payload);
        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseWrapper<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseWrapper.<Void>builder()
                            .data(null)
                            .message("No active session found")
                            .httpStatusCode(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
        String token = authHeader.substring(7);
        ResponseWrapper<Void> response = userService.logout(token);
        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }
}
