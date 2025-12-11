package org.delcom.app.entities;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JournalEntryTest {

    @Test
    void testJournalEntrySettersAndGetters() {
        // Test Getter & Setter Biasa
        JournalEntry entry = new JournalEntry();

        UUID id = UUID.randomUUID();
        Perjalanan mockPerjalanan = Mockito.mock(Perjalanan.class);
        String judul = "Pagi di Pantai";
        String catatan = "Jalan-jalan pagi melihat sunrise";
        String lokasi = "Pantai Kuta";
        String fotoPath = "uploads/foto1.jpg";
        LocalDateTime now = LocalDateTime.now();

        entry.setId(id);
        entry.setPerjalanan(mockPerjalanan);
        entry.setJudul(judul);
        entry.setCatatan(catatan);
        entry.setLokasi(lokasi);
        entry.setTanggalAktivitas(now);
        entry.setFotoPath(fotoPath);
        
        assertEquals(id, entry.getId());
        assertEquals(mockPerjalanan, entry.getPerjalanan());
        assertEquals(judul, entry.getJudul());
        assertEquals(catatan, entry.getCatatan());
        assertEquals(lokasi, entry.getLokasi());
        assertEquals(now, entry.getTanggalAktivitas());
        assertEquals(fotoPath, entry.getFotoPath());
    }

    @Test
    void testLifecycleMethods() {
        // --- MENUTUPI GARIS MERAH: onCreate() dan getCreatedAt() ---
        
        // 1. Buat object baru
        JournalEntry entry = new JournalEntry();

        // 2. Pastikan createdAt masih null sebelum disimpan
        assertNull(entry.getCreatedAt());

        // 3. Panggil method onCreate() secara manual
        // Karena kita ada di package yang sama (org.delcom.app.entities),
        // kita bisa akses method protected ini.
        entry.onCreate();

        // 4. Pastikan createdAt sekarang sudah terisi (Tidak Null)
        assertNotNull(entry.getCreatedAt());
        
        // 5. Cek apakah waktunya barusan (opsional, tapi bagus)
        assertTrue(entry.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}