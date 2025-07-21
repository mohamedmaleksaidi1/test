package com.speeda.Core.service;
import com.speeda.Core.dto.*;
import com.speeda.Core.model.AuthToken;
import com.speeda.Core.model.Enum.UserStatus;
import com.speeda.Core.model.User;
import com.speeda.Core.repository.UserRepository;
import com.speeda.Core.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final IAuthTokenService authTokenService;
    private static final String VERIFY_TOKEN = "whatsappWebhookToken2024";
    private static final String N8N_WEBHOOK_URL = "https://n8n.speeda.ai/webhook-test/e86f9292-10ec-4025-87f6-e46f9dcd9cce";
    private final RestTemplate restTemplate = new RestTemplate();
    @Override
    public AuthResponse registerAndAuthenticate(RegisterRequest request) {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(request.getPhoneNumber());
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            if (request.getUsername() != null) {
                user.setUsername(request.getUsername());
            }
            if (request.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            userRepository.save(user);
        } else {
            user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .phoneNumber(request.getPhoneNumber())
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(user);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
        );

        if (user.getStatus() != UserStatus.CONFIRMER) {
            user.setStatus(UserStatus.CONFIRMER);
            userRepository.save(user);
        }

        Optional<AuthToken> optionalAuthToken = authTokenService.findByUser(user);
        String accessToken;
        if (optionalAuthToken.isPresent()) {
            accessToken = optionalAuthToken.get().getAccessToken();
        } else {
            accessToken = jwtUtil.generateToken(user.getUsername());
        }
        AuthToken authToken = authTokenService.createOrUpdateAuthToken(user, accessToken);

        // Déclenche le webhook (avec la variable statique N8N_WEBHOOK_URL)
        triggerWebhook(user);

        return new AuthResponse(accessToken, authToken.getRefreshToken());
    }
    private void triggerWebhook(User user) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> toSend = new HashMap<>();
            toSend.put("user_id", user.getId());
            toSend.put("phone", user.getPhoneNumber());
            toSend.put("user_exist", true);
            toSend.put("token_valide", true); // à adapter selon ta logique
            toSend.put("status", user.getStatus().name());


            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(toSend, headers);
            restTemplate.postForEntity(N8N_WEBHOOK_URL, entity, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != UserStatus.CONFIRMER) {
            user.setStatus(UserStatus.CONFIRMER);
            userRepository.save(user);
        }

        Optional<AuthToken> optionalAuthToken = authTokenService.findByUser(user);
        String accessToken;
        if (optionalAuthToken.isPresent()) {
            accessToken = optionalAuthToken.get().getAccessToken();
        } else {
            accessToken = jwtUtil.generateToken(user.getUsername());
        }
        AuthToken authToken = authTokenService.createOrUpdateAuthToken(user, accessToken);

        return new AuthResponse(accessToken, authToken.getRefreshToken());
    }



    @Override
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        AuthToken authToken = authTokenService.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        if (authTokenService.isRefreshTokenExpired(authToken)) {
            authTokenService.deleteByUser(authToken.getUser());
            throw new RuntimeException("Refresh token expired, please login again");
        }
        String accessToken = jwtUtil.generateToken(authToken.getUser().getUsername());
        return new AuthResponse(accessToken, authToken.getRefreshToken());
    }

    @Override
    public void logout(LogoutRequest request) {
        AuthToken authToken = authTokenService.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        authTokenService.deleteByUser(authToken.getUser());
    }
}
