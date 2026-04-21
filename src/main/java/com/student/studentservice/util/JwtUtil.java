package com.student.studentservice.util;

import com.student.studentservice.service.Roles;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;

    // generate signing key from secret
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // generate access token
    public String generateAccessToken(String email, Roles role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.name());
        claims.put("type", "ACCESS");

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    // generate refresh token
    public String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH");

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    // extract email from token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // extract role from token
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // extract token type (ACCESS or REFRESH)
    public String extractTokenType(String token) {
        return extractClaims(token).get("type", String.class);
    }

    // check if token is expired
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // validate token
    public boolean validateToken(String token, String email) {
        try {
            String extractedEmail = extractEmail(token);
            return extractedEmail.equals(email) && !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // extract all claims from token
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
