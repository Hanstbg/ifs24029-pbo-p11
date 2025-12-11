package org.delcom.app.controllers;

import jakarta.servlet.http.HttpSession;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.User;
import org.delcom.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthContext authContext;

    @GetMapping("/login")
    public String showLoginForm() {
        if (authContext.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username, 
                               @RequestParam String password, 
                               RedirectAttributes redirectAttributes) {
        
        User user = userService.getUserByUsername(username);

        // --- UPDATE LOGIKA CEK PASSWORD ---
        // 1. Cek User null?
        // 2. Cek Password cocok dengan hash di DB?
        if (user == null || !userService.checkPassword(password, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Username atau Password salah! ❌");
            return "redirect:/auth/login";
        }

        // Login Sukses -> Simpan ke Session Manual
        authContext.setAuthUser(user);
        
        redirectAttributes.addFlashAttribute("success", "Selamat datang, " + user.getUsername() + "! ✈️");
        return "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (authContext.isAuthenticated()) return "redirect:/dashboard";
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(user); // Password akan di-enkripsi di service
            redirectAttributes.addFlashAttribute("success", "Registrasi berhasil! Silakan login.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal: " + e.getMessage());
            return "redirect:/auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("info", "Berhasil logout.");
        return "redirect:/auth/login";
    }
}