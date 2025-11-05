package com.subscription.subscription_system.utils;

import com.subscription.subscription_system.exception.CommonException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final String SECRET_KEY = "yF7pTz2V9kL3nXq8bA1mH5rS6dP4uE0cJ9oZxR2tWvU7yNqM3bL5hG8sF1dK6aT9"; // At least 32 chars
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 5; // 5 hours

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(String token) throws CommonException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new CommonException("Invalid JWT token", HttpStatus.BAD_REQUEST.value());
        }
    }
    public String extractEmail(String token) throws Exception {
        return validateToken(token).getSubject();
    }

    public String extractRole(String token) throws Exception {
        return validateToken(token).get("role", String.class);
    }
}
