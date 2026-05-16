package com.org.saasurlshortner.dto.response;

import com.org.saasurlshortner.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProxy {
    private UUID id;
    private String name;
    private String email;
    private Set<Roles> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
