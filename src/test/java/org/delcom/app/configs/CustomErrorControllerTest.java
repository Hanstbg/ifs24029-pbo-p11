package org.delcom.app.configs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomErrorControllerTest {

    @Mock
    private ErrorAttributes errorAttributes;

    @InjectMocks
    private CustomErrorController customErrorController;

    // --- CASE 1: Client Error (4xx) -> Expect status "fail" ---
    @Test
    void testHandleError_404_Fail() {
        ServletWebRequest mockWebRequest = mock(ServletWebRequest.class);
        
        // Setup Map error 404
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("status", 404);
        errorMap.put("error", "Not Found");
        errorMap.put("path", "/api/test");

        when(errorAttributes.getErrorAttributes(any(WebRequest.class), any(ErrorAttributeOptions.class)))
                .thenReturn(errorMap);

        ResponseEntity<Map<String, Object>> response = customErrorController.handleError(mockWebRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        
        // Verifikasi logika: status >= 500 ? "error" : "fail"
        assertEquals("fail", body.get("status")); 
        assertEquals("Endpoint tidak ditemukan atau terjadi error", body.get("message"));
        assertEquals("/api/test", body.get("path"));
    }

    // --- CASE 2: Server Error (5xx) -> Expect status "error" ---
    @Test
    void testHandleError_500_Error() {
        ServletWebRequest mockWebRequest = mock(ServletWebRequest.class);
        
        // Setup Map error 500
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("status", 500);
        errorMap.put("error", "Internal Server Error");

        when(errorAttributes.getErrorAttributes(any(WebRequest.class), any(ErrorAttributeOptions.class)))
                .thenReturn(errorMap);

        ResponseEntity<Map<String, Object>> response = customErrorController.handleError(mockWebRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        // Verifikasi logika: status >= 500 ? "error" : "fail"
        assertEquals("error", body.get("status"));
    }
}