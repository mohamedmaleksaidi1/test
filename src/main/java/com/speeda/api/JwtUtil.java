package com.speeda.api;




import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
@Component
public class JwtUtil {
    private final int accessTokenExpirationMs = 15 * 60 * 1000;    // 15 min
    private final int refreshTokenExpirationMs = 7 * 24 * 60 * 60 * 1000; // 7 jours

    private final String jwtSecret = "QkXNd6HW!kF4v9atJ3uV2mSG8zLp5xAhBt6PfR0sWyZcEg7qL1jUoNiVrXsTbQeY"; // 64 caractères !

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }


    public String generateAccessToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + accessTokenExpirationMs))
                .claim("type", "access")
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + refreshTokenExpirationMs))
                .claim("type", "refresh")
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, String username) {
        final String usernameFromToken = extractUsername(token);
        return (usernameFromToken.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
