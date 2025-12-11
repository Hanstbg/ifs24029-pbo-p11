package org.delcom.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @GetMapping("/")
    public String root() {
        // Ini akan me-redirect user dari halaman awal (localhost:8080)
        // ke /dashboard yang sekarang diurus oleh DashboardController
        return "redirect:/dashboard";
    }

    // METHOD DASHBOARD DIHAPUS UNTUK MENGHINDARI ERROR "Ambiguous Mapping"
    // Karena URL "/dashboard" sudah dipakai oleh DashboardController.java

    @GetMapping("/admin")
    @ResponseBody
    public String admin() {
        return "Welcome Admin";
    }
}