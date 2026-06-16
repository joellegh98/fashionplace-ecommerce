package com.fashionplace.repository;

import com.fashionplace.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link ActivityLog} entries. Provides CRUD methods for free.
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    /**
     * Returns the most recent activity entries, newest first.
     *
     * @return up to 100 newest entries
     */
    List<ActivityLog> findTop100ByOrderByIdDesc();
}
