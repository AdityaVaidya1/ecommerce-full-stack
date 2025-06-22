package com.aditya.ecommerce.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "thisisaverysecurekeyforjwttokenandshouldbe32chars"; // ✅ Must be 32+ characters
    private final long EXPIRATION_TIME = 86400000; // 1 day in ms

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ✅ Generate token with username and role
    public String generateToken(String username, String role) {
        String token = Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        System.out.println("🔐 Generated JWT for user: " + username + " with role: " + role);
        return token;
    }

    // ✅ Extract username from token
    public String extractUsername(String token) {
        try {
            String username = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            System.out.println("🔍 Username extracted from token: " + username);
            return username;
        } catch (JwtException e) {
            System.out.println("❌ Failed to extract username: " + e.getMessage());
            return null;
        }
    }

    // ✅ Extract role from token
    public String extractRole(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);
        } catch (JwtException e) {
            System.out.println("❌ Failed to extract role: " + e.getMessage());
            return null;
        }
    }

    // ✅ Validate token
    public boolean isTokenValid(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            boolean expired = isTokenExpired(token);
            boolean valid = extractedUsername != null && extractedUsername.equals(username) && !expired;
            System.out.println(valid ? "✅ Token is valid" : "❌ Token is invalid");
            return valid;
        } catch (Exception e) {
            System.out.println("❌ Token validation error: " + e.getMessage());
            return false;
        }
    }

    // ✅ Check expiration
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            System.out.println("❌ Error checking token expiration: " + e.getMessage());
            return true;
        }
    }
}
