package com.org.saasurlshortner.mapper;

import com.org.saasurlshortner.dto.request.RegisterRequest;
import com.org.saasurlshortner.dto.response.AuthResponse;
import com.org.saasurlshortner.dto.response.ResponseWrapper;
import com.org.saasurlshortner.dto.response.UserProxy;
import com.org.saasurlshortner.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserModel toUserModel(RegisterRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserProxy toUserProxy(UserModel userModel);

    default ResponseWrapper<AuthResponse> toResponse(UUID id, String token, String message, HttpStatus status) {
        return ResponseWrapper.<AuthResponse>builder()
                .data(AuthResponse.builder().id(id).token(token).build())
                .message(message)
                .httpStatusCode(status)
                .build();
    }
}
