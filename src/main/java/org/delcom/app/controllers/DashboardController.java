package org.delcom.app.controllers;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.BucketList; // Import Entity Baru
import org.delcom.app.entities.Perjalanan;
import org.delcom.app.entities.User;
import org.delcom.app.services.BucketListService; // Import Service Baru
import org.delcom.app.services.FileStorageService;
import org.delcom.app.services.PerjalananService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private PerjalananService perjalananService;

    @Autowired
    private BucketListService bucketListService; // [BARU] Inject Service Bucket List

    @Autowired
    private AuthContext authContext;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = authContext.getAuthUser();
        if (user == null) {
            return "redirect:/auth/login";
        }

        try {
            // 1. Ambil Data List Perjalanan
            List<Perjalanan> listPerjalanan = perjalananService.getAllPerjalananByUserId(user.getId());
            if (listPerjalanan == null) listPerjalanan = new ArrayList<>();

            // 2. [BARU] Ambil Data Bucket List untuk Widget Sidebar
            List<BucketList> bucketList = bucketListService.getAllByUser(user.getId());
            if (bucketList == null) bucketList = new ArrayList<>();

            // 3. Ambil Data Chart
            Map<String, Object> serviceResult = perjalananService.getChartDataByLokasi(user.getId());
            
            List<String> chartLabels = new ArrayList<>();
            List<Long> chartValues = new ArrayList<>();

            if (serviceResult != null && serviceResult.containsKey("data_per_lokasi")) {
                List<Map<String, Object>> chartData = (List<Map<String, Object>>) serviceResult.get("data_per_lokasi");
                
                if (chartData != null) {
                    chartLabels = chartData.stream()
                            .map(m -> (String) m.get("lokasi"))
                            .collect(Collectors.toList());
                    
                    chartValues = chartData.stream()
                            .map(m -> (Long) m.get("jumlah"))
                            .collect(Collectors.toList());
                }
            }

            // --- Kirim Data ke View ---
            model.addAttribute("user", user);
            model.addAttribute("listPerjalanan", listPerjalanan);
            
            // [BARU] Kirim bucketList ke View agar Widget Sidebar muncul datanya
            model.addAttribute("bucketList", bucketList); 

            model.addAttribute("totalPerjalanan", listPerjalanan.size());
            
            Object totalLokasiObj = (serviceResult != null) ? serviceResult.get("total_data") : 0;
            model.addAttribute("totalLokasi", totalLokasiObj != null ? totalLokasiObj : 0);

            model.addAttribute("chartLabels", chartLabels);
            model.addAttribute("chartValues", chartValues);

            return "view/dashboard";

        } catch (Exception e) {
            e.printStackTrace();
            return "error"; // Pastikan file error.html ada, atau ganti return "redirect:/auth/login";
        }
    }

    @GetMapping("/dashboard/add")
    public String showAddForm(Model model) {
        if (!authContext.isAuthenticated()) return "redirect:/auth/login";
        model.addAttribute("perjalanan", new Perjalanan());
        model.addAttribute("isEdit", false);
        return "view/form-perjalanan";
    }

    @GetMapping("/dashboard/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        if (!authContext.isAuthenticated()) return "redirect:/auth/login";
        User user = authContext.getAuthUser();
        try {
            Perjalanan data = perjalananService.getPerjalananById(user.getId(), id);
            model.addAttribute("perjalanan", data);
            model.addAttribute("isEdit", true);
            return "view/form-perjalanan";
        } catch (Exception e) {
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/dashboard/save")
    public String savePerjalanan(@ModelAttribute Perjalanan perjalanan,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                 @RequestParam(value = "urlInput", required = false) String urlInput) {
        
        User user = authContext.getAuthUser();
        if (user == null) return "redirect:/auth/login";

        try {
            // Logika Upload Gambar
            if (imageFile != null && !imageFile.isEmpty()) {
                String filename = fileStorageService.storeFile(imageFile, user.getId());
                perjalanan.setFotoUtama(filename); 
            } else if (urlInput != null && !urlInput.trim().isEmpty()) {
                perjalanan.setFotoUtama(urlInput); 
            }
            
            // Simpan Data
            if (perjalanan.getId() != null) {
                perjalananService.ubahPerjalanan(user.getId(), perjalanan.getId(), perjalanan);
            } else {
                perjalananService.tambahPerjalanan(user.getId(), perjalanan);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard/delete/{id}")
    public String deletePerjalanan(@PathVariable UUID id) {
        User user = authContext.getAuthUser();
        if (user != null) perjalananService.hapusPerjalanan(user.getId(), id);
        return "redirect:/dashboard";
    }
}