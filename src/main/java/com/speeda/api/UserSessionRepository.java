package com.speeda.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    UserSession findByToken(String token);
    List<UserSession> findByUser(User user);
}
