package org.delcom.app.services;

import org.delcom.app.entities.AuthToken;
import org.delcom.app.repositories.AuthTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthTokenServiceTests {

    @Mock
    private AuthTokenRepository authTokenRepository;

    @InjectMocks
    private AuthTokenService authTokenService;

    @Test
    void testCreateToken() {
        AuthToken token = new AuthToken();
        token.setUserId(UUID.randomUUID());
        token.setToken("dummy-token");

        // Panggil method createToken (sesuai kode di AuthTokenService)
        authTokenService.createToken(token);

        // Verifikasi repo.save dipanggil
        verify(authTokenRepository, times(1)).save(token);
    }

    @Test
    void testFindUserToken() {
        UUID userId = UUID.randomUUID();
        String tokenString = "valid-token";
        AuthToken token = new AuthToken();
        token.setUserId(userId);
        token.setToken(tokenString);

        // Mock repository behavior (sesuai nama method di Repository yang baru)
        when(authTokenRepository.findByUserIdAndToken(userId, tokenString))
                .thenReturn(Optional.of(token));

        // Panggil service
        AuthToken result = authTokenService.findUserToken(userId, tokenString);

        // Assert
        assertNotNull(result);
        assertEquals(tokenString, result.getToken());
    }
}