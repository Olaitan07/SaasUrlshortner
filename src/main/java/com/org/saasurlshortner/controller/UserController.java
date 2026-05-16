package com.org.saasurlshortner.controller;

import com.org.saasurlshortner.dto.response.ResponseWrapper;
import com.org.saasurlshortner.dto.response.UserProxy;
import com.org.saasurlshortner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ResponseWrapper<UserProxy>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        ResponseWrapper<UserProxy> response = userService.getProfile(userDetails.getUsername());
        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }
}
