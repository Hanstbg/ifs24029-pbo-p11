package org.delcom.app.controllers;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.Perjalanan;
import org.delcom.app.entities.User;
import org.delcom.app.services.PerjalananService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/perjalanan")
public class PerjalananController {

    @Autowired
    private PerjalananService perjalananService;

    @Autowired
    private AuthContext authContext; // Integrasi Auth yang sudah dibuat

    // Helper method untuk mendapatkan User ID yang sedang login
    private UUID getCurrentUserId() {
        User user = authContext.getAuthUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User belum login");
        }
        return user.getId();
    }

    // [2] Create
    @PostMapping
    public ResponseEntity<Perjalanan> createPerjalanan(@RequestBody Perjalanan perjalananBaru) {
        UUID userId = getCurrentUserId();
        Perjalanan result = perjalananService.tambahPerjalanan(userId, perjalananBaru);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    // [6] Read All
    @GetMapping
    public ResponseEntity<List<Perjalanan>> getAllPerjalanan() {
        UUID userId = getCurrentUserId();
        List<Perjalanan> list = perjalananService.getAllPerjalananByUserId(userId);
        return ResponseEntity.ok(list);
    }
    
    // [7] Read One (Detail)
    @GetMapping("/{id}")
    public ResponseEntity<Perjalanan> getDetailPerjalanan(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        Perjalanan perjalanan = perjalananService.getPerjalananById(userId, id);
        return ResponseEntity.ok(perjalanan);
    }

    // [3] Update Info
    @PutMapping("/{id}")
    public ResponseEntity<Perjalanan> updatePerjalanan(@PathVariable UUID id, @RequestBody Perjalanan updateData) {
        UUID userId = getCurrentUserId();
        Perjalanan updated = perjalananService.ubahPerjalanan(userId, id, updateData);
        return ResponseEntity.ok(updated);
    }
    
    // [4] Update Foto
    @PostMapping("/{id}/upload-foto")
    public ResponseEntity<Perjalanan> uploadFotoUtama(
            @PathVariable UUID id, 
            @RequestParam("file") MultipartFile file) {
        UUID userId = getCurrentUserId();
        Perjalanan updated = perjalananService.ubahFotoPerjalanan(userId, id, file);
        return ResponseEntity.ok(updated);
    }

    // [5] Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerjalanan(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        perjalananService.hapusPerjalanan(userId, id);
        return ResponseEntity.noContent().build();
    }
    
    // [8] Chart Data
    @GetMapping("/chart/lokasi")
    public ResponseEntity<?> getLokasiChartData() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(perjalananService.getChartDataByLokasi(userId));
    }
}