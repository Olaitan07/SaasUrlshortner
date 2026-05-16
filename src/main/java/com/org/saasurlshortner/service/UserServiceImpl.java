package com.org.saasurlshortner.service;

import com.org.saasurlshortner.auth.JwtService;
import com.org.saasurlshortner.auth.UserAuthService;
import com.org.saasurlshortner.dto.request.LoginRequest;
import com.org.saasurlshortner.dto.request.RegisterRequest;
import com.org.saasurlshortner.dto.response.AuthResponse;
import com.org.saasurlshortner.dto.response.ResponseWrapper;
import com.org.saasurlshortner.dto.response.UserProxy;
import com.org.saasurlshortner.exceptions.ResourceNotFoundException;
import com.org.saasurlshortner.mapper.UserMapper;
import com.org.saasurlshortner.model.UserModel;
import com.org.saasurlshortner.repository.UserModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserModelRepository userModelRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final UserAuthService userAuthService;
    private final SessionService sessionService;

    @Override
    public ResponseWrapper<AuthResponse> register(RegisterRequest payload) {
        UserModel user = userMapper.toUserModel(payload);
        user.setPassword(passwordEncoder.encode(payload.getPassword()));
        UserModel savedUser = userModelRepository.save(user);

        UserDetails userDetails = userAuthService.loadUserByUsername(payload.getEmail());
        String token = jwtService.generateToken(userDetails);

        sessionService.createSession(
                savedUser,
                token,
                payload.getDeviceInfo(),
                false,
                jwtService.getExpirationDate(false)
        );

        return userMapper.toResponse(savedUser.getId(), token, "User registered successfully", HttpStatus.CREATED);
    }

    @Override
    public ResponseWrapper<AuthResponse> login(LoginRequest payload) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(payload.getEmail(), payload.getPassword())
        );

        UserModel user = userModelRepository.findByEmail(payload.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserDetails userDetails = userAuthService.loadUserByUsername(payload.getEmail());
        String token = jwtService.generateToken(userDetails, payload.isRememberMe());

        sessionService.createSession(
                user,
                token,
                payload.getDeviceInfo(),
                payload.isRememberMe(),
                jwtService.getExpirationDate(payload.isRememberMe())
        );

        return userMapper.toResponse(user.getId(), token, "Login successful", HttpStatus.OK);
    }

    @Override
    public ResponseWrapper<Void> logout(String token) {
        ResponseWrapper<Void> result = sessionService.logoutCurrentSession(token);
        SecurityContextHolder.clearContext();
        return result;
    }

    @Override
    public ResponseWrapper<UserProxy> getProfile(String email) {
        UserModel user = userModelRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseWrapper.<UserProxy>builder()
                .data(userMapper.toUserProxy(user))
                .message("Profile retrieved successfully")
                .httpStatusCode(HttpStatus.OK)
                .build();
    }
}
