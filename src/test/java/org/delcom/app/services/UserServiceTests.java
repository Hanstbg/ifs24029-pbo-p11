package org.delcom.app.services;

import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // --- 1. Test findByUsername (Method Fix Baru) ---
    @Test
    void testFindByUsername() {
        String username = "hans";
        User mockUser = new User();
        mockUser.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(mockUser);

        User result = userService.findByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository).findByUsername(username);
    }

    // --- 2. Test getUserById (Ada logic .orElse(null)) ---
    @Test
    void testGetUserById_Found() {
        UUID id = UUID.randomUUID();
        User mockUser = new User();
        
        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));

        User result = userService.getUserById(id);
        assertNotNull(result);
    }

    @Test
    void testGetUserById_NotFound() {
        UUID id = UUID.randomUUID();
        
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        User result = userService.getUserById(id);
        assertNull(result); // Harus null sesuai kodingan service
    }

    // --- 3. Test getUserByUsername (Method Lama) ---
    @Test
    void testGetUserByUsername() {
        String username = "test";
        User mockUser = new User();
        
        when(userRepository.findByUsername(username)).thenReturn(mockUser);

        User result = userService.getUserByUsername(username);
        assertNotNull(result);
    }

    // --- 4. Test RegisterUser (Cover Exception & Role Logic) ---
    
    // Case A: Sukses Register (Role Null -> Jadi ROLE_USER)
    // Menghijaukan cabang IF role == null
    @Test
    void testRegisterUser_Success_RoleNull() {
        User newUser = new User();
        newUser.setUsername("baru");
        newUser.setPassword("rawPass");
        newUser.setRole(null); // Role kosong

        when(userRepository.findByUsername("baru")).thenReturn(null); // User belum ada
        when(passwordEncoder.encode("rawPass")).thenReturn("hashedPass");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User result = userService.registerUser(newUser);

        assertNotNull(result);
        assertEquals("ROLE_USER", result.getRole()); // Pastikan role di-set
        assertEquals("hashedPass", result.getPassword()); 
    }

    // Case B: Sukses Register (Role Empty String -> Jadi ROLE_USER)
    // Menghijaukan cabang IF role.isEmpty()
    @Test
    void testRegisterUser_Success_RoleEmpty() {
        User newUser = new User();
        newUser.setUsername("kosong");
        newUser.setPassword("rawPass");
        newUser.setRole(""); // Role string kosong

        when(userRepository.findByUsername("kosong")).thenReturn(null);
        when(passwordEncoder.encode("rawPass")).thenReturn("hashedPass");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User result = userService.registerUser(newUser);

        assertEquals("ROLE_USER", result.getRole());
    }

    // Case C: Sukses Register (Role Sudah Ada -> Tidak ditimpa)
    // Menghijaukan cabang ELSE dari Role check
    @Test
    void testRegisterUser_Success_ExistingRole() {
        User newUser = new User();
        newUser.setUsername("admin");
        newUser.setPassword("rawPass");
        newUser.setRole("ROLE_ADMIN"); // Role sudah ada

        when(userRepository.findByUsername("admin")).thenReturn(null);
        when(passwordEncoder.encode("rawPass")).thenReturn("hashedPass");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User result = userService.registerUser(newUser);

        assertEquals("ROLE_ADMIN", result.getRole()); // Role tidak boleh berubah jadi ROLE_USER
    }

    // Case D: Gagal Register (Username Duplicate) 
    // Menghijaukan cabang IF (found != null) -> Throw Exception
    @Test
    void testRegisterUser_DuplicateUsername() {
        User existingUser = new User();
        existingUser.setUsername("duplikat");

        when(userRepository.findByUsername("duplikat")).thenReturn(new User()); // User sudah ada di DB

        // Pastikan melempar RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(existingUser);
        });

        assertEquals("Username sudah digunakan!", exception.getMessage());
        verify(userRepository, never()).save(any()); // Save tidak boleh dipanggil
    }

    // --- 5. Test CheckPassword ---
    @Test
    void testCheckPassword() {
        String raw = "pass";
        String encoded = "hash";

        when(passwordEncoder.matches(raw, encoded)).thenReturn(true);

        boolean result = userService.checkPassword(raw, encoded);
        assertTrue(result);
    }
}