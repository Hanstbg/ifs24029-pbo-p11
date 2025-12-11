package org.delcom.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.Perjalanan;
import org.delcom.app.entities.User;
import org.delcom.app.services.PerjalananService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PerjalananControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PerjalananService perjalananService;

    @Mock
    private AuthContext authContext;

    @InjectMocks
    private PerjalananController perjalananController;

    private ObjectMapper objectMapper;

    // ID Dummy
    private final UUID DUMMY_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    
    private Perjalanan perjalanan;
    private UUID perjalananId;

    @BeforeEach
    void setUp() {
        // Setup Standalone MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(perjalananController).build();

        // Setup ObjectMapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Setup Data Dummy User
        User dummyUser = new User();
        dummyUser.setId(DUMMY_USER_ID);
        dummyUser.setUsername("testUser");

        // Default: User Login (Happy Path)
        // Gunakan lenient() agar bisa di-override di test Unauthorized
        org.mockito.Mockito.lenient().when(authContext.getAuthUser()).thenReturn(dummyUser);

        // Setup Data Dummy Perjalanan
        perjalananId = UUID.randomUUID();
        perjalanan = new Perjalanan();
        perjalanan.setId(perjalananId);
        perjalanan.setUserId(DUMMY_USER_ID);
        perjalanan.setJudul("Liburan ke Bali");
        perjalanan.setLokasi("Bali");
        perjalanan.setTanggalMulai(LocalDate.now());
        perjalanan.setDeskripsi("Sangat menyenangkan");
    }

    // --- EXISTING TESTS (CRUD) ---

    @Test
    void testCreatePerjalanan() throws Exception {
        when(perjalananService.tambahPerjalanan(eq(DUMMY_USER_ID), any(Perjalanan.class)))
                .thenReturn(perjalanan);

        mockMvc.perform(post("/api/perjalanan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(perjalanan)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.judul").value("Liburan ke Bali"));
    }

    @Test
    void testGetAllPerjalanan() throws Exception {
        List<Perjalanan> list = Arrays.asList(perjalanan);
        
        when(perjalananService.getAllPerjalananByUserId(DUMMY_USER_ID))
                .thenReturn(list);

        mockMvc.perform(get("/api/perjalanan")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].judul").value("Liburan ke Bali"));
    }

    @Test
    void testGetDetailPerjalanan() throws Exception {
        when(perjalananService.getPerjalananById(DUMMY_USER_ID, perjalananId))
                .thenReturn(perjalanan);

        mockMvc.perform(get("/api/perjalanan/{id}", perjalananId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(perjalananId.toString()));
    }

    @Test
    void testUpdatePerjalanan() throws Exception {
        Perjalanan updateData = new Perjalanan();
        updateData.setJudul("Liburan ke Jepang");

        Perjalanan updatedPerjalanan = new Perjalanan();
        updatedPerjalanan.setId(perjalananId);
        updatedPerjalanan.setJudul("Liburan ke Jepang");

        when(perjalananService.ubahPerjalanan(eq(DUMMY_USER_ID), eq(perjalananId), any(Perjalanan.class)))
                .thenReturn(updatedPerjalanan);

        mockMvc.perform(put("/api/perjalanan/{id}", perjalananId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.judul").value("Liburan ke Jepang"));
    }

    @Test
    void testDeletePerjalanan() throws Exception {
        doNothing().when(perjalananService).hapusPerjalanan(DUMMY_USER_ID, perjalananId);

        mockMvc.perform(delete("/api/perjalanan/{id}", perjalananId))
                .andExpect(status().isNoContent());
    }

    // --- NEW TESTS (UNTUK MENGHIJAUKAN COVERAGE) ---

    // 1. Test Upload Foto (Menutupi uploadFotoUtama)
    @Test
    void testUploadFotoUtama() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "foto.jpg", 
                MediaType.IMAGE_JPEG_VALUE, 
                "test image content".getBytes()
        );

        when(perjalananService.ubahFotoPerjalanan(eq(DUMMY_USER_ID), eq(perjalananId), any()))
                .thenReturn(perjalanan);

        mockMvc.perform(multipart("/api/perjalanan/{id}/upload-foto", perjalananId)
                .file(file))
                .andExpect(status().isOk());
    }

    // 2. Test Get Lokasi Chart Data (Menutupi getLokasiChartData)
    @Test
    void testGetLokasiChartData() throws Exception {
        // [PERBAIKAN] Menggunakan <String, Object> agar kompatibel dengan return type Service
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("Bali", 5L);
        mockData.put("Jepang", 2L);

        when(perjalananService.getChartDataByLokasi(DUMMY_USER_ID)).thenReturn(mockData);

        mockMvc.perform(get("/api/perjalanan/chart/lokasi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Bali").value(5))
                .andExpect(jsonPath("$.Jepang").value(2));
    }

    // 3. Test Unauthorized Access (Menutupi if (user == null) di getCurrentUserId)
    @Test
    void testUnauthorizedAccess() throws Exception {
        // Override mock: User belum login (return null)
        when(authContext.getAuthUser()).thenReturn(null);

        // Panggil salah satu endpoint yang butuh login
        mockMvc.perform(get("/api/perjalanan"))
                .andExpect(status().isUnauthorized()) // Harapannya 401
                .andExpect(status().reason("User belum login")); // Cek pesan error
    }
}