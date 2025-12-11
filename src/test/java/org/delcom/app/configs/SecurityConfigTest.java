package org.delcom.app.configs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Mock
    private HttpSecurity http;

    @Test
    void testPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSecurityFilterChain() throws Exception {
        // 1. Mock chaining HttpSecurity
        when(http.csrf(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.formLogin(any())).thenReturn(http);
        when(http.logout(any())).thenReturn(http);
        when(http.build()).thenReturn(mock(DefaultSecurityFilterChain.class));

        // 2. Mock CSRF Lambda
        when(http.csrf(any(Customizer.class))).thenAnswer(invocation -> {
            Customizer<CsrfConfigurer<HttpSecurity>> customizer = invocation.getArgument(0);
            CsrfConfigurer<HttpSecurity> csrfConfig = mock(CsrfConfigurer.class);
            customizer.customize(csrfConfig);
            verify(csrfConfig).disable();
            return http;
        });

        // 3. Mock FormLogin Lambda
        when(http.formLogin(any(Customizer.class))).thenAnswer(invocation -> {
            Customizer<FormLoginConfigurer<HttpSecurity>> customizer = invocation.getArgument(0);
            FormLoginConfigurer<HttpSecurity> formLogin = mock(FormLoginConfigurer.class);
            customizer.customize(formLogin);
            verify(formLogin).disable();
            return http;
        });

        // 4. Mock Logout Lambda
        when(http.logout(any(Customizer.class))).thenAnswer(invocation -> {
            Customizer<LogoutConfigurer<HttpSecurity>> customizer = invocation.getArgument(0);
            LogoutConfigurer<HttpSecurity> logout = mock(LogoutConfigurer.class);
            customizer.customize(logout);
            verify(logout).disable();
            return http;
        });

        // 5. Mock AuthorizeHttpRequests Lambda (PERBAIKAN DI SINI)
        when(http.authorizeHttpRequests(any(Customizer.class))).thenAnswer(invocation -> {
            Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> customizer = invocation.getArgument(0);
            
            // Mock Registry
            var registry = mock(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry.class);
            
            // Mock AuthorizedUrl (Objek antara sebelum permitAll)
            var authorizedUrl = mock(AuthorizeHttpRequestsConfigurer.AuthorizedUrl.class);
            
            // --- BAGIAN PENTING YANG MEMPERBAIKI NPE ---
            // Agar chaining .permitAll().requestMatchers() bisa jalan,
            // permitAll() harus mengembalikan registry kembali.
            when(authorizedUrl.permitAll()).thenReturn(registry);
            // -------------------------------------------

            // Setup registry behavior
            when(registry.requestMatchers(any(String[].class))).thenReturn(authorizedUrl);
            when(registry.anyRequest()).thenReturn(authorizedUrl); // Untuk .anyRequest().permitAll()

            // Jalankan kode di dalam lambda SecurityConfig
            customizer.customize(registry);

            // Verifikasi
            verify(registry, atLeastOnce()).requestMatchers(any(String[].class));
            verify(registry).anyRequest();
            verify(authorizedUrl, atLeastOnce()).permitAll();
            
            return http;
        });

        // --- Execute ---
        SecurityFilterChain chain = securityConfig.securityFilterChain(http);

        // --- Verify ---
        assertNotNull(chain);
        verify(http).build();
    }
}