package com.speeda.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.*;

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

        Optional<TokenSession> oldSessionOpt = tokenSessionRepository.findFirstByUserOrderByCreatedAtDesc(user);
        oldSessionOpt.ifPresent(tokenSessionRepository::delete);

        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        TokenSession newSession = new TokenSession();
        newSession.setUser(user);
        newSession.setToken(newRefreshToken);
        newSession.setType("refresh");
        newSession.setCreatedAt(new Date());
        newSession.setExpiresAt(jwtUtil.extractExpiration(newRefreshToken));

        tokenSessionRepository.save(newSession);

        return ResponseEntity.ok(Map.of("refreshToken", newRefreshToken));
    }




    }




