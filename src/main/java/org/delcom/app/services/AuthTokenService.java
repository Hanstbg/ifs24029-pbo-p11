package org.delcom.app.services;

import org.delcom.app.entities.AuthToken;
import org.delcom.app.repositories.AuthTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthTokenService {

    @Autowired
    private AuthTokenRepository authTokenRepository;

    // Method yang sudah ada (untuk interceptor)
    public AuthToken findUserToken(UUID userId, String token) {
        return authTokenRepository.findByUserIdAndToken(userId, token).orElse(null);
    }

    // --- PERBAIKAN: Tambahkan method createToken ---
    public void createToken(AuthToken authToken) {
        authTokenRepository.save(authToken);
    }
}