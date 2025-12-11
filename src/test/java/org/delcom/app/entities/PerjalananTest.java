package org.delcom.app.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PerjalananTest {

    @Test
    void testCustomConstructor() {
        // MENUTUPI GARIS MERAH: Constructor Parameter
        UUID userId = UUID.randomUUID();
        String judul = "Liburan Musim Panas";

        Perjalanan perjalanan = new Perjalanan(userId, judul);

        assertEquals(userId, perjalanan.getUserId());
        assertEquals(judul, perjalanan.getJudul());
    }

    @Test
    void testLifecycleMethods() {
        // MENUTUPI GARIS MERAH: onCreate() dan onUpdate()
        Perjalanan perjalanan = new Perjalanan();

        // 1. Cek sebelum onCreate, tanggal harusnya null (kecuali di-set manual)
        assertNull(perjalanan.getCreatedAt());
        assertNull(perjalanan.getUpdatedAt());

        // 2. Panggil manual method protected onCreate()
        // (Bisa dilakukan karena file test ini ada di package yang sama: org.delcom.app.entities)
        perjalanan.onCreate();

        assertNotNull(perjalanan.getCreatedAt());
        assertNotNull(perjalanan.getUpdatedAt());

        // 3. Panggil manual method protected onUpdate()
        // Kita beri jeda sedikit atau overwrite value lama untuk memastikan update berjalan
        perjalanan.setUpdatedAt(null); 
        perjalanan.onUpdate();

        assertNotNull(perjalanan.getUpdatedAt());
    }

    @Test
    void testGettersAndSetters_Complete() {
        // Memastikan semua getter/setter hijau 100%
        Perjalanan p = new Perjalanan();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDate now = LocalDate.now();
        LocalDateTime dateTime = LocalDateTime.now();

        p.setId(id);
        p.setUserId(userId);
        p.setJudul("Judul Test");
        p.setLokasi("Bandung");
        p.setDeskripsi("Deskripsi Test");
        p.setFotoUtama("img.jpg");
        p.setTanggalMulai(now);
        p.setTanggalSelesai(now.plusDays(3));
        p.setCreatedAt(dateTime);
        p.setUpdatedAt(dateTime);

        assertEquals(id, p.getId());
        assertEquals(userId, p.getUserId());
        assertEquals("Judul Test", p.getJudul());
        assertEquals("Bandung", p.getLokasi());
        assertEquals("Deskripsi Test", p.getDeskripsi());
        assertEquals("img.jpg", p.getFotoUtama());
        assertEquals(now, p.getTanggalMulai());
        assertEquals(now.plusDays(3), p.getTanggalSelesai());
        assertEquals(dateTime, p.getCreatedAt());
        assertEquals(dateTime, p.getUpdatedAt());
    }
}