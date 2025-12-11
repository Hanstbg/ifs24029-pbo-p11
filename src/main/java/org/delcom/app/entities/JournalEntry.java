package org.delcom.app.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "journal_entry")
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perjalanan_id", nullable = false)
    private Perjalanan perjalanan;

    @Column(name = "judul", nullable = false)
    private String judul;

    @Lob
    @Column(name = "catatan", length = 5000)
    private String catatan;

    @Column(name = "lokasi")
    private String lokasi;

    @Column(name = "tanggal_aktivitas")
    private LocalDateTime tanggalAktivitas;

    @Column(name = "foto_path")
    private String fotoPath;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Method ini protected, tapi bisa diakses oleh Test di package yang sama
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public JournalEntry() {}

    // --- GETTERS & SETTERS ---
    
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Perjalanan getPerjalanan() { return perjalanan; }
    public void setPerjalanan(Perjalanan perjalanan) { this.perjalanan = perjalanan; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public LocalDateTime getTanggalAktivitas() { return tanggalAktivitas; }
    public void setTanggalAktivitas(LocalDateTime tanggalAktivitas) { this.tanggalAktivitas = tanggalAktivitas; }

    public String getFotoPath() { return fotoPath; }
    public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}