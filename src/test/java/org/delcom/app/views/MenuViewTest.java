package org.delcom.app.views;

import org.delcom.app.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuViewTest {

    @InjectMocks
    private MenuView menuView;

    @Mock
    private Model model;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // PENTING: Kita atur SecurityContextHolder agar menggunakan Mock kita
        // Ini memanipulasi static method SecurityContextHolder.getContext()
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        // PENTING: Bersihkan context setelah test selesai agar tidak mengganggu test lain
        SecurityContextHolder.clearContext();
    }

    // --- 1. User Belum Login (Authentication Null) ---
    @Test
    void testMenu_NotLoggedIn_NullAuth() {
        // Simulasi context mengembalikan null saat getAuthentication()
        when(securityContext.getAuthentication()).thenReturn(null);

        String view = menuView.menu(model);

        // Harus redirect ke logout
        assertEquals("redirect:/auth/logout", view);
    }

    // --- 2. User Anonymous (Belum Login Resmi) ---
    @Test
    void testMenu_AnonymousUser() {
        // Simulasi token anonim (seperti user tamu/internet public)
        AnonymousAuthenticationToken anonymousToken = mock(AnonymousAuthenticationToken.class);
        when(securityContext.getAuthentication()).thenReturn(anonymousToken);

        String view = menuView.menu(model);

        assertEquals("redirect:/auth/logout", view);
    }

    // --- 3. User Login tapi Tipe Salah (Bukan User Entity Kita) ---
    @Test
    void testMenu_WrongPrincipalType() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        // Principal mengembalikan String biasa, bukan object User entity
        when(authentication.getPrincipal()).thenReturn("UserStringBiasa");

        String view = menuView.menu(model);

        assertEquals("redirect:/auth/logout", view);
    }

    // --- 4. User Login Sukses (Happy Path) ---
    @Test
    void testMenu_Success() {
        // Siapkan User Entity
        User appUser = new User();
        appUser.setUsername("hans");
        // appUser.setId(java.util.UUID.randomUUID()); // Uncomment jika perlu ID

        // Setup Mocking agar tembus semua validasi
        when(securityContext.getAuthentication()).thenReturn(authentication);
        // Pastikan getPrincipal mengembalikan object User
        when(authentication.getPrincipal()).thenReturn(appUser);

        // Jalankan method
        String view = menuView.menu(model);

        // Verifikasi
        verify(model).addAttribute("auth", appUser); // Pastikan user dikirim ke view
        assertEquals("models/home", view); // Pastikan nama view benar
    }
}