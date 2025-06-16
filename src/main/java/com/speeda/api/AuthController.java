package com.speeda.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
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




        @PostMapping("/refreshToken")
        public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
            String phone = request.get("phone");
            if (phone == null || phone.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Numéro de téléphone manquant"));
            }

            Optional<User> userOpt = userRepository.findByPhoneNumber(phone);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Utilisateur non trouvé"));
            }

            User user = userOpt.get();

            // Récupère le dernier refresh token expiré (s’il y en a)
            Optional<TokenSession> expiredRefreshTokenOpt = user.getTokenSessions().stream()
                    .filter(t -> "refresh".equalsIgnoreCase(t.getType()))
                    .filter(t -> t.getExpiresAt() != null && t.getExpiresAt().before(new Date()))
                    .sorted(Comparator.comparing(TokenSession::getCreatedAt).reversed())
                    .findFirst();

            if (expiredRefreshTokenOpt.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "Aucun refresh token expiré trouvé pour cet utilisateur"));
            }

            // Générer un nouveau refresh token
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

            // Supprimer l’ancien refresh token
            tokenSessionRepository.delete(expiredRefreshTokenOpt.get());

            // Enregistrer le nouveau refresh token
            TokenSession newTokenSession = new TokenSession();
            newTokenSession.setUser(user);
            newTokenSession.setToken(newRefreshToken);
            newTokenSession.setType("refresh");
            newTokenSession.setCreatedAt(new Date());
            newTokenSession.setExpiresAt(jwtUtil.extractExpiration(newRefreshToken));
            tokenSessionRepository.save(newTokenSession);

            return ResponseEntity.ok(Map.of("refreshToken", newRefreshToken));
        }




    }




