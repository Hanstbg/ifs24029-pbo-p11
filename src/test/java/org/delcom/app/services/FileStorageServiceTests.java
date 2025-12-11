package org.delcom.app.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTests {

    @InjectMocks
    private FileStorageService fileStorageService;

    // JUnit 5 akan membuat folder sementara yang otomatis bersih setelah test
    @TempDir
    Path tempDir;

    // --- TEST 1: Normal Upload & Create Directory (Logic Baris 21-23) ---
    // Menghijaukan IF (!Files.exists) -> TRUE
    @Test
    void testStoreFile_CreateDirectory() throws IOException {
        Path nonExistentDir = tempDir.resolve("new_uploads");
        String uploadDirPath = nonExistentDir.toAbsolutePath().toString();

        ReflectionTestUtils.setField(fileStorageService, "uploadDir", uploadDirPath);

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "content".getBytes()
        );

        String filename = fileStorageService.storeFile(file, UUID.randomUUID());

        assertNotNull(filename);
        assertTrue(Files.exists(nonExistentDir)); // Direktori berhasil dibuat
        assertTrue(Files.exists(nonExistentDir.resolve(filename)));
    }

    // --- TEST 2: Cek Ekstensi File & Directory Sudah Ada ---
    // Menghijaukan IF (!Files.exists) -> FALSE (karena tempDir sudah ada)
    // Menghijaukan IF (contains(".")) -> TRUE dan FALSE
    @Test
    void testStoreFile_ExtensionLogic() throws IOException {
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());

        // Case A: File punya ekstensi (.png)
        MockMultipartFile fileWithExt = new MockMultipartFile("f", "icon.png", "image/png", "x".getBytes());
        String res1 = fileStorageService.storeFile(fileWithExt, UUID.randomUUID());
        assertTrue(res1.endsWith(".png"));

        // Case B: File tanpa ekstensi
        MockMultipartFile fileNoExt = new MockMultipartFile("f", "readme", "text/plain", "x".getBytes());
        String res2 = fileStorageService.storeFile(fileNoExt, UUID.randomUUID());
        // Nama file generate adalah "img_UUID_Time", tidak ada titik jika ekstensi kosong
        assertFalse(res2.contains(".")); 
    }

    // --- TEST 3: Null Filename (PENTING untuk Branch Coverage) ---
    // Menghijaukan IF (originalFilename != null) -> FALSE
    @Test
    void testStoreFile_NullFilename() throws IOException {
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());

        // Mock MultipartFile agar mengembalikan NULL saat getOriginalFilename dipanggil
        MockMultipartFile file = mock(MockMultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(null);
        when(file.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("test".getBytes()));

        String result = fileStorageService.storeFile(file, UUID.randomUUID());

        // Hasil harusnya tersimpan tanpa error, dan tanpa ekstensi
        assertNotNull(result);
        assertFalse(result.contains("."));
    }

    // --- TEST 4: Delete File Sukses ---
    @Test
    void testDeleteFile_Success() throws IOException {
        Path filePath = tempDir.resolve("gambar_hapus.jpg");
        Files.createFile(filePath);

        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());

        boolean result = fileStorageService.deleteFile("gambar_hapus.jpg");

        assertTrue(result);
        assertFalse(Files.exists(filePath));
    }

    // --- TEST 5: Delete File Exception (Catch Block) ---
    // Menghijaukan blok CATCH IOException
    @Test
    void testDeleteFile_Exception() {
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());

        // MOCK STATIC: Memaksa Files.deleteIfExists melempar error
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            
            // Kita harus mengizinkan calls lain (seperti Paths.get) berjalan normal jika perlu,
            // tapi karena deleteFile memanggil Paths.get dulu baru Files.deleteIfExists, aman.
            
            mockedFiles.when(() -> Files.deleteIfExists(any(Path.class)))
                       .thenThrow(new IOException("Disk Error"));

            boolean result = fileStorageService.deleteFile("error.jpg");

            assertFalse(result); // Harus false karena masuk catch
        }
    }

    // --- TEST 6: Load File & Check Exists ---
    @Test
    void testLoadAndCheckFile() throws IOException {
        Path filePath = tempDir.resolve("cek.txt");
        Files.createFile(filePath);

        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());

        // Test File Exists
        assertTrue(fileStorageService.fileExists("cek.txt"));
        assertFalse(fileStorageService.fileExists("ga_ada.txt"));

        // Test Load File
        Path loadedPath = fileStorageService.loadFile("cek.txt");
        assertNotNull(loadedPath);
        assertEquals(filePath.toAbsolutePath().toString(), loadedPath.toAbsolutePath().toString());
    }
}