package org.delcom.app.services;

import org.delcom.app.entities.BucketList;
import org.delcom.app.repositories.BucketListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class BucketListService {

    @Autowired
    private BucketListRepository bucketListRepository;

    public List<BucketList> getAllByUser(UUID userId) {
        return bucketListRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void addBucketItemObject(BucketList item) {
        bucketListRepository.save(item);
    }

    public void deleteItem(UUID id) {
        bucketListRepository.deleteById(id);
    }

    public void toggleStatus(UUID id) {
        // Menggunakan logic ini agar mudah ditest coverage-nya (Null vs Not Null)
        BucketList item = bucketListRepository.findById(id).orElse(null);
        if (item != null) {
            item.setAchieved(!item.isAchieved());
            bucketListRepository.save(item);
        }
    }
}