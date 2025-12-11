package org.delcom.app.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {

    // Secret Key punya kamu (Jangan diubah agar cocok dengan token yang mungkin sudah tergenerate)
    private static final String SECRET_KEY = "NghR8fQn5O6V2z7VwpvQkDELCOMXoCYQbQZjx3xWUpPfw5i9L8RrGg==";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2; // 2 jam
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static SecretKey getKey() {
        return key;
    }

    // 1. GENERATE TOKEN
    public static String generateToken(UUID userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // 2. GET USER ID FROM TOKEN (INI YANG DIBUTUHKAN AUTH INTERCEPTOR)
    // Saya buat method ini untuk menjembatani panggilan dari AuthInterceptor
    public static UUID getUserIdFromToken(String token) {
        return extractUserId(token);
    }

    // Method asli punya kamu (tetap dipertahankan)
    public static UUID extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return UUID.fromString(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    // 3. VALIDASI TOKEN
    public static boolean validateToken(String token, boolean ignoreExpired) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true; // valid
        } catch (ExpiredJwtException e) {
            if (ignoreExpired) {
                return true; // abaikan expired
            }
            return false;
        } catch (Exception e) {
            return false; // token invalid
        }
    }
}