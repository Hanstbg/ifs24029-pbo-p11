package org.delcom.app.controllers;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.BucketList;
import org.delcom.app.entities.User;
import org.delcom.app.services.BucketListService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BucketListControllerTest {

    @Mock
    private BucketListService bucketListService;

    @Mock
    private AuthContext authContext;

    @Mock
    private Model model;

    @InjectMocks
    private BucketListController bucketListController;

    // --- 1. SHOW PAGE ---

    @Test
    void testShowBucketListPage_NotLoggedIn() {
        // Cover baris: if (user == null) return "redirect:/auth/login";
        when(authContext.getAuthUser()).thenReturn(null);

        String view = bucketListController.showBucketListPage(model);

        assertEquals("redirect:/auth/login", view);
    }

    @Test
    void testShowBucketListPage_Success() {
        // Setup User
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(authContext.getAuthUser()).thenReturn(user);
        when(bucketListService.getAllByUser(userId)).thenReturn(new ArrayList<>());

        String view = bucketListController.showBucketListPage(model);

        verify(model).addAttribute(eq("bucketList"), any());
        assertEquals("bucketlist", view); 
    }

    // --- 2. ADD ITEM ---

    @Test
    void testAddItem_NotLoggedIn() {
        // Cover baris: if (user == null) return "redirect:/auth/login";
        when(authContext.getAuthUser()).thenReturn(null);
        String view = bucketListController.addItem("Judul", "Ket", LocalDate.now());
        assertEquals("redirect:/auth/login", view);
    }

    @Test
    void testAddItem_Success() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(authContext.getAuthUser()).thenReturn(user);

        String view = bucketListController.addItem("Naik Gunung", "Gunung Semeru", LocalDate.now());

        verify(bucketListService).addBucketItemObject(any(BucketList.class));
        assertEquals("redirect:/bucketlist", view);
    }

    // --- 3. TOGGLE STATUS ---

    @Test
    void testToggleItemStatus_Success() {
        // Cover cabang TRUE: isAuthenticated() == true
        when(authContext.isAuthenticated()).thenReturn(true);
        
        UUID itemId = UUID.randomUUID();
        String view = bucketListController.toggleItemStatus(itemId);

        verify(bucketListService).toggleStatus(itemId);
        assertEquals("redirect:/bucketlist", view);
    }

    @Test
    void testToggleItemStatus_NotLoggedIn() {
        // [BARU] Cover cabang FALSE: isAuthenticated() == false (Garis Kuning Hilang)
        when(authContext.isAuthenticated()).thenReturn(false);
        
        UUID itemId = UUID.randomUUID();
        String view = bucketListController.toggleItemStatus(itemId);

        // Pastikan service TIDAK dipanggil
        verify(bucketListService, never()).toggleStatus(any());
        assertEquals("redirect:/auth/login", view);
    }

    // --- 4. DELETE ITEM ---

    @Test
    void testDeleteItem_Success() {
        // Cover cabang TRUE: isAuthenticated() == true
        when(authContext.isAuthenticated()).thenReturn(true);
        
        UUID itemId = UUID.randomUUID();
        String view = bucketListController.deleteItem(itemId);

        verify(bucketListService).deleteItem(itemId);
        assertEquals("redirect:/bucketlist", view);
    }

    @Test
    void testDeleteItem_NotLoggedIn() {
        // [BARU] Cover cabang FALSE: isAuthenticated() == false (Garis Kuning Hilang)
        when(authContext.isAuthenticated()).thenReturn(false);
        
        UUID itemId = UUID.randomUUID();
        String view = bucketListController.deleteItem(itemId);

        // Pastikan service TIDAK dipanggil
        verify(bucketListService, never()).deleteItem(any());
        assertEquals("redirect:/auth/login", view);
    }
}