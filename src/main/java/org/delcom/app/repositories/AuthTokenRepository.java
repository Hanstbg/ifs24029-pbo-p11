package org.delcom.app.repositories;

import org.delcom.app.entities.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, UUID> {
    
    // Method ini yang menyebabkan error "cannot find symbol"
    // Spring Data JPA akan otomatis membuat query-nya berdasarkan nama method ini
    Optional<AuthToken> findByUserIdAndToken(UUID userId, String token);
    
    // Opsional: Cari token berdasarkan user saja (jika nanti butuh)
    Optional<AuthToken> findByUserId(UUID userId);
}