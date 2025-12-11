package org.delcom.app.interceptors;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.User;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthContext authContext;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // 1. CEK SESSION (Cara Paling Cepat)
        // Jika user sudah login barusan, session pasti ada.
        if (authContext.getAuthUser() != null) {
            return true; // Lanjut, boleh masuk
        }

        // 2. CEK COOKIE (Auto Login / Remember Me)
        // Jika session kosong (misal browser baru dibuka ulang), kita cek apakah ada Cookie "AUTH_TOKEN"
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("AUTH_TOKEN".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // Jika Token Ditemukan, kita validasi
        if (token != null) {
            try {
                // Ambil ID User dari Token
                UUID userId = JwtUtil.getUserIdFromToken(token);
                
                // Ambil Data User lengkap dari Database
                User user = userService.getUserById(userId);

                if (user != null) {
                    // PENTING: Masukkan kembali user ke Session
                    authContext.setAuthUser(user);
                    return true; // Token valid, user dipulihkan, boleh masuk
                }
            } catch (Exception e) {
                // Token tidak valid atau expired, abaikan saja
                System.out.println("Token invalid: " + e.getMessage());
            }
        }

        // 3. JIKA GAGAL SEMUA (Session kosong, Cookie kosong/invalid)
        // Lempar ke halaman login
        response.sendRedirect("/auth/login");
        return false; // Stop, jangan lanjut ke Controller
    }
}