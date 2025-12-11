package org.delcom.app.configs;

import jakarta.servlet.http.HttpSession;
import org.delcom.app.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthContextTests {

    @Mock
    private HttpSession session; // Kita buat Session palsu (Mock)

    @InjectMocks
    private AuthContext authContext; // Kita masukkan session palsu ke AuthContext

    @BeforeEach
    void setUp() {
        // Inisialisasi Mockito agar @Mock dan @InjectMocks bekerja
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMembuatInstanceKelasAuthContextDenganBenar() {
        User user = new User();
        user.setUsername("testuser");

        // 1. Test setAuthUser
        // Ini tidak akan error lagi karena session-nya sudah ada (walau palsu)
        authContext.setAuthUser(user);

        // Verifikasi bahwa method setAttribute dipanggil di session
        verify(session, times(1)).setAttribute("authUser", user);

        // 2. Test getAuthUser
        // Kita atur agar session palsu mengembalikan user saat diminta
        when(session.getAttribute("authUser")).thenReturn(user);

        User result = authContext.getAuthUser();
        
        // Assertions
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertTrue(authContext.isAuthenticated());
    }
    
    @Test
    void testGetAuthUserWhenNull() {
        // Simulasi session kosong
        when(session.getAttribute("authUser")).thenReturn(null);
        
        assertNull(authContext.getAuthUser());
        assertFalse(authContext.isAuthenticated());
    }
}