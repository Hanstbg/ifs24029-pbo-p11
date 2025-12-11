package org.delcom.app.entities;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "perjalanan")
public class Perjalanan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "judul")
    private String judul;

    @Column(name = "lokasi")
    private String lokasi;

    @Column(name = "deskripsi", length = 2000)
    private String deskripsi;

    @Column(name = "foto_utama")
    private String fotoUtama;

    @Column(name = "tanggal_mulai")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalMulai;

    @Column(name = "tanggal_selesai")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSelesai;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- CONSTRUCTOR ---
    
    // 1. Constructor Kosong (WAJIB ADA untuk JPA)
    public Perjalanan() {
    }

    // 2. Constructor Parameter (Opsional, untuk kemudahan di Test)
    public Perjalanan(UUID userId, String judul) {
        this.userId = userId;
        this.judul = judul;
    }

    // -------------------

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    // --- GETTERS & SETTERS ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getFotoUtama() { return fotoUtama; }
    public void setFotoUtama(String fotoUtama) { this.fotoUtama = fotoUtama; }

    public LocalDate getTanggalMulai() { return tanggalMulai; }
    public void setTanggalMulai(LocalDate tanggalMulai) { this.tanggalMulai = tanggalMulai; }

    public LocalDate getTanggalSelesai() { return tanggalSelesai; }
    public void setTanggalSelesai(LocalDate tanggalSelesai) { this.tanggalSelesai = tanggalSelesai; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}