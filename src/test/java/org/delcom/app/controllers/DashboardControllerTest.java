package org.delcom.app.controllers;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.BucketList;
import org.delcom.app.entities.Perjalanan;
import org.delcom.app.entities.User;
import org.delcom.app.services.BucketListService;
import org.delcom.app.services.FileStorageService;
import org.delcom.app.services.PerjalananService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private PerjalananService perjalananService;

    @Mock
    private BucketListService bucketListService;

    @Mock
    private AuthContext authContext;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private Model model;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private DashboardController dashboardController;

    // ==========================================
    // 1. TEST METHOD: dashboard()
    // ==========================================

    @Test
    void testDashboard_NotLoggedIn() {
        when(authContext.getAuthUser()).thenReturn(null);
        String view = dashboardController.dashboard(model);
        assertEquals("redirect:/auth/login", view);
    }

    @Test
    void testDashboard_Success_FullData() {
        UUID userId = UUID.randomUUID();
        User user = new User(); user.setId(userId);
        when(authContext.getAuthUser()).thenReturn(user);

        List<Perjalanan> perjalanans = new ArrayList<>();
        perjalanans.add(new Perjalanan());
        when(perjalananService.getAllPerjalananByUserId(userId)).thenReturn(perjalanans);

        List<BucketList> bucketLists = new ArrayList<>();
        when(bucketListService.getAllByUser(userId)).thenReturn(bucketLists);

        Map<String, Object> serviceResult = new HashMap<>();
        List<Map<String, Object>> dataPerLokasi = new ArrayList<>();
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("lokasi", "Jakarta");
        locationData.put("jumlah", 10L);
        dataPerLokasi.add(locationData);

        serviceResult.put("data_per_lokasi", dataPerLokasi);
        serviceResult.put("total_data", 10); 

        when(perjalananService.getChartDataByLokasi(userId)).thenReturn(serviceResult);

        String view = dashboardController.dashboard(model);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("listPerjalanan", perjalanans);
        verify(model).addAttribute("bucketList", bucketLists);
        verify(model).addAttribute("totalPerjalanan", 1);
        verify(model).addAttribute("totalLokasi", 10);
        verify(model).addAttribute(eq("chartLabels"), anyList()); 
        verify(model).addAttribute(eq("chartValues"), anyList());

        assertEquals("view/dashboard", view);
    }

    @Test
    void testDashboard_Success_NullLists() {
        UUID userId = UUID.randomUUID();
        User user = new User(); user.setId(userId);
        when(authContext.getAuthUser()).thenReturn(user);

        when(perjalananService.getAllPerjalananByUserId(userId)).thenReturn(null);
        when(bucketListService.getAllByUser(userId)).thenReturn(null);
        when(perjalananService.getChartDataByLokasi(userId)).thenReturn(null);

        String view = dashboardController.dashboard(model);

        verify(model).addAttribute(eq("listPerjalanan"), eq(new ArrayList<>()));
        verify(model).addAttribute(eq("bucketList"), eq(new ArrayList<>()));
        verify(model).addAttribute("totalLokasi", 0);

        assertEquals("view/dashboard", view);
    }
    
    @Test
    void testDashboard_ChartData_MapExists_KeyMissing() {
        UUID userId = UUID.randomUUID();
        User user = new User(); user.setId(userId);
        when(authContext.getAuthUser()).thenReturn(user);

        Map<String, Object> serviceResult = new HashMap<>();
        when(perjalananService.getChartDataByLokasi(userId)).thenReturn(serviceResult);

        dashboardController.dashboard(model);
        
        verify(model).addAttribute(eq("chartLabels"), eq(new ArrayList<>()));
    }

    @Test
    void testDashboard_ChartData_KeyExists_ValueNull() {
        UUID userId = UUID.randomUUID();
        User user = new User(); user.setId(userId);
        when(authContext.getAuthUser()).thenReturn(user);

        Map<String, Object> serviceResult = new HashMap<>();
        serviceResult.put("data_per_lokasi", null);

        when(perjalananService.getChartDataByLokasi(userId)).thenReturn(serviceResult);

        dashboardController.dashboard(model);
        
        verify(model).addAttribute(eq("chartLabels"), eq(new ArrayList<>()));
    }

    @Test
    void testDashboard_Exception() {
        UUID userId = UUID.randomUUID();
        User user = new User(); user.setId(userId);
        when(authContext.getAuthUser()).thenReturn(user);
        
        when(perjalananService.getAllPerjalananByUserId(userId)).thenThrow(new RuntimeException("DB Error"));

        String view = dashboardController.dashboard(model);
        assertEquals("error", view);
    }

    // ==========================================
    // 2. TEST METHOD: showAddForm() & showEditForm()
    // ==========================================

    @Test
    void testShowAddForm_Success() {
        when(authContext.isAuthenticated()).thenReturn(true);
        String view = dashboardController.showAddForm(model);
        verify(model).addAttribute(eq("perjalanan"), any(Perjalanan.class));
        verify(model).addAttribute("isEdit", false);
        assertEquals("view/form-perjalanan", view);
    }

    @Test
    void testShowAddForm_NotAuthenticated() {
        when(authContext.isAuthenticated()).thenReturn(false);
        String view = dashboardController.showAddForm(model);
        assertEquals("redirect:/auth/login", view);
    }

    @Test
    void testShowEditForm_Success() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = new User(); user.setId(userId);

        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        when(perjalananService.getPerjalananById(userId, id)).thenReturn(new Perjalanan());

        String view = dashboardController.showEditForm(id, model);
        verify(model).addAttribute("isEdit", true);
        assertEquals("view/form-perjalanan", view);
    }

    @Test
    void testShowEditForm_NotAuthenticated() {
        when(authContext.isAuthenticated()).thenReturn(false);
        String view = dashboardController.showEditForm(UUID.randomUUID(), model);
        assertEquals("redirect:/auth/login", view);
    }

    @Test
    void testShowEditForm_NotFound() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = new User(); user.setId(userId);

        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        when(perjalananService.getPerjalananById(userId, id)).thenThrow(new RuntimeException());

        String view = dashboardController.showEditForm(id, model);
        assertEquals("redirect:/dashboard", view);
    }

    // ==========================================
    // 3. TEST METHOD: savePerjalanan()
    // ==========================================

    @Test
    void testSavePerjalanan_New_WithImage() throws IOException {
        UUID userId = UUID.randomUUID();
        User user = new User(); user.setId(userId);
        when(authContext.getAuthUser()).thenReturn(user);

        Perjalanan p = new Perjalanan();
        when(multipartFile.isEmpty()).thenReturn(false); 
        when(fileStorageService.storeFile(multipartFile, userId)).thenReturn("img.jpg");

        String view = dashboardController.savePerjalanan(p, multipartFile, null);

        verify(perjalananService).tambahPerjalanan(eq(userId), any());
        assertEquals("img.jpg", p.getFotoUtama());
        assertEquals("redirect:/dashboard", view);
    }

    @Test
    void testSavePerjalanan_FilePresent_ButEmpty_ThenUrlValid() throws IOException {
        UUID userId = UUID.randomUUID();
        User user = new User(); user.setId(userId);
        when(authContext.getAuthUser()).thenReturn(user);

        Perjalanan p = new Perjalanan();
        String urlInput = "http://valid-url.com/pic.png";

        when(multipartFile.isEmpty()).thenReturn(true); 

        String view = dashboardController.savePerjalanan(p, multipartFile, urlInput);

        assertEquals(urlInput, p.getFotoUtama());
        // Verify ini memaksa kita menambahkan 'throws IOException'
        verify(fileStorageService, never()).storeFile(any(), any()); 
        assertEquals("redirect:/dashboard", view);
    }

    @Test
    void testSavePerjalanan_NoImage_WithUrl() {
        UUID userId = UUID.randomUUID();
        User user = new User(); user.setId(userId);
        when(authContext.getAuthUser()).thenReturn(user);

        Perjalanan p = new Perjalanan();
        p.setId(UUID.randomUUID()); 
        String url = "http://img.com/a.jpg";

        String view = dashboardController.savePerjalanan(p, null, url);

        verify(perjalananService).ubahPerjalanan(eq(userId), eq(p.getId()), any());
        assertEquals(url, p.getFotoUtama());
        assertEquals("redirect:/dashboard", view);
    }
    
    @Test
    void testSavePerjalanan_NoImage_UrlWhitespace() {
        UUID userId = UUID.randomUUID();
        User user = new User(); user.setId(userId);
        when(authContext.getAuthUser()).thenReturn(user);

        Perjalanan p = new Perjalanan();
        
        String view = dashboardController.savePerjalanan(p, null, "   "); 

        verify(perjalananService).tambahPerjalanan(eq(userId), any());
        assertEquals(null, p.getFotoUtama());
        assertEquals("redirect:/dashboard", view);
    }

    @Test
    void testSavePerjalanan_NotLoggedIn() {
        when(authContext.getAuthUser()).thenReturn(null);
        String view = dashboardController.savePerjalanan(new Perjalanan(), null, null);
        assertEquals("redirect:/auth/login", view);
    }

    @Test
    void testSavePerjalanan_Exception() {
        UUID userId = UUID.randomUUID();
        User user = new User(); user.setId(userId);
        when(authContext.getAuthUser()).thenReturn(user);
        doThrow(new RuntimeException()).when(perjalananService).tambahPerjalanan(any(), any());

        String view = dashboardController.savePerjalanan(new Perjalanan(), null, null);
        assertEquals("redirect:/dashboard", view);
    }

    // ==========================================
    // 4. TEST METHOD: deletePerjalanan()
    // ==========================================

    @Test
    void testDeletePerjalanan_Success() {
        UUID userId = UUID.randomUUID();
        User user = new User(); user.setId(userId);
        when(authContext.getAuthUser()).thenReturn(user);
        
        UUID pid = UUID.randomUUID();
        String view = dashboardController.deletePerjalanan(pid);

        verify(perjalananService).hapusPerjalanan(userId, pid);
        assertEquals("redirect:/dashboard", view);
    }
    
    @Test
    void testDeletePerjalanan_NotLoggedIn() {
        when(authContext.getAuthUser()).thenReturn(null);
        
        UUID pid = UUID.randomUUID();
        String view = dashboardController.deletePerjalanan(pid);

        verify(perjalananService, never()).hapusPerjalanan(any(), any());
        assertEquals("redirect:/dashboard", view);
    }
}