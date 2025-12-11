package org.delcom.app.controllers;

import jakarta.servlet.http.HttpSession;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.User;
import org.delcom.app.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthContext authContext;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthController authController;

    // --- 1. LOGIN FORM ---

    @Test
    void testShowLoginForm_NotAuthenticated() {
        when(authContext.isAuthenticated()).thenReturn(false);
        String view = authController.showLoginForm();
        assertEquals("auth/login", view);
    }

    @Test
    void testShowLoginForm_AlreadyAuthenticated() {
        when(authContext.isAuthenticated()).thenReturn(true);
        String view = authController.showLoginForm();
        assertEquals("redirect:/dashboard", view);
    }

    // --- 2. PROCESS LOGIN ---

    @Test
    void testProcessLogin_Success() {
        // Setup Data
        String username = "hans";
        String rawPassword = "password123";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword("hashed_password"); // Password di DB ter-hash

        // 1. Mock User ketemu
        when(userService.getUserByUsername(username)).thenReturn(mockUser);
        
        // 2. Mock Password Match (PENTING: Sesuaikan dengan logika controller baru)
        when(userService.checkPassword(rawPassword, "hashed_password")).thenReturn(true);

        // Action
        String view = authController.processLogin(username, rawPassword, redirectAttributes);

        // Verify
        verify(authContext).setAuthUser(mockUser); // Pastikan user disimpan ke session context
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
        assertEquals("redirect:/dashboard", view);
    }

    @Test
    void testProcessLogin_UserNotFound() {
        String username = "unknown";
        String password = "pwd";

        when(userService.getUserByUsername(username)).thenReturn(null);

        String view = authController.processLogin(username, password, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
        assertEquals("redirect:/auth/login", view);
    }

    @Test
    void testProcessLogin_WrongPassword() {
        String username = "hans";
        String rawPassword = "salah";
        User mockUser = new User();
        mockUser.setPassword("hashed_real_password");

        when(userService.getUserByUsername(username)).thenReturn(mockUser);
        
        // Mock Password TIDAK Match
        when(userService.checkPassword(rawPassword, "hashed_real_password")).thenReturn(false);

        String view = authController.processLogin(username, rawPassword, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
        verify(authContext, never()).setAuthUser(any()); // Pastikan tidak login
        assertEquals("redirect:/auth/login", view);
    }

    // --- 3. REGISTER FORM ---

    @Test
    void testShowRegisterForm_Success() {
        when(authContext.isAuthenticated()).thenReturn(false);
        
        String view = authController.showRegisterForm(model);
        
        verify(model).addAttribute(eq("user"), any(User.class));
        assertEquals("auth/register", view);
    }

    @Test
    void testShowRegisterForm_AlreadyAuthenticated() {
        when(authContext.isAuthenticated()).thenReturn(true);
        String view = authController.showRegisterForm(model);
        assertEquals("redirect:/dashboard", view);
    }

    // --- 4. PROCESS REGISTER ---

    @Test
    void testProcessRegister_Success() {
        User newUser = new User();
        
        // Action
        String view = authController.processRegister(newUser, redirectAttributes);

        // Verify service dipanggil
        verify(userService).registerUser(newUser);
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
        assertEquals("redirect:/auth/login", view);
    }

    @Test
    void testProcessRegister_Fail_Exception() {
        User newUser = new User();

        // Paksa error (misal username duplicate)
        doThrow(new RuntimeException("Username exist")).when(userService).registerUser(any());

        String view = authController.processRegister(newUser, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Gagal"));
        assertEquals("redirect:/auth/register", view);
    }

    // --- 5. LOGOUT ---

    @Test
    void testLogout() {
        String view = authController.logout(session, redirectAttributes);

        verify(session).invalidate(); // Pastikan session dihapus
        verify(redirectAttributes).addFlashAttribute(eq("info"), anyString());
        assertEquals("redirect:/auth/login", view);
    }
}