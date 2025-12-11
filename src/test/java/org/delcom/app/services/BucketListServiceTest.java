package org.delcom.app.services;

import org.delcom.app.entities.BucketList;
import org.delcom.app.repositories.BucketListRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BucketListServiceTest {

    @Mock
    private BucketListRepository bucketListRepository;

    @InjectMocks
    private BucketListService bucketListService;

    // --- 1. Test Get All By User ---
    @Test
    void testGetAllByUser() {
        UUID userId = UUID.randomUUID();
        List<BucketList> mockList = new ArrayList<>();
        mockList.add(new BucketList());

        when(bucketListRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(mockList);

        List<BucketList> result = bucketListService.getAllByUser(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bucketListRepository).findByUserIdOrderByCreatedAtDesc(userId);
    }

    // --- 2. Test Add Item ---
    @Test
    void testAddBucketItemObject() {
        BucketList item = new BucketList();
        item.setDestination("Mendaki Gunung"); 
        
        bucketListService.addBucketItemObject(item);

        verify(bucketListRepository, times(1)).save(item);
    }

    // --- 3. Test Delete Item ---
    @Test
    void testDeleteItem() {
        UUID itemId = UUID.randomUUID();

        bucketListService.deleteItem(itemId);

        verify(bucketListRepository, times(1)).deleteById(itemId);
    }

    // --- 4. Test Toggle Status (Logic Lengkap) ---

    // Case A: Data Tidak Ditemukan (Menghijaukan cabang IF False / Else)
    @Test
    void testToggleStatus_NotFound() {
        UUID itemId = UUID.randomUUID();

        // Mock return empty
        when(bucketListRepository.findById(itemId)).thenReturn(Optional.empty());

        bucketListService.toggleStatus(itemId);

        // Verify save TIDAK dipanggil
        verify(bucketListRepository, never()).save(any());
    }

    // Case B: Data Ditemukan, Status False -> True (Menghijaukan Boolean logic 1)
    @Test
    void testToggleStatus_Found_FalseToTrue() {
        UUID itemId = UUID.randomUUID();
        BucketList item = new BucketList();
        item.setId(itemId);
        item.setAchieved(false); // Awal False

        when(bucketListRepository.findById(itemId)).thenReturn(Optional.of(item));

        bucketListService.toggleStatus(itemId);

        // Assert jadi True
        assertTrue(item.isAchieved()); 
        verify(bucketListRepository).save(item);
    }

    // Case C: Data Ditemukan, Status True -> False (Menghijaukan Boolean logic 2 -> SISA BRANCH TERAKHIR)
    @Test
    void testToggleStatus_Found_TrueToFalse() {
        UUID itemId = UUID.randomUUID();
        BucketList item = new BucketList();
        item.setId(itemId);
        item.setAchieved(true); // Awal True

        when(bucketListRepository.findById(itemId)).thenReturn(Optional.of(item));

        bucketListService.toggleStatus(itemId);

        // Assert jadi False
        assertFalse(item.isAchieved()); 
        verify(bucketListRepository).save(item);
    }
}
