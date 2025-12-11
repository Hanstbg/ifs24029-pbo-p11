package org.delcom.app.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

class BucketListTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        // 1. Siapkan Data Dummy
        User mockUser = Mockito.mock(User.class);
        String destination = "Paris";
        String notes = "Visit Eiffel Tower";
        LocalDate targetDate = LocalDate.of(2025, 12, 31);

        // 2. Panggil Constructor Lengkap
        BucketList bucketList = new BucketList(mockUser, destination, notes, targetDate);

        // 3. Panggil Getters
        Assertions.assertEquals(mockUser, bucketList.getUser());
        Assertions.assertEquals(destination, bucketList.getDestination());
        Assertions.assertEquals(notes, bucketList.getNotes());
        Assertions.assertEquals(targetDate, bucketList.getTargetDate());
        
        // Cek default value isAchieved
        Assertions.assertFalse(bucketList.isAchieved());
    }

    @Test
    void testSettersAndGetters() {
        // Test untuk Constructor Kosong dan Setter
        BucketList bucketList = new BucketList();
        
        UUID id = UUID.randomUUID();
        User mockUser = Mockito.mock(User.class);
        String destination = "Bali";
        String notes = "Beach walk";
        LocalDate targetDate = LocalDate.now();
        LocalDateTime manualCreatedAt = LocalDateTime.now(); // Data Dummy Waktu

        // Panggil SEMUA Setter (termasuk setCreatedAt yang sebelumnya merah)
        bucketList.setId(id);
        bucketList.setUser(mockUser);
        bucketList.setDestination(destination);
        bucketList.setNotes(notes);
        bucketList.setTargetDate(targetDate);
        bucketList.setAchieved(true);
        bucketList.setCreatedAt(manualCreatedAt); // <--- INI PERBAIKANNYA

        // Assert Getters
        Assertions.assertEquals(id, bucketList.getId());
        Assertions.assertEquals(mockUser, bucketList.getUser());
        Assertions.assertEquals(destination, bucketList.getDestination());
        Assertions.assertEquals(notes, bucketList.getNotes());
        Assertions.assertEquals(targetDate, bucketList.getTargetDate());
        Assertions.assertTrue(bucketList.isAchieved());
        Assertions.assertEquals(manualCreatedAt, bucketList.getCreatedAt()); // <--- Verifikasi
    }

    @Test
    void testOnCreate() {
        // 1. Buat object
        BucketList bucketList = new BucketList();

        // 2. Pastikan createdAt masih null sebelum onCreate dipanggil
        Assertions.assertNull(bucketList.getCreatedAt());

        // 3. Panggil method onCreate secara manual
        bucketList.onCreate(); 

        // 4. Assert
        Assertions.assertNotNull(bucketList.getCreatedAt());
        Assertions.assertTrue(bucketList.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}