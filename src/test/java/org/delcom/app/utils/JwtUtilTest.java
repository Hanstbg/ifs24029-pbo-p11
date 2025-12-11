package org.delcom.app.utils;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    // --- 1. Test Generate, Extract, & Validate (Happy Path) ---
    @Test
    void testTokenLifecycle() {
        UUID userId = UUID.randomUUID();

        // A. Generate Token
        String token = JwtUtil.generateToken(userId);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // B. Extract User ID (Wrapper Method)
        UUID extractedId = JwtUtil.getUserIdFromToken(token);
        assertEquals(userId, extractedId);

        // C. Extract User ID (Direct Method)
        UUID directId = JwtUtil.extractUserId(token);
        assertEquals(userId, directId);

        // D. Validate Token (Normal)
        assertTrue(JwtUtil.validateToken(token, false));
    }

    // --- 2. Test Token Rusak / Invalid ---
    @Test
    void testInvalidToken() {
        String invalidToken = "ini.token.ngasal";

        // Extract harus return null (masuk catch Exception)
        assertNull(JwtUtil.extractUserId(invalidToken));

        // Validate harus return false
        assertFalse(JwtUtil.validateToken(invalidToken, false));
    }

    // --- 3. Test Token Expired (Case Khusus catch ExpiredJwtException) ---
    @Test
    void testExpiredToken() {
        UUID userId = UUID.randomUUID();

        // Kita buat token secara MANUAL yang sudah kedaluwarsa 1 detik yang lalu.
        // Kita harus menggunakan JwtUtil.getKey() agar tanda tangannya (signature) tetap valid,
        // sehingga parser tidak menganggapnya invalid token, tapi expired token.
        String expiredToken = Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date(System.currentTimeMillis() - 20000)) // Dibuat 20 detik lalu
                .expiration(new Date(System.currentTimeMillis() - 1000)) // Expired 1 detik lalu
                .signWith(JwtUtil.getKey()) // Sign dengan key aplikasi
                .compact();

        // Skenario A: Validasi ketat (ignoreExpired = false)
        // Harusnya return FALSE karena expired
        assertFalse(JwtUtil.validateToken(expiredToken, false));

        // Skenario B: Abaikan expired (ignoreExpired = true)
        // Harusnya return TRUE (Masuk catch ExpiredJwtException -> return true)
        assertTrue(JwtUtil.validateToken(expiredToken, true));
    }

    // --- 4. Test Constructor & GetKey (Untuk Coverage 100%) ---
    @Test
    void testStructure() {
        // Memastikan key tidak null
        assertNotNull(JwtUtil.getKey());
        
        // Memanggil constructor (hanya untuk coverage class definition)
        new JwtUtil(); 
    }
}