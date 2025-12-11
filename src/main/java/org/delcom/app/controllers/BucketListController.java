package org.delcom.app.controllers;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.BucketList;
import org.delcom.app.entities.User;
import org.delcom.app.services.BucketListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/bucketlist")
public class BucketListController {

    @Autowired
    private BucketListService bucketListService;

    @Autowired
    private AuthContext authContext;

    @GetMapping
    public String showBucketListPage(Model model) {
        User user = authContext.getAuthUser();
        if (user == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("bucketList", bucketListService.getAllByUser(user.getId()));
        
        // --- PERBAIKAN DISINI ---
        // Hapus "view/" jika file kamu ada di folder templates langsung
        return "bucketlist"; 
    }

    @PostMapping("/add")
    public String addItem(@RequestParam String destination, 
                          @RequestParam(required = false) String notes,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate targetDate) {
        
        User user = authContext.getAuthUser();
        if (user == null) return "redirect:/auth/login";

        BucketList item = new BucketList();
        item.setUser(user);
        item.setDestination(destination);
        item.setNotes(notes);
        item.setTargetDate(targetDate);
        
        bucketListService.addBucketItemObject(item); 
        
        return "redirect:/bucketlist";
    }

    @GetMapping("/toggle/{id}")
    public String toggleItemStatus(@PathVariable UUID id) {
        if (!authContext.isAuthenticated()) return "redirect:/auth/login";
        bucketListService.toggleStatus(id);
        return "redirect:/bucketlist";
    }

    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable UUID id) {
        if (!authContext.isAuthenticated()) return "redirect:/auth/login";
        bucketListService.deleteItem(id);
        return "redirect:/bucketlist";
    }
}