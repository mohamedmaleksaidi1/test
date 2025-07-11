package com.speeda.Core.repository;

import com.speeda.Core.model.AuthToken;
import com.speeda.Core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    /**
     * Trouve le token d'un utilisateur.
     */
    Optional<AuthToken> findByUser(User user);

    /**
     * Trouve par refresh token (pour la logique de refresh JWT).
     */
    Optional<AuthToken> findByRefreshToken(String refreshToken);

    /**
     * Supprime tous les tokens liés à un utilisateur.
     */
    void deleteByUser(User user);
}
