package org.delcom.app.configs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MvcConfigTest {

    @InjectMocks
    private MvcConfig mvcConfig;

    @Mock
    private ResourceHandlerRegistry registry;

    @Mock
    private ResourceHandlerRegistration registration;

    @Test
    void testAddResourceHandlers() {
        // 1. Inject nilai @Value secara manual menggunakan ReflectionTestUtils
        // Ini menggantikan fungsi @Value("${app.upload.dir}") yang tidak jalan di unit test murni
        ReflectionTestUtils.setField(mvcConfig, "uploadDir", "./uploads");

        // 2. Setup Mocking Chaining
        when(registry.addResourceHandler(anyString())).thenReturn(registration);
        when(registration.addResourceLocations(anyString())).thenReturn(registration);

        // 3. Execute
        mvcConfig.addResourceHandlers(registry);

        // 4. Verify
        // Pastikan handler untuk /uploads/** didaftarkan
        verify(registry).addResourceHandler("/uploads/**");
        
        // Pastikan location ditambahkan dan dimulai dengan "file:" (karena .toUri() menghasilkan file:/...)
        verify(registration).addResourceLocations(startsWith("file:"));
    }
}