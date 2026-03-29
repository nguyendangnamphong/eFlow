package com.vnu.uet.repository;

import com.vnu.uet.domain.MapForm;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MapForm entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MapFormRepository extends JpaRepository<MapForm, Long> {
    List<MapForm> findAllByNodeId(Long nodeId);
}
