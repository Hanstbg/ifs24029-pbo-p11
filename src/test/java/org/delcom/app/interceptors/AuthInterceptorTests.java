package org.delcom.app.interceptors;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.User;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorTests {

    @Mock
    private AuthContext authContext;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthInterceptor authInterceptor;

    // --- 1. Session Ada (Langsung Masuk) ---
    @Test
    void testPreHandle_SessionExists() throws Exception {
        when(authContext.getAuthUser()).thenReturn(new User());

        boolean result = authInterceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verifyNoInteractions(userService); // Tidak perlu cek cookie/db
    }

    // --- 2. Session Kosong, Cookie Kosong (Redirect Login) ---
    @Test
    void testPreHandle_NoSession_NoCookies() throws Exception {
        when(authContext.getAuthUser()).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        boolean result = authInterceptor.preHandle(request, response, new Object());

        assertFalse(result);
        verify(response).sendRedirect("/auth/login");
    }

    // --- 3. Session Kosong, Cookie Ada tapi BUKAN AUTH_TOKEN (Cover Loop) ---
    @Test
    void testPreHandle_CookieNameMismatch() throws Exception {
        when(authContext.getAuthUser()).thenReturn(null);
        
        // Cookie namanya beda
        Cookie[] cookies = { new Cookie("JSESSIONID", "12345") };
        when(request.getCookies()).thenReturn(cookies);

        boolean result = authInterceptor.preHandle(request, response, new Object());

        assertFalse(result); // Token tetap null
        verify(response).sendRedirect("/auth/login");
    }

    // --- 4. Sukses Login via Cookie (Token Valid) ---
    @Test
    void testPreHandle_ValidCookie_UserFound() throws Exception {
        when(authContext.getAuthUser()).thenReturn(null);

        Cookie[] cookies = { new Cookie("AUTH_TOKEN", "valid_token") };
        when(request.getCookies()).thenReturn(cookies);

        UUID userId = UUID.randomUUID();
        User mockUser = new User();

        // MOCK STATIC JwtUtil untuk return UUID sukses
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken("valid_token"))
                       .thenReturn(userId);

            when(userService.getUserById(userId)).thenReturn(mockUser);

            boolean result = authInterceptor.preHandle(request, response, new Object());

            assertTrue(result);
            verify(authContext).setAuthUser(mockUser); // Pastikan user dikembalikan ke session
        }
    }

    // --- 5. Validasi Token Sukses, Tapi User Tidak Ada di DB ---
    @Test
    void testPreHandle_ValidCookie_UserNotFound() throws Exception {
        when(authContext.getAuthUser()).thenReturn(null);
        Cookie[] cookies = { new Cookie("AUTH_TOKEN", "valid_token") };
        when(request.getCookies()).thenReturn(cookies);

        UUID userId = UUID.randomUUID();

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken("valid_token"))
                       .thenReturn(userId);

            when(userService.getUserById(userId)).thenReturn(null); // User di DB null

            boolean result = authInterceptor.preHandle(request, response, new Object());

            assertFalse(result);
            verify(response).sendRedirect("/auth/login");
        }
    }

    // --- 6. EXCEPTION HANDLING (Cover Catch Block yang Merah) ---
    @Test
    void testPreHandle_Exception_CatchBlock() throws Exception {
        when(authContext.getAuthUser()).thenReturn(null);

        Cookie[] cookies = { new Cookie("AUTH_TOKEN", "invalid_token") };
        when(request.getCookies()).thenReturn(cookies);

        // MOCK STATIC JwtUtil untuk MELEMPAR EXCEPTION
        // Ini akan memaksa kode masuk ke blok catch (Exception e)
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(anyString()))
                       .thenThrow(new RuntimeException("Token Expired or Invalid"));

            boolean result = authInterceptor.preHandle(request, response, new Object());

            // Verifikasi
            // 1. Masuk catch -> print error -> loop selesai
            // 2. Lanjut ke baris response.sendRedirect
            assertFalse(result);
            verify(response).sendRedirect("/auth/login");
        }
    }
}