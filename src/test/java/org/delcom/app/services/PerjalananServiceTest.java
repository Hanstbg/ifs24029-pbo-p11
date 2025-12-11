package org.delcom.app.services;

import org.delcom.app.entities.Perjalanan;
import org.delcom.app.repositories.PerjalananRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils; // Penting untuk memanipulasi field private
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PerjalananServiceTest {

    @InjectMocks
    private PerjalananService perjalananService;

    @Mock
    private PerjalananRepository perjalananRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private MultipartFile multipartFile;

    // --- 1. TEST GET BY ID ---
    @Test
    void testGetPerjalananById_Success() {
        UUID userId = UUID.randomUUID();
        UUID perjalananId = UUID.randomUUID();
        Perjalanan mockPerjalanan = new Perjalanan();
        mockPerjalanan.setId(perjalananId);
        mockPerjalanan.setUserId(userId);
        mockPerjalanan.setJudul("Liburan");

        when(perjalananRepository.findByIdAndUserId(perjalananId, userId))
                .thenReturn(Optional.of(mockPerjalanan));

        Perjalanan result = perjalananService.getPerjalananById(userId, perjalananId);

        assertNotNull(result);
        assertEquals("Liburan", result.getJudul());
    }

    @Test
    void testGetPerjalananById_NotFound() {
        UUID userId = UUID.randomUUID();
        UUID perjalananId = UUID.randomUUID();
        when(perjalananRepository.findByIdAndUserId(perjalananId, userId))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            perjalananService.getPerjalananById(userId, perjalananId);
        });
    }

    // --- 2. TEST TAMBAH DATA ---
    @Test
    void testTambahPerjalanan_Success() {
        UUID userId = UUID.randomUUID();
        Perjalanan input = new Perjalanan();
        input.setJudul("Liburan ke Bali");

        when(perjalananRepository.save(any(Perjalanan.class))).thenReturn(input);

        perjalananService.tambahPerjalanan(userId, input);

        verify(perjalananRepository, times(1)).save(any(Perjalanan.class));
    }

    @Test
    void testTambahPerjalanan_JudulKosong() {
        UUID userId = UUID.randomUUID();
        Perjalanan input = new Perjalanan();
        input.setJudul(""); 
        assertThrows(ResponseStatusException.class, () -> perjalananService.tambahPerjalanan(userId, input));
    }
    
    @Test
    void testTambahPerjalanan_JudulNull() {
        UUID userId = UUID.randomUUID();
        Perjalanan input = new Perjalanan();
        input.setJudul(null); 
        assertThrows(ResponseStatusException.class, () -> perjalananService.tambahPerjalanan(userId, input));
    }

    // --- 3. TEST GET ALL ---
    @Test
    void testGetAllPerjalananByUserId() {
        UUID userId = UUID.randomUUID();
        List<Perjalanan> mockList = new ArrayList<>();
        mockList.add(new Perjalanan());

        when(perjalananRepository.findAllByUserIdOrderByCreatedAtDesc(userId)).thenReturn(mockList);

        List<Perjalanan> result = perjalananService.getAllPerjalananByUserId(userId);

        assertEquals(1, result.size());
    }

    // --- 4. TEST UPDATE DATA ---
    @Test
    void testUbahPerjalanan_Success() {
        UUID userId = UUID.randomUUID();
        UUID id = UUID.randomUUID();

        Perjalanan existing = new Perjalanan();
        existing.setId(id);
        existing.setUserId(userId);

        Perjalanan updateData = new Perjalanan();
        updateData.setJudul("Judul Baru");

        when(perjalananRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(existing));
        when(perjalananRepository.save(any(Perjalanan.class))).thenReturn(existing);

        Perjalanan result = perjalananService.ubahPerjalanan(userId, id, updateData);

        assertEquals("Judul Baru", result.getJudul());
    }

    // --- 5. TEST UPDATE FOTO (COVERAGE FIX) ---
    
    @Test
    void testUbahFotoPerjalanan_FileEmpty() {
        UUID userId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Perjalanan existing = new Perjalanan();

        when(perjalananRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(existing));
        when(multipartFile.isEmpty()).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> {
            perjalananService.ubahFotoPerjalanan(userId, id, multipartFile);
        });
    }

    @Test
    void testUbahFotoPerjalanan_Success() throws IOException {
        UUID userId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Perjalanan existing = new Perjalanan();

        when(perjalananRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(existing));
        when(multipartFile.isEmpty()).thenReturn(false);
        // Pastikan fileStorageService TIDAK null (Default Mockito behavior)
        when(fileStorageService.storeFile(multipartFile, userId)).thenReturn("foto.jpg");
        when(perjalananRepository.save(any(Perjalanan.class))).thenReturn(existing);

        Perjalanan result = perjalananService.ubahFotoPerjalanan(userId, id, multipartFile);

        assertEquals("foto.jpg", result.getFotoUtama());
    }

    /**
     * PERBAIKAN COVERAGE BARIS 77-78
     * Scenario: FileStorageService null (misal bean tidak terload), 
     * maka harus masuk blok 'else' dan pakai dummy-url.
     */
    @Test
    void testUbahFotoPerjalanan_ServiceNull_DummyUrl() {
        UUID userId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Perjalanan existing = new Perjalanan();

        when(perjalananRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(existing));
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("test.png");
        when(perjalananRepository.save(any(Perjalanan.class))).thenAnswer(i -> i.getArguments()[0]);

        // Paksa fileStorageService menjadi null di dalam instance perjalananService
        ReflectionTestUtils.setField(perjalananService, "fileStorageService", null);

        Perjalanan result = perjalananService.ubahFotoPerjalanan(userId, id, multipartFile);

        // Assert masuk ke logika else
        assertNotNull(result.getFotoUtama());
        assertTrue(result.getFotoUtama().contains("dummy-url/test.png"));
        
        // Kembalikan mock agar test lain tidak error (Clean up)
        ReflectionTestUtils.setField(perjalananService, "fileStorageService", fileStorageService);
    }

    /**
     * PERBAIKAN COVERAGE BARIS 83-84
     * Scenario: Exception terjadi saat proses upload
     */
    @Test
    void testUbahFotoPerjalanan_StorageError() throws IOException {
        UUID userId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Perjalanan existing = new Perjalanan();

        when(perjalananRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(existing));
        when(multipartFile.isEmpty()).thenReturn(false);
        
        // Simulasikan Error
        when(fileStorageService.storeFile(multipartFile, userId)).thenThrow(new RuntimeException("IO Error"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            perjalananService.ubahFotoPerjalanan(userId, id, multipartFile);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Gagal upload file"));
    }

    // --- 6. TEST HAPUS PERJALANAN ---
    @Test
    void testHapusPerjalanan() {
        UUID userId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Perjalanan existing = new Perjalanan();

        when(perjalananRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.of(existing));

        perjalananService.hapusPerjalanan(userId, id);

        verify(perjalananRepository, times(1)).delete(existing);
    }

    // --- 7. TEST CHART DATA (COVERAGE FIX FILTER STREAM) ---
    @Test
    void testGetChartDataByLokasi() {
        UUID userId = UUID.randomUUID();
        List<Perjalanan> dataList = new ArrayList<>();
        
        // 1. Data Valid 1
        Perjalanan p1 = new Perjalanan();
        p1.setLokasi("Bali");
        dataList.add(p1);
        
        // 2. Data Valid 2 (Duplicate Location)
        Perjalanan p2 = new Perjalanan();
        p2.setLokasi("Bali");
        dataList.add(p2);
        
        // 3. Data Valid 3
        Perjalanan p3 = new Perjalanan();
        p3.setLokasi("Jakarta");
        dataList.add(p3);

        // 4. Data Lokasi Null (Harus difilter)
        Perjalanan p4 = new Perjalanan();
        p4.setLokasi(null);
        dataList.add(p4);

        // 5. Data Lokasi Empty String (Harus difilter juga agar coverage kuning jadi hijau)
        Perjalanan p5 = new Perjalanan();
        p5.setLokasi("");
        dataList.add(p5);

        when(perjalananRepository.findByUserId(userId)).thenReturn(dataList);

        Map<String, Object> result = perjalananService.getChartDataByLokasi(userId);

        // Verifikasi
        assertNotNull(result);
        assertEquals(5, result.get("total_data")); // Total semua data (termasuk null/empty)
        
        List<Map<String, Object>> chartList = (List<Map<String, Object>>) result.get("data_per_lokasi");
        
        // Harusnya hanya ada 2 entry di chart: Bali dan Jakarta (Null dan Empty dibuang)
        assertEquals(2, chartList.size()); 
        
        // Cek jumlah Bali = 2
        boolean baliCorrect = chartList.stream().anyMatch(m -> m.get("lokasi").equals("Bali") && (Long)m.get("jumlah") == 2);
        assertTrue(baliCorrect, "Jumlah Bali harus 2");
    }
}