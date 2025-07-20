package com.speeda.Core.service;
import com.speeda.Core.dto.*;
import com.speeda.Core.model.AuthToken;
import com.speeda.Core.model.Enum.UserStatus;
import com.speeda.Core.model.User;
import com.speeda.Core.repository.UserRepository;
import com.speeda.Core.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final IAuthTokenService authTokenService;

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
        return new AuthResponse(accessToken, authToken.getRefreshToken());
    }




    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername())
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
