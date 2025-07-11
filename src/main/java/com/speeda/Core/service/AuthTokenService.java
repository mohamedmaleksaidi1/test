package com.speeda.Core.service;

import com.speeda.Core.model.AuthToken;
import com.speeda.Core.model.User;
import com.speeda.Core.repository.AuthTokenRepository;
import com.speeda.Core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthTokenService implements IAuthTokenService {

    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final AuthTokenRepository authTokenRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public AuthToken createOrUpdateAuthToken(User user, String accessToken) {
        Optional<AuthToken> optionalAuthToken = authTokenRepository.findByUser(user);
        String refreshToken = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(refreshTokenDurationMs);

        AuthToken authToken;
        if (optionalAuthToken.isPresent()) {
            // On update SEULEMENT le refresh token et la date d'expiration
            authToken = optionalAuthToken.get();
            authToken.setRefreshToken(refreshToken);
            authToken.setExpiryDate(expiryDate);
            // accessToken NE BOUGE PAS !
        } else {
            // Premier login : on stocke tout
            authToken = AuthToken.builder()
                    .user(user)
                    .accessToken(accessToken) // généré uniquement au premier login
                    .refreshToken(refreshToken)
                    .expiryDate(expiryDate)
                    .build();
        }
        return authTokenRepository.save(authToken);
    }

    @Override
    public Optional<AuthToken> findByRefreshToken(String refreshToken) {
        return authTokenRepository.findByRefreshToken(refreshToken);
    }

    @Override
    public boolean isRefreshTokenExpired(AuthToken authToken) {
        return authToken.getExpiryDate().isBefore(Instant.now());
    }

    @Override
    @Transactional
    public void deleteByUser(User user) {
        authTokenRepository.deleteByUser(user);
    }

    @Override
    public Optional<AuthToken> findByUser(User user) {
        return authTokenRepository.findByUser(user);
    }

    @Override
    @Transactional
    public AuthToken createOrUpdateAuthTokenN8N(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable pour le numéro : " + phoneNumber));

        Optional<AuthToken> optionalToken = authTokenRepository.findByUser(user);

        String newRefreshToken = UUID.randomUUID().toString();
        Instant newExpiry = Instant.now().plusMillis(refreshTokenDurationMs);

        AuthToken token;
        if (optionalToken.isPresent()) {
            token = optionalToken.get();
            token.setRefreshToken(newRefreshToken);
            token.setExpiryDate(newExpiry);
        } else {
            token = AuthToken.builder()
                    .user(user)
                    .accessToken("generated-once-or-null") // si obligatoire
                    .refreshToken(newRefreshToken)
                    .expiryDate(newExpiry)
                    .build();
        }

        return authTokenRepository.save(token);
    }


}
