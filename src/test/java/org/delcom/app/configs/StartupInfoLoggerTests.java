package org.delcom.app.configs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartupInfoLoggerTests {

    @Mock
    private ApplicationReadyEvent event;

    @Mock
    private ConfigurableApplicationContext applicationContext;

    @Mock
    private ConfigurableEnvironment env;

    @InjectMocks
    private StartupInfoLogger startupInfoLogger;

    @BeforeEach
    void setUp() {
        // Setup dasar rantai pemanggilan
        when(event.getApplicationContext()).thenReturn(applicationContext);
        when(applicationContext.getEnvironment()).thenReturn(env);
    }

    // --- TEST 1: Context Path NULL ---
    @Test
    void testOnApplicationEvent_ContextPathNull() {
        // 1. Mock Server Port
        when(env.getProperty(eq("server.port"), anyString())).thenReturn("8080");
        
        // 2. Mock Context Path (Target Test: NULL)
        when(env.getProperty(eq("server.servlet.context-path"), anyString())).thenReturn(null);

        // 3. Mock LiveReload Enabled
        when(env.getProperty(eq("spring.devtools.livereload.enabled"), eq(Boolean.class), eq(false))).thenReturn(false);
        
        // 4. Mock LiveReload Port (INI YANG SEBELUMNYA KURANG)
        when(env.getProperty(eq("spring.devtools.livereload.port"), anyString())).thenReturn("35729");

        // 5. Mock Server Address
        when(env.getProperty(eq("server.address"), anyString())).thenReturn("localhost");

        // Execute
        startupInfoLogger.onApplicationEvent(event);

        // Verify
        verify(env).getProperty(eq("server.servlet.context-path"), anyString());
    }

    // --- TEST 2: Context Path "/" ---
    @Test
    void testOnApplicationEvent_ContextPathSlash() {
        // 1. Mock Server Port
        when(env.getProperty(eq("server.port"), anyString())).thenReturn("8080");
        
        // 2. Mock Context Path (Target Test: "/")
        when(env.getProperty(eq("server.servlet.context-path"), anyString())).thenReturn("/");

        // 3. Mock LiveReload Enabled
        when(env.getProperty(eq("spring.devtools.livereload.enabled"), eq(Boolean.class), eq(false))).thenReturn(false);

        // 4. Mock LiveReload Port (INI YANG SEBELUMNYA KURANG)
        when(env.getProperty(eq("spring.devtools.livereload.port"), anyString())).thenReturn("35729");

        // 5. Mock Server Address
        when(env.getProperty(eq("server.address"), anyString())).thenReturn("localhost");

        // Execute
        startupInfoLogger.onApplicationEvent(event);
        
        verify(env).getProperty(eq("server.servlet.context-path"), anyString());
    }

    // --- TEST 3: Context Path Normal & LiveReload Enabled ---
    @Test
    void testOnApplicationEvent_CustomPath_LiveReloadEnabled() {
        // 1. Mock Server Port
        when(env.getProperty(eq("server.port"), anyString())).thenReturn("9090");
        
        // 2. Mock Context Path
        when(env.getProperty(eq("server.servlet.context-path"), anyString())).thenReturn("/api");

        // 3. Mock LiveReload Enabled (TRUE)
        when(env.getProperty(eq("spring.devtools.livereload.enabled"), eq(Boolean.class), eq(false))).thenReturn(true);
        
        // 4. Mock LiveReload Port
        when(env.getProperty(eq("spring.devtools.livereload.port"), anyString())).thenReturn("1234");
        
        // 5. Mock Server Address
        when(env.getProperty(eq("server.address"), anyString())).thenReturn("127.0.0.1");

        // Execute
        startupInfoLogger.onApplicationEvent(event);

        verify(env).getProperty(eq("spring.devtools.livereload.enabled"), eq(Boolean.class), eq(false));
    }
}