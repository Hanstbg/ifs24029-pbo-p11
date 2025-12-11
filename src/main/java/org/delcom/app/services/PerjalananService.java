package org.delcom.app.services;

import org.delcom.app.entities.Perjalanan;
import org.delcom.app.repositories.PerjalananRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PerjalananService {

    @Autowired
    private PerjalananRepository perjalananRepository;

    @Autowired(required = false)
    private FileStorageService fileStorageService;

    // [2] Tambah Data
    @Transactional
    public Perjalanan tambahPerjalanan(UUID userId, Perjalanan perjalananBaru) {
        if (perjalananBaru.getJudul() == null || perjalananBaru.getJudul().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Judul perjalanan wajib diisi.");
        }
        perjalananBaru.setUserId(userId);
        return perjalananRepository.save(perjalananBaru);
    }

    // [6] Read All
    public List<Perjalanan> getAllPerjalananByUserId(UUID userId) {
        // Method ini sekarang sudah ada di Repository (setelah diperbaiki di atas)
        return perjalananRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }
    
    // [7] Read One
    public Perjalanan getPerjalananById(UUID userId, UUID id) {
        return perjalananRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catatan perjalanan tidak ditemukan."));
    }

    // [3] Update Data
    @Transactional
    public Perjalanan ubahPerjalanan(UUID userId, UUID id, Perjalanan updateData) {
        Perjalanan existing = getPerjalananById(userId, id);

        existing.setJudul(updateData.getJudul());
        existing.setLokasi(updateData.getLokasi());
        existing.setDeskripsi(updateData.getDeskripsi());
        existing.setTanggalMulai(updateData.getTanggalMulai());
        existing.setTanggalSelesai(updateData.getTanggalSelesai());
        
        return perjalananRepository.save(existing);
    }
    
    // [4] Update Foto
    @Transactional
    public Perjalanan ubahFotoPerjalanan(UUID userId, UUID id, MultipartFile file) {
        Perjalanan existing = getPerjalananById(userId, id);

        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File tidak boleh kosong.");
        }

        try {
            String fileName;
            if (fileStorageService != null) {
                // --- PERBAIKAN DI SINI ---
                // Error sebelumnya: actual and formal argument lists differ in length
                // Solusi: Menambahkan parameter 'userId' karena storeFile butuh (File, UUID)
                fileName = fileStorageService.storeFile(file, userId); 
            } else {
                fileName = "dummy-url/" + file.getOriginalFilename();
            }
            
            existing.setFotoUtama(fileName);
            return perjalananRepository.save(existing);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Gagal upload file: " + e.getMessage());
        }
    }

    // [5] Delete
    @Transactional
    public void hapusPerjalanan(UUID userId, UUID id) {
        Perjalanan existing = getPerjalananById(userId, id);
        perjalananRepository.delete(existing);
    }
    
    // [8] Data Chart
    public Map<String, Object> getChartDataByLokasi(UUID userId) {
        List<Perjalanan> allData = perjalananRepository.findByUserId(userId);
        
        Map<String, Long> locationCounts = allData.stream()
            .filter(p -> p.getLokasi() != null && !p.getLokasi().isEmpty())
            .collect(Collectors.groupingBy(Perjalanan::getLokasi, Collectors.counting()));
        
        List<Map<String, Object>> chartList = new ArrayList<>();
        locationCounts.forEach((lokasi, jumlah) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("lokasi", lokasi);
            map.put("jumlah", jumlah);
            chartList.add(map);
        });

        Map<String, Object> result = new HashMap<>();
        result.put("data_per_lokasi", chartList);
        result.put("total_data", allData.size());
        
        return result;
    }
}