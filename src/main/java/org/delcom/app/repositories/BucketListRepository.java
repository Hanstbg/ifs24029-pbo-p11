package org.delcom.app.repositories;

import org.delcom.app.entities.BucketList;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BucketListRepository extends JpaRepository<BucketList, UUID> {
    List<BucketList> findByUserIdOrderByCreatedAtDesc(UUID userId);
}