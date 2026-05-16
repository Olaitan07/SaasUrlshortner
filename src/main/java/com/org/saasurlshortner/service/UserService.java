package com.org.saasurlshortner.service;

import com.org.saasurlshortner.dto.request.LoginRequest;
import com.org.saasurlshortner.dto.request.RegisterRequest;
import com.org.saasurlshortner.dto.response.AuthResponse;
import com.org.saasurlshortner.dto.response.ResponseWrapper;
import com.org.saasurlshortner.dto.response.UserProxy;

public interface UserService {
    ResponseWrapper<AuthResponse> register(RegisterRequest payload);
    ResponseWrapper<AuthResponse> login(LoginRequest payload);
    ResponseWrapper<Void> logout(String token);
    ResponseWrapper<UserProxy> getProfile(String email);
}
