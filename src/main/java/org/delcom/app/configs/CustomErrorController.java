package org.delcom.app.configs;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(ServletWebRequest webRequest) {
        // Ambil atribut error dari request
        Map<String, Object> attributes = errorAttributes
                .getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());

        // Ambil status dan path, default ke 500/unknown
        int status = (int) attributes.getOrDefault("status", 500);
        String path = (String) attributes.getOrDefault("path", "unknown");

        // Format body respons sesuai standar API (misalnya JSend-like structure)
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status >= 500 ? "error" : "fail", // Menggunakan "fail" untuk 4xx, "error" untuk 5xx
                "error", attributes.getOrDefault("error", "Unknown Error"),
                "message", "Endpoint tidak ditemukan atau terjadi error",
                "path", path);

        // Kembalikan respons dengan HttpStatus yang sesuai
        return new ResponseEntity<>(body, HttpStatus.valueOf(status));
    }
}