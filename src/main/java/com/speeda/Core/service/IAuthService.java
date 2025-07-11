package com.speeda.Core.service;
import com.speeda.Core.dto.*;
public interface IAuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(TokenRefreshRequest request);
    void logout(LogoutRequest request);
}
