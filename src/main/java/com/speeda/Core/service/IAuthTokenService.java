package com.speeda.Core.service;
import com.speeda.Core.model.AuthToken;
import com.speeda.Core.model.User;
import java.util.Optional;
import java.util.Optional;

public interface IAuthTokenService {

    AuthToken createOrUpdateAuthToken(User user, String accessToken);
    AuthToken createOrUpdateAuthTokenN8N(String phoneNumber);

    Optional<AuthToken> findByRefreshToken(String refreshToken);


    boolean isRefreshTokenExpired(AuthToken authToken);


    void deleteByUser(User user);
    /**
     * Recherche un AuthToken par utilisateur.
     * @param user L'utilisateur concerné
     * @return Optional<AuthToken>
     */
    Optional<AuthToken> findByUser(User user);

}
