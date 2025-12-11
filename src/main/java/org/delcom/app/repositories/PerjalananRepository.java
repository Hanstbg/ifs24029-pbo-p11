package org.delcom.app.repositories;

import org.delcom.app.entities.Perjalanan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PerjalananRepository extends JpaRepository<Perjalanan, UUID> {
    
    // Query default JPA
    List<Perjalanan> findByUserId(UUID userId);
    
    // --- PERBAIKAN: Menambahkan method yang error sebelumnya ---
    List<Perjalanan> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
    
    // Query spesifik untuk keamanan (cek id dan user_id)
    Optional<Perjalanan> findByIdAndUserId(UUID id, UUID userId);
    
    // (Opsional) Jika method findByUserId sudah ada di atas, yang findAllByUserId di bawah ini bisa dihapus jika duplikat
    // Tapi untuk aman biarkan saja list di bawah ini:
    List<Perjalanan> findAllByUserId(UUID userId);
}