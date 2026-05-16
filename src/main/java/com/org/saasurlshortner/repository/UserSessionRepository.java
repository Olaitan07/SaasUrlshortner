package com.org.saasurlshortner.repository;

import com.org.saasurlshortner.model.UserModel;
import com.org.saasurlshortner.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    Optional<UserSession> findByToken(String token);
    List<UserSession> findAllByUserAndActiveTrue(UserModel user);
    long countByUserAndActiveTrue(UserModel user);
    Optional<UserSession> findFirstByUserAndActiveTrueOrderByCreatedAtAsc(UserModel user);
}
