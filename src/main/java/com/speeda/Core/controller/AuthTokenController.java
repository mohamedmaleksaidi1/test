package com.speeda.Core.controller;

import com.speeda.Core.model.AuthToken;
import com.speeda.Core.service.AuthTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/authn8n")
public class AuthTokenController {

    @Autowired
    private AuthTokenService authTokenService;

    @PostMapping("/refreshtoken")
    public ResponseEntity<Map<String, Object>> updateRefreshToken(@RequestParam("phoneNumber") String phoneNumber) {
        try {
            AuthToken token = authTokenService.createOrUpdateAuthTokenN8N(phoneNumber);

            Map<String, Object> response = Map.of(
                    "refreshToken", token.getRefreshToken(),
                    "expiryDate", token.getExpiryDate(),
                    "userId", token.getUser().getId()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
