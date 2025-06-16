package com.speeda.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final TokenSessionRepository tokenSessionRepository;
    private void saveTokenSession(User user, String token, String type, Date expiresAt) {
        TokenSession session = new TokenSession();
        session.setUser(user);
        session.setToken(token);
        session.setType(type);
        session.setCreatedAt(new Date());
        session.setExpiresAt(expiresAt);
        session.setStatus("ACTIVE");
        tokenSessionRepository.save(session);
    }
    // --- Constructeur pour l'injection de dépendance ---
    public AuthController(AuthService authService, JwtUtil jwtUtil, TokenSessionRepository tokenSessionRepository) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.tokenSessionRepository = tokenSessionRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessTokenByPhone(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");

        Optional<User> userOpt = userRepository.findByPhoneNumber(phone);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Utilisateur non trouvé"));
        }

        User user = userOpt.get();

        // Trouver un refresh token actif et non expiré
        Optional<TokenSession> refreshTokenOpt = user.getTokenSessions().stream()
                .filter(t -> "refresh".equalsIgnoreCase(t.getType()))
                .filter(t -> "ACTIVE".equalsIgnoreCase(t.getStatus()))
                .filter(t -> t.getExpiresAt() != null && t.getExpiresAt().after(new Date()))
                .findFirst();

        if (refreshTokenOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Aucun refresh token valide trouvé"));
        }

        // Générer nouveau access token
        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername());
        saveTokenSession(user, newAccessToken, "access", jwtUtil.extractExpiration(newAccessToken));

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }


}

